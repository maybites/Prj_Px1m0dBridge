package ch.maybites.px1m0d.plug.config;

import proxml.XMLElement;
import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.message.*;
import ch.maybites.px1m0d.plug.config.exception.MessageCreationException;
import ch.maybites.px1m0d.plug.config.exception.PlugConfigurationException;
import de.cnc.expression.*;
import de.cnc.expression.exceptions.*;
import java.io.*;

public class PipeMessageConfig implements MessageConfig, Serializable{
	private static final long serialVersionUID = 1L;

	private final int DATA_TYPE_ABS = 0;
	private final int DATA_TYPE_REL = 1;

	private final static String TAG_VALUE = "value";

	private final static String ATTR_CHANNEL = "channel";
	private final static String ATTR_TYPE = "type";

	private final static String ATTR_TYPE_VALUE_ABS = "abs";
	private final static String ATTR_TYPE_VALUE_REL = "rel";


	private String _myChannel, _myValueData;
	private int _myValueType;

	transient private XMLElement _myPlugElement;
	transient private Expression _myExpValueData;
	transient private Expression _myExpChannel;

	public PipeMessageConfig(XMLElement data) throws PlugConfigurationException{
		try{
			_myPlugElement = data;
			_myChannel = getPipeChannel();
			_myValueData = getValueData();
			if(this.isValueTypeABS()){
				_myValueType = DATA_TYPE_ABS;
			}else if(this.isValueTypeREL()){
				_myValueType = DATA_TYPE_REL;
			}
		} catch (proxml.InvalidAttributeException e){
			Debugger.getInstance().fatalMessage(this.getClass(), "Parsing Error in XML-Tree: ");
			data.printElementTree();
			throw new PlugConfigurationException(e.getMessage());
		}
	}

	public void parse(AbstractRuntimeEnvironment runEnv) throws ExpressionParseException{
		_myExpValueData = Expression.parse(_myValueData, runEnv);
		_myExpChannel = Expression.parse(_myChannel, runEnv);
	}

	public Message evaluate(AbstractRuntimeEnvironment runEnv) throws ExpressionEvaluationException, MessageCreationException{
		Object evaluation;
		int channel;
		float value;
		evaluation = _myExpChannel.eval(runEnv);
		if(evaluation instanceof Number){
			channel = ((Number)evaluation).intValue();
		} else {
			throw new MessageCreationException("Expression for PipeChannel: ("+ _myChannel +") does not return valid Number");
		}
		evaluation = _myExpValueData.eval(runEnv);
		if(evaluation instanceof Number){
			value = ((Number)evaluation).floatValue();
		} else {
			throw new MessageCreationException("Expression for PipeValue: (" + _myValueData + ") does not return valid Number");
		}

		Px1MessageInternalPipe message = new Px1MessageInternalPipe();	
		if(_myValueType == DATA_TYPE_ABS){
			message.createAbsoluteMessage(channel, value);
		}
		if(_myValueType == DATA_TYPE_REL){
			message.createRelativeMessage(channel, value);
		}
		return message;
	}

	private boolean hasPipeChannel(){
		return (_myPlugElement.hasAttribute(ATTR_CHANNEL))? true: false;
	}


	private String getPipeChannel(){
		return PlugConfig.translateBooleanChars(_myPlugElement.getAttribute(ATTR_CHANNEL));
	}

	private boolean hasValueTag(){
		return (getValueTag() != null)? true: false;
	}

	private XMLElement getValueTag(){
		XMLElement[] children = _myPlugElement.getChildren();
		for(int i = 0; i < children.length; i++){
			if(children[i].getName().equals(this.TAG_VALUE)){
				return children[i];
			}
		}
		return null;
	}

	private boolean hasValueTypeAttribute(){
		return (getValueTypeAttribute() != null)? true: false;
	}

	private String getValueTypeAttribute(){
		if(hasValueTag()){
			return getValueTag().getAttribute(ATTR_TYPE);
		}
		return null;
	}

	private boolean isValueTypeABS(){
		return (getValueTypeAttribute().equals(ATTR_TYPE_VALUE_ABS))? true: false;
	}

	private boolean isValueTypeREL(){
		return (getValueTypeAttribute().equals(ATTR_TYPE_VALUE_REL))? true: false;
	}

	private boolean hasValueData(){
		return (getValueData() != null)? true: false;
	}

	private String getValueData(){
		if(hasValueTag()){
			if(getValueTag().hasChildren()){
				return PlugConfig.translateBooleanChars(getValueTag().getChild(0).getText());
			}
		}
		return null;
	}

	public void print(){
		Debugger.getInstance().infoMessage(null, "                Pipe Message:");
		Debugger.getInstance().infoMessage(null, "                    Channel: " + _myChannel);
		Debugger.getInstance().infoMessage(null, "                    Type: " + _myValueType);
		Debugger.getInstance().infoMessage(null, "                    Value : " + _myValueData);
	}
}
