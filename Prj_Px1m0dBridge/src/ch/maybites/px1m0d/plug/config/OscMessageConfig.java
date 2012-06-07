package ch.maybites.px1m0d.plug.config;

import proxml.XMLElement;
import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.message.*;
import de.cnc.expression.*;
import de.cnc.expression.exceptions.*;
import oscP5.*;
import java.io.*;

import ch.maybites.px1m0d.plug.config.exception.MessageCreationException;
import ch.maybites.px1m0d.plug.config.exception.PlugConfigurationException;

public class OscMessageConfig implements MessageConfig, Serializable{
	private static final long serialVersionUID = 1L;

	private final int DATA_TYPE_UNDEFINED = 0;
	private final int DATA_TYPE_INT = 1;
	private final int DATA_TYPE_FLOAT = 2;
	private final int DATA_TYPE_STRING = 3;

	private final static String TAG_ADD_STRING_TO_OSC = "addOscString";
	private final static String TAG_ADD_INT_TO_OSC = "addOscInt";
	private final static String TAG_ADD_FLOAT_TO_OSC = "addOscFloat";
	private final static String TAG_OSC_ADDRESSPATTERN = "addressPattern";

	private final static String ATTR_OSC_IPADDRESS = "ipAddress";
	private final static String ATTR_OSC_IPADDRESSPORT = "port";

	private String _myOscIPAddress, _myOscAddrPattern;
	private int _myOscIPAddressPort;

	private String[] _myOscData;
	private int[] _myOscDataType;

	transient private XMLElement _myPlugElement;
	transient private Expression[] _myExpOscData;
	transient private Expression _myExpOscAddressPattern;

	private boolean parseSuccess;

	public OscMessageConfig(XMLElement data) throws PlugConfigurationException{
		parseSuccess = false;
		try{
			_myPlugElement = data;
			_myOscIPAddress = getOscIPAddress();
			_myOscAddrPattern = getOscAddrPattern();
			_myOscIPAddressPort = getOscIPAddressPort();
			storeOscData();
		} catch (Exception e){
			Debugger.getInstance().fatalMessage(this.getClass(), "Parsing Error in XML-Tree: ");
			data.printElementTree();
			throw new PlugConfigurationException(e.getMessage());
		}
	}

	public void parse(AbstractRuntimeEnvironment runEnv) throws ExpressionParseException{
		_myExpOscAddressPattern = Expression.parse(_myOscAddrPattern, runEnv);
		_myExpOscData = new Expression[_myOscData.length];
		for(int i = 0; i < _myOscData.length; i++){
			//if(_myOscDataType[i] != DATA_TYPE_STRING){
			_myExpOscData[i] = Expression.parse(_myOscData[i], runEnv);
			//}
		}
		parseSuccess = true;
	}

	public Message evaluate(AbstractRuntimeEnvironment runEnv) throws ExpressionEvaluationException, MessageCreationException{
		if(parseSuccess){
			Object evaluation;
			OscMessage msg;
			evaluation = _myExpOscAddressPattern.eval(runEnv);
			if(evaluation instanceof String){
				msg = new OscMessage((String)evaluation);
			} else {
				print();
				throw new MessageCreationException("Expression for OscAddressPattern does not return valid String - check for >'<!: " + _myExpOscAddressPattern.getOriginalSource());
			}
			for(int i = 0; i < _myExpOscData.length; i++){
				switch (_myOscDataType[i]){
				case DATA_TYPE_INT:
					evaluation = _myExpOscData[i].eval(runEnv);
					if(evaluation instanceof Number){
						msg.add(((Number)evaluation).intValue());
					} else {
						print();
						throw new MessageCreationException("Expression for OscDataType 'INT' does not return valid Number: " + _myExpOscData[i].getOriginalSource());
					}
					break;
				case DATA_TYPE_FLOAT:
					evaluation = _myExpOscData[i].eval(runEnv);
					if(evaluation instanceof Number){
						msg.add(((Number)evaluation).floatValue());
					} else {
						print();
						throw new MessageCreationException("Expression for OscDataType 'FLOAT' does not return valid Number: " + _myExpOscData[i].getOriginalSource());
					}
					break;
				case DATA_TYPE_STRING:
					evaluation = _myExpOscData[i].eval(runEnv);
					if(evaluation instanceof String){
						msg.add(((String)evaluation));
					} else {
						print();
						throw new MessageCreationException("Expression for OscDataType 'STRING' does not return valid String - check for >'<! : " + _myExpOscData[i].getOriginalSource());
					}
					//msg.add(_myOscDataType[i]);		
					break;
				}		                      
			}
			Px1MessageOsc message = new Px1MessageOsc(msg, _myOscIPAddress, _myOscIPAddressPort);	

			return message;
		}
		throw new MessageCreationException("ExpressionParsing Failed. No Message can be created for: " + _myOscIPAddress + ":" + _myOscIPAddressPort + ":" + _myOscAddrPattern);
	}

	private String getOscIPAddress(){
		return _myPlugElement.getAttribute(ATTR_OSC_IPADDRESS);
	}

	private int getOscIPAddressPort() throws PlugConfigurationException{
		if(_myPlugElement.hasAttribute(ATTR_OSC_IPADDRESSPORT) && 
				!_myPlugElement.getAttribute(ATTR_OSC_IPADDRESS).equals(Px1MessageOsc.DEFAULT_IP_ADDRESS)){
			return (new Integer(_myPlugElement.getAttribute(ATTR_OSC_IPADDRESSPORT))).intValue();		
		}if(_myPlugElement.getAttribute(ATTR_OSC_IPADDRESS).equals(Px1MessageOsc.DEFAULT_IP_ADDRESS)){
			return 0;
		}
		throw new PlugConfigurationException("Attribute port is missing");
	}

	private String getOscAddrPattern() throws PlugConfigurationException{
		XMLElement[] children = _myPlugElement.getChildren();
		for(int i = 0; i < children.length; i++){
			if(children[i].getName().equals(TAG_OSC_ADDRESSPATTERN))
				return children[i].getChild(0).getText();
		}
		throw new PlugConfigurationException("AddressPattern Tag is missing");
	}

	private void storeOscData() throws PlugConfigurationException{
		XMLElement[] children = _myPlugElement.getChildren();
		_myOscData = new String[getOscDataChildren()];
		_myOscDataType = new int[getOscDataChildren()];
		int count = 0;
		for(int i = 0; i < children.length; i++){
			if(children[i].getName().equals(TAG_ADD_FLOAT_TO_OSC)){
				_myOscDataType[count] = DATA_TYPE_FLOAT;
				_myOscData[count] = (PlugConfig.translateBooleanChars(children[i].getChild(0).getText()));
				count++;
			} else if(children[i].getName().equals(TAG_ADD_STRING_TO_OSC)){
				_myOscDataType[count] = DATA_TYPE_STRING;
				_myOscData[count] = (PlugConfig.translateBooleanChars(children[i].getChild(0).getText()));
				count++;
			} else if(children[i].getName().equals(TAG_ADD_INT_TO_OSC)){
				_myOscDataType[count] = DATA_TYPE_INT;
				_myOscData[count] = (PlugConfig.translateBooleanChars(children[i].getChild(0).getText()));
				count++;
			} else if(children[i].getName().equals(TAG_OSC_ADDRESSPATTERN)){
				;
			} else {
				throw new PlugConfigurationException("Unkown Tag: " + children[i].getName());
			}
		}	
	}
	
	private int getOscDataChildren(){
		XMLElement[] children = _myPlugElement.getChildren();
		int count = 0;
		for(int i = 0; i < children.length; i++){
			if(children[i].getName().equals(TAG_ADD_FLOAT_TO_OSC) || 
					children[i].getName().equals(TAG_ADD_STRING_TO_OSC) || 
					children[i].getName().equals(TAG_ADD_INT_TO_OSC)){
				count++;
			}
		}	
		return count;
	}

	public void print(){
		Debugger.getInstance().infoMessage(null, "                Osc Message:");
		Debugger.getInstance().infoMessage(null, "                    Adress: " + _myOscIPAddress);
		Debugger.getInstance().infoMessage(null, "                    Path: " + _myOscAddrPattern);
		for(int i = 0; i < _myOscData.length; i++){
			Debugger.getInstance().infoMessage(null, "                     Value[" + i + "] : " + _myOscData[i]);
		}
	}

}
