package ch.maybites.px1m0d.plug.config;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.message.Message;
import ch.maybites.px1m0d.plug.*;
import ch.maybites.px1m0d.plug.config.exception.MessageCreationException;
import ch.maybites.px1m0d.plug.config.exception.PlugConfigurationException;
import de.cnc.expression.AbstractRuntimeEnvironment;
import de.cnc.expression.Expression;
import de.cnc.expression.exceptions.ExpressionEvaluationException;
import de.cnc.expression.exceptions.ExpressionParseException;
import proxml.XMLElement;
import java.io.*;

public class JobConfig implements Serializable{
	private static final long serialVersionUID = 1L;

	private final static String UNDEFINED = "undefined";

	private final static String TAG_MIDI = "midi";
	private final static String TAG_OSC = "osc";
	private final static String TAG_PIPE = "pipe";

	private final static String TAG_JOB = "job";
	private final static String TAG_MESSAGE = "message";

	private final static String TAG_TARGET = "target";
	private final static String TAG_SENDTOWORLD = "sendToWorld";
	private final static String TAG_SENDTOSOCKETID = "sendToSocketID";
	private final static String TAG_SENDTOPLUGID = "sendToPlugID";
	private final static String TAG_SENDTOROW = "sendToRow";
	private final static String TAG_SENDTOCOLLUM = "sendToCollum";
	private final static String TAG_SENDTONEIGHBOURS = "sendToNeighbours";
	private final static String TAG_SENDTOBOARD = "sendToBoard";

	private final static String ATTR_JOB_ID = "id";
	private final static String ATTR_JOB_NAME = "name";
	private final static String ATTR_JOB_DESCRIPT = "description";
	private final static String ATTR_JOB_TRIGGER = "trigger";

	private final static int TARGET_UNDEFINED = 0;
	private final static int TARGET_PLUGID = 1;
	private final static int TARGET_SOCKETID = 2;
	private final static int TARGET_WORLD = 3;
	private final static int TARGET_ROW = 4;
	private final static int TARGET_COLLUM = 5;
	private final static int TARGET_NEIGHBOURS = 6;
	private final static int TARGET_BOARD = 7;

	public int ID;
	public String Name, Description, Trigger;

	private int _myTargetType = TARGET_WORLD;
	private String _myTarget = UNDEFINED;

	private MessageConfig[] _myMessages;

	transient private Expression _myExpTrigger, _myExpTarget;
	transient private XMLElement _myPlugElement;

	public JobConfig(XMLElement data) throws PlugConfigurationException{
		try{
			_myPlugElement = data;
			ID = (hasJobID())? getJobID(): 0;
			Name = (hasJobName())? getJobName(): "";
			Description = (hasJobDescription())?getJobDescription(): "";
			Trigger = getJobTrigger();

			_myTargetType = TARGET_UNDEFINED;
			_myTarget = "0";

			if(hasTarget()){
				if(this.isPlugIDTarget()){
					_myTargetType = TARGET_PLUGID;
					_myTarget = this.getTargetPlugID();
				}else if(this.isSocketIDTarget()){
					_myTargetType = TARGET_SOCKETID;
					_myTarget = this.getTargetSocketID();
				}else if(this.isRowTarget()){
					_myTargetType = TARGET_ROW;
					_myTarget = this.getTargetRow();
				}else if(this.isCollumTarget()){
					_myTargetType = TARGET_COLLUM;
					_myTarget = this.getTargetCollum();
				}else if(this.isNeighboursTarget()){
					_myTargetType = TARGET_NEIGHBOURS;
					_myTarget = this.getTargetNeighbours();
				}else if(this.isBoardTarget()){
					_myTargetType = TARGET_BOARD;
					_myTarget = this.getTargetBoard();
				}else if(this.isWorldTarget()){
					_myTargetType = TARGET_WORLD;
					_myTarget = "-1";
				}else{
					_myTargetType = TARGET_UNDEFINED;
					_myTarget = "-1";
				}
			}

			int numberOfMessageTypes = countMessageTypes();
			_myMessages = new MessageConfig[numberOfMessageTypes];
			for(int i = 0; i < numberOfMessageTypes; i++){
				XMLElement job = getMessageType(i);
				if(this.isOSCJob(job)){
					_myMessages[i] = new OscMessageConfig(getMessageType(i));
				}
				if(this.isMidiJob(job)){
					_myMessages[i] = new MidiMessageConfig(getMessageType(i));
				}
				if(this.isPipeJob(job)){
					_myMessages[i] = new PipeMessageConfig(getMessageType(i));
				}
			}
			
		} catch (proxml.InvalidAttributeException e){
			Debugger.getInstance().fatalMessage(this.getClass(), "Parsing Error in XML-Tree: ");
			data.printElementTree();
			throw new PlugConfigurationException(e.getMessage());
		}
	}

	public void parse(AbstractRuntimeEnvironment runEnv) throws ExpressionParseException{
		_myExpTrigger = Expression.parse(Trigger, runEnv);
		_myExpTarget = Expression.parse(_myTarget, runEnv);
		for(int i = 0; i < _myMessages.length; i++){
			_myMessages[i].parse(runEnv);
		}
	}

	public Message[] evaluate(AbstractRuntimeEnvironment runEnv, PlugParameter parameter, boolean enforceTrigger) throws ExpressionEvaluationException, MessageCreationException{
		Object evaluation = _myExpTrigger.eval(runEnv);
		if(evaluation instanceof Boolean){
			if(((Boolean)_myExpTrigger.eval(runEnv)).booleanValue() || enforceTrigger){
				Message[] msg = new Message[_myMessages.length];
				evaluation = _myExpTarget.eval(runEnv);
				if(evaluation instanceof Number){
					int target = ((Number)evaluation).intValue();
					for(int i = 0; i < _myMessages.length; i++){
						msg[i] = _myMessages[i].evaluate(runEnv);
						switch(_myTargetType){
						case TARGET_WORLD:
							msg[i].routeExternalyToWorld();
							break;
						case TARGET_PLUGID:
							msg[i].routeInternalyToPlugID(target);	
							break;
						case TARGET_SOCKETID:
							msg[i].routeInternalyToSocketID(target);	
							break;
						case TARGET_ROW:
							msg[i].routeInternalyToRow(target);	
							break;
						case TARGET_COLLUM:
							msg[i].routeInternalyToCol(target);	
							break;
						case TARGET_NEIGHBOURS:
							msg[i].routeInternalyToNeighbour(target);	
							break;
						case TARGET_BOARD:
							msg[i].routeInternalyToBoard(target);	
							break;
						default:
							msg[i].routeExternalyToWorld();
							//Debugger.getInstance().infoMessage(getClass(), "Message: no message target specified!");
						}
					}
				} else {
					throw new MessageCreationException("The Trigger Expression '"+_myTarget+"' is not returning a Number Value.");
				}
				return msg;
			}
		} else {
			throw new MessageCreationException("The Trigger Expression '"+Trigger+"' is not returning a Boolean Value.");
		}
		return null;
	}

	private XMLElement getJob(){
		if(_myPlugElement.getName().equals(TAG_JOB)){
			return _myPlugElement;
		}
		return null;
	}

	private boolean hasJobID(){
		return getJob().hasAttribute(ATTR_JOB_ID);
	}

	private int getJobID(){
		return (new Integer(getJob().getAttribute(ATTR_JOB_ID))).intValue();
	}

	private boolean hasJobName(){
		return getJob().hasAttribute(ATTR_JOB_NAME);
	}

	private String getJobName(){
		return getJob().getAttribute(ATTR_JOB_NAME);
	}

	private boolean hasJobDescription(){
		return getJob().hasAttribute(ATTR_JOB_DESCRIPT);
	}

	private String getJobDescription(){
		return getJob().getAttribute(ATTR_JOB_DESCRIPT);
	}

	private String getJobTrigger(){
		return PlugConfig.translateBooleanChars(getMessage().getAttribute(ATTR_JOB_TRIGGER));
	}

	private boolean hasMessage(){
		return (getMessage() != null)? true: false;
	}

	private XMLElement getMessage(){
		XMLElement[] children = getJob().getChildren();
		for(int i = 0; i < children.length; i++){
			if(children[i].getName().equals(TAG_MESSAGE)){
				return children[i];
			}
		}
		return null;
	}

	private boolean hasMessageType(){
		return (getMessageType() != null)? true: false;
	}

	private int countMessageTypes(){
		XMLElement[] children = getMessage().getChildren();
		int count = 0;
		for(int i = 0; i < children.length; i++){
			if(children[i].getName().equals(TAG_MIDI) || children[i].getName().equals(TAG_OSC) || children[i].getName().equals(TAG_PIPE)){
				count++;
			}
		}
		return count;
	}

	private XMLElement getMessageType(int pos){
		XMLElement[] children = getMessage().getChildren();
		int count = 0;
		for(int i = 0; i < children.length; i++){
			if(children[i].getName().equals(TAG_MIDI) || children[i].getName().equals(TAG_OSC) || children[i].getName().equals(TAG_PIPE)){
				if(count == pos)
					return children[i];
				count++;
			}
		}
		return null;
	}

	private XMLElement getMessageType(){
		XMLElement[] children = getMessage().getChildren();
		for(int i = 0; i < children.length; i++){
			if(children[i].getName().equals(TAG_MIDI)){
				return children[i];
			}else if(children[i].getName().equals(TAG_OSC)){
				return children[i];
			}else if(children[i].getName().equals(TAG_PIPE)){
				return children[i];
			}
		}
		return null;
	}

	private boolean isMidiJob(XMLElement job){
		return (job.getName().equals(TAG_MIDI))? true: false;
	}
	
	private boolean isOSCJob(XMLElement job){
		return (job.getName().equals(TAG_OSC))? true: false;
	}

	private boolean isPipeJob(XMLElement job){
		return (job.getName().equals(TAG_PIPE))? true: false;
	}

	
	private boolean isMidiJob(){
		if(hasMessageType())
			if(getMessageType().getName().equals(TAG_MIDI))
				return true;
		return false;
	}

	private boolean isOSCJob(){
		if(hasMessageType())
			if(getMessageType().getName().equals(TAG_OSC))
				return true;
		return false;
	}

	private boolean isPipeJob(){
		if(hasMessageType())
			if(getMessageType().getName().equals(TAG_PIPE))
				return true;
		return false;
	}

	private boolean hasTarget(){
		return (getTarget() != null);
	}

	private XMLElement getTarget(){
		XMLElement[] children = getMessage().getChildren();
		for(int i = 0; i < children.length; i++){
			if(children[i].getName().equals(TAG_TARGET)){
				return children[i].getChild(0);
			}
		}
		return null;
	}

	private boolean isWorldTarget(){
		if(hasTarget())
			if(getTarget().getName().equals(TAG_SENDTOWORLD))
				return true;
		return false;
	}

	private boolean isSocketIDTarget(){
		if(hasTarget())
			if(getTarget().getName().equals(TAG_SENDTOSOCKETID))
				return true;
		return false;
	}

	private String getTargetSocketID(){
		if(isSocketIDTarget()){
			return PlugConfig.translateBooleanChars(getTarget().getChild(0).getText());
		}
		return null;
	}

	private boolean isRowTarget(){
		if(hasTarget())
			if(getTarget().getName().equals(TAG_SENDTOROW))
				return true;
		return false;
	}

	private String getTargetRow(){
		if(isRowTarget()){
			return PlugConfig.translateBooleanChars(getTarget().getChild(0).getText());
		}
		return null;
	}

	private boolean isCollumTarget(){
		if(hasTarget())
			if(getTarget().getName().equals(TAG_SENDTOCOLLUM))
				return true;
		return false;
	}

	private String getTargetCollum(){
		if(isCollumTarget()){
			return PlugConfig.translateBooleanChars(getTarget().getChild(0).getText());
		}
		return null;
	}

	private boolean isNeighboursTarget(){
		if(hasTarget())
			if(getTarget().getName().equals(TAG_SENDTONEIGHBOURS))
				return true;
		return false;
	}

	private String getTargetNeighbours(){
		if(isNeighboursTarget()){
			return PlugConfig.translateBooleanChars(getTarget().getChild(0).getText());
		}
		return null;
	}

	private boolean isBoardTarget(){
		if(hasTarget())
			if(getTarget().getName().equals(TAG_SENDTOBOARD))
				return true;
		return false;
	}

	private String getTargetBoard(){
		if(isBoardTarget()){
			return PlugConfig.translateBooleanChars(getTarget().getChild(0).getText());
		}
		return null;
	}

	private boolean isPlugIDTarget(){
		if(hasTarget())
			if(getTarget().getName().equals(TAG_SENDTOPLUGID))
				return true;
		return false;
	}

	private String getTargetPlugID(){
		if(isPlugIDTarget()){
			return PlugConfig.translateBooleanChars(getTarget().getChild(0).getText());
		}
		return null;
	}

	public void print(){
		Debugger.getInstance().infoMessage(null, "           Job Data:");
		Debugger.getInstance().infoMessage(null, "                ID: " + ID);
		Debugger.getInstance().infoMessage(null, "                Name: " + Name);
		Debugger.getInstance().infoMessage(null, "                Description: " + Description);
		Debugger.getInstance().infoMessage(null, "                Trigger: " + Trigger);
		if(hasTarget()){
			if(this.isPlugIDTarget()){
				Debugger.getInstance().infoMessage(null, "                Target: <sendToPlugID>: "+ _myTarget);
			}else if(this.isSocketIDTarget()){
				Debugger.getInstance().infoMessage(null, "                Target: <sendToSocketID>:"+ _myTarget);
			}else if(this.isRowTarget()){
				Debugger.getInstance().infoMessage(null, "                Target: <sendToRow>: "+ _myTarget);
			}else if(this.isCollumTarget()){
				Debugger.getInstance().infoMessage(null, "                Target: <sendToCollum>: "+ _myTarget);
			}else if(this.isNeighboursTarget()){
				Debugger.getInstance().infoMessage(null, "                Target: <sendToNeighbours>: "+ _myTarget);
			}else if(this.isBoardTarget()){
				Debugger.getInstance().infoMessage(null, "                Target: <sendToBoard>: "+ _myTarget);
			}else if(this.isWorldTarget()){
				Debugger.getInstance().infoMessage(null, "                Target: <sendToWorld/>");
			}
		} else {
			Debugger.getInstance().infoMessage(null, "                Target: UNDEFINED: will be sent to World");
			
		}
		if(hasMessage()){
			for(int i = 0; i < _myMessages.length; i++)
				_myMessages[i].print();
		}
	}

}
