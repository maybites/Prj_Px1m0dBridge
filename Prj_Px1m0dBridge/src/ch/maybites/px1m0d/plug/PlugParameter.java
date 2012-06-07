package ch.maybites.px1m0d.plug;

import de.cnc.expression.*;
import de.cnc.expression.exceptions.*;

import java.util.*;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.config.PlugConfig;
import ch.maybites.px1m0d.message.*;
import ch.maybites.px1m0d.plug.drawing.*;
import java.io.*;
import gestalt.shape.Color;

public class PlugParameter implements Serializable{
	private static final long serialVersionUID = 1L;

	private static final String VAR_LINK_SOCKETID 			= "LinkSocketID";
	private static final String VAR_LINK_PLUGID 			= "LinkPlugID";
	private static final String VAR_LINK_PLUGNAME 			= "LinkPlugName";
	private static final String VAR_LINK_DIRECTION			= "LinkDirection";
	private static final String VAR_LINK_X_DIRECTION		= "LinkXDirection";
	private static final String VAR_LINK_Y_DIRECTION		= "LinkYDirection";
	private static final String VAR_LINK_X_POS				= "LinkXPos";
	private static final String VAR_LINK_Y_POS				= "LinkYPos";
	private static final String VAR_LINK_PLUGISPLUGGED	 	= "LinkPlugIsPlugged";
	private static final String VAR_LINK_PLUGISUNPLUGGED	= "LinkPlugIsUnPlugged";
	private static final String VAR_LINK_PLUGGINGISTOGGLED	= "LinkPluggingIsToggled";

	private static final String VAR_LOCAL_SOCKETID 			= "LocalSocketID";
	private static final String VAR_LOCAL_PLUGID 			= "LocalPlugID";
	private static final String VAR_LOCAL_PLUGNAME 			= "LocalPlugName";
	private static final String VAR_LOCAL_DIRECTION			= "LocalDirection";
	private static final String VAR_LOCAL_X_DIRECTION		= "LocalXDirection";
	private static final String VAR_LOCAL_Y_DIRECTION		= "LocalYDirection";
	private static final String VAR_LOCAL_X_POS				= "LocalXPos";
	private static final String VAR_LOCAL_Y_POS				= "LocalYPos";

	private static final String VAR_LOCAL_JUST_CREATED		= "LocalPlugJustCreated";
	private static final String VAR_LOCAL_JUST_DESTROYED 	= "LocalPlugJustDestroyed";

	private static final String VAR_LOCAL_BUTTONISPRESSED 	= "LocalButtonIsPressed";
	private static final String VAR_LOCAL_BUTTONISRELEASED 	= "LocalButtonIsReleased";
	private static final String VAR_LOCAL_BUTTONISTOGGLED	= "LocalButtonIsToggled";
	private static final String VAR_LOCAL_SWITCHISON 		= "LocalSwitchIsOn";
	private static final String VAR_LOCAL_SWITCHISOFF 		= "LocalSwitchIsOff";
	private static final String VAR_LOCAL_SWITCHISTOGGLED	= "LocalSwitchIsToggled";
	private static final String VAR_LOCAL_LAMPISON 			= "LocalLampIsOn";
	private static final String VAR_LOCAL_LAMPISOFF 		= "LocalLampIsOff";	
	private static final String VAR_LOCAL_PLUGISPLUGGED	 	= "LocalPlugIsPlugged";
	private static final String VAR_LOCAL_PLUGISUNPLUGGED	= "LocalPlugIsUnPlugged";
	private static final String VAR_LOCAL_PLUGGINGISTOGGLED	= "LocalPluggingIsToggled";

	private static final String VAR_LOCAL_PLUGISLINKED	 	= "LocalPlugIsLinked";
	private static final String VAR_LOCAL_PLUGGOTLINKED		= "LocalPlugGotLinked";
	private static final String VAR_LOCAL_PLUGGOTUNLINKED	= "LocalPlugGotUnLinked";

	private static final String VAR_LOCAL_METRONOME_HASCLICKED = "LocalMetronomeHasClicked";

	private static final String VAR_LOCAL_NATEBUVALUE		= "LocalNatebuValue";
	private static final String VAR_LOCAL_NATEBUVELOCITY	= "LocalNatebuVelocity";
	private static final String VAR_LOCAL_NATEBUACCELERATION= "LocalNatebuAcceleration";
	private static final String VAR_LOCAL_NATEBUVALUES_HASCHANGED = "LocalNatebuValueHasChanged";

	private static final String VAR_LOCAL_NATEBUWEIGHT		 = "NatebuWeight";
	private static final String VAR_LOCAL_NATEBUWEIGHT_POS_X = "NatebuWeightPosX";
	private static final String VAR_LOCAL_NATEBUWEIGHT_POS_Y = "NatebuWeightPosY";
	private static final String VAR_LOCAL_NATEBUWEIGHT_HASCHANGED = "NatebuWeightHasChanged";

	private static final String VAR_LOCAL_PIPEVALUE_HASCHANGED= "LocalPipeValueHasChanged";

	private static final String[] VAR_LOCAL_PIPE_XX_ISUSED = 
	{	"LocalPipe00IsUsed", 
		"LocalPipe01IsUsed",
		"LocalPipe02IsUsed",
		"LocalPipe03IsUsed",
		"LocalPipe04IsUsed",
		"LocalPipe05IsUsed",
		"LocalPipe06IsUsed",
		"LocalPipe07IsUsed",
	"LocalPipe08IsUsed"};

	private static final String[] VAR_LOCAL_PIPEVALUE_XX_CHANGED = 
	{	"LocalPipeValue00HasChanged", 
		"LocalPipeValue01HasChanged",
		"LocalPipeValue02HasChanged",
		"LocalPipeValue03HasChanged",
		"LocalPipeValue04HasChanged",
		"LocalPipeValue05HasChanged",
		"LocalPipeValue06HasChanged",
		"LocalPipeValue07HasChanged",
	"LocalPipeValue08HasChanged"};

	private static final String[] VAR_LOCAL_PIPEVALUE = 
	{	"LocalPipeValue00", 
		"LocalPipeValue01",
		"LocalPipeValue02",
		"LocalPipeValue03",
		"LocalPipeValue04",
		"LocalPipeValue05",
		"LocalPipeValue06",
		"LocalPipeValue07",
	"LocalPipeValue08"};

	private static final String[] VAR_NATEBU_BOTTOMSWITCH = 
	{	"NatebuBottomSwitch00",
		"NatebuBottomSwitch01",
		"NatebuBottomSwitch02",
		"NatebuBottomSwitch03",
		"NatebuBottomSwitch04",
		"NatebuBottomSwitch05",
		"NatebuBottomSwitch06",
	"NatebuBottomSwitch07"};

	private static final String[] VAR_NATEBU_RIGHTSWITCH	= 
	{	"NatebuRightSwitch00",
		"NatebuRightSwitch01",
		"NatebuRightSwitch02",
		"NatebuRightSwitch03",
		"NatebuRightSwitch04",
		"NatebuRightSwitch05",
		"NatebuRightSwitch06",
	"NatebuRightSwitch07"};

	private static final String VAR_NATEBU_LEFTSELECTOR_HAS_CHANGED		= "NatebuLeftSelectorHasChanged";
	private static final String VAR_NATEBU_RIGHTSELECTOR_HAS_CHANGED	= "NatebuRightSelectorHasChanged";
	private static final String VAR_NATEBU_LEFTPOTI_HAS_CHANGED			= "NatebuLeftPotiHasChanged";
	private static final String VAR_NATEBU_RIGHTPOTI_HAS_CHANGED		= "NatebuRightPotiHasChanged";
	private static final String VAR_NATEBU_CENTERBUTTON_HAS_CHANGED		= "NatebuCenterButtonHasChanged";
	private static final String VAR_NATEBU_RIGHTSWITCH_HAS_CHANGED		= "NatebuRightSwitchHasChanged";
	private static final String VAR_NATEBU_BOTTOMSWITCH_HAS_CHANGED		= "NatebuBottomSwitchHasChanged";
	private static final String VAR_NATEBU_A_INPUT_HAS_CHANGED			= "NatebuAInputHasChanged";
	private static final String VAR_NATEBU_B_INPUT_HAS_CHANGED			= "NatebuBInputHasChanged";

	private static final String VAR_NATEBU_UI_HAS_CHANGED		= "NatebuUIHasChanged";

	private static final String VAR_NATEBU_SELECTOR_LEFT	= "NatebuSelectorLeft";
	private static final String VAR_NATEBU_SELECTOR_RIGHT	= "NatebuSelectorRight";

	private static final String VAR_NATEBU_POTI_LEFT	= "NatebuPotiLeft";
	private static final String VAR_NATEBU_POTI_RIGHT	= "NatebuPotiRight";

	private static final String VAR_NATEBU_CENTRAL_BUTTON_IS_PRESSED = "NatebuCentralButtonIsPressed";

	private static final String VAR_NATEBU_PLUG_A_INPUT_1_IS_ON = "NatebuPlugAInput1IsOn";
	private static final String VAR_NATEBU_PLUG_A_INPUT_2_IS_ON = "NatebuPlugAInput2IsOn";
	private static final String VAR_NATEBU_PLUG_B_INPUT_1_IS_ON = "NatebuPlugBInput1IsOn";
	private static final String VAR_NATEBU_PLUG_B_INPUT_2_IS_ON = "NatebuPlugBInput2IsOn";

	private Integer[] _myNatebuSwitchBottom;
	private Integer[] _myNatebuSwitchRight;

	private Integer _myNatebuSelectorLeft;
	private Integer _myNatebuSelectorRight;
	private Integer _myNatebuPotiLeft;
	private Integer _myNatebuPotiRight;

	private Boolean _myNatebuUIHasChanged;
	private Boolean _myNatebuCentralButtonHasChanged;
	private Boolean _myNatebuLeftSelectorHasChanged;
	private Boolean _myNatebuRightSelectorHasChanged;
	private Boolean _myNatebuLeftPotiHasChanged;
	private Boolean _myNatebuRightPotiHasChanged;
	private Boolean _myNatebuRightSwitchHasChanged;
	private Boolean _myNatebuBottomSwitchHasChanged;
	private Boolean _myNatebuAInputHasChanged;
	private Boolean _myNatebuBInputHasChanged;

	private Boolean _myNatebuCentralButtonIsPressed;
	private Boolean _myNatebuPlugAInput1IsOn;
	private Boolean _myNatebuPlugAInput2IsOn;
	private Boolean _myNatebuPlugBInput1IsOn;
	private Boolean _myNatebuPlugBInput2IsOn;

	private Integer _mySocketId;
	private Integer _myPlugId;
	private Integer _myDirection;
	private Integer _myXDirection;
	private Integer _myYDirection;
	private Integer _myXPos;
	private Integer _myYPos;

	private Boolean _myselfJustCreated;
	private Boolean _myselfJustDestroyed;

	private Boolean _myButtonIsPressed;
	private Boolean _myButtonIsReleased;
	private Boolean _myButtonIsToggled;
	private Boolean _mySwitchIsOn;
	private Boolean _mySwitchIsOff;
	private Boolean _mySwitchIsToggled;
	private Boolean _myLampIsOn;
	private Boolean _myLampIsOff;

	private Boolean _myPlugIsLinked;
	private Boolean _myPlugGotLinked;
	private Boolean _myPlugGotUnLinked;

	private Boolean _myPlugIsPlugged;
	private Boolean _myPlugIsUnPlugged;
	private Boolean _myPluggingIsToggled;
	private Boolean _myLinkPluggingIsToggled;

	private Float _myPipeValue[];
	private Boolean _myPipeXXIsUsed[];
	private Boolean _myPipeValueXXHasChanged[];
	private Boolean _myPipeValueHasChanged;

	private Boolean _myMetronomeHasClicked;

	private Boolean _myNatebuValueHasChanged;
	private Float _myNatebuValue;
	private Float _myNatebuVelocity;
	private Float _myNatebuAcceleration;

	private Float _myNatebuWeight;
	private Float _myNatebuWeightPosX;
	private Float _myNatebuWeightPosY;
	private Boolean _myNatebuWeightHasChanged;

	private Plug _myLink;

	transient private AbstractRuntimeEnvironment _myRunEnv;
	transient private Vector<AbstractRuntimeEnvironment> _myLinkedEnv;

	private PlugConfig _myConfiguration;

	public PlugParameter(PlugConfig config){
		_myConfiguration = config;
		_myRunEnv = new StandaloneRuntimeEnvironment();
		_myPipeValue = new Float[9];
		_myPipeXXIsUsed = new Boolean[9];
		_myPipeValueXXHasChanged = new Boolean[9];

		setSocketID(0);
		setPlugID(0);
		setDirection(0);
		setPosition(0, 0);
		setButton(0);
		setIsUnPlugged();
		setLamp(0);
		unLink();
		for(int i = 0; i < _myPipeValue.length; i++){
			setPipeValue(i, 0);
			setPipeUsedConndition(i, false);
		}
		clearNatebuInterface();
		setNatebuValue(0);
		setSwitch(false);
		setMetronomeClick(false);
		setNatebuWeight(0f,0f,0f);
		setLinkedPluggedToggle();
		previousActionReset();
		
		callOnCreation(); // has to be called after the previousActionReset() - function!!

		parse();
	}

	public void refreshConfiguration(PlugConfig config){
		_myConfiguration = config;
		LocalEnvRefresh();
		parse();
	}


	private void parse(){
		try{
			_myConfiguration.parse(_myRunEnv);
		}catch(ExpressionParseException e){
			Debugger.getInstance().errorMessage(getClass(), "Unable to Parse Expressions of PlugConfiguration from Plug: " + _myConfiguration.Name);
			Debugger.getInstance().errorMessage(getClass(), e.getMessage());
		}		
	}


	public Vector<Message> MessageFactory(){
		return MessageFactory(false);
	}

	public Vector<Message> MessageFactory(boolean enforceTrigger){
		return _myConfiguration.evaluate(_myRunEnv, this, enforceTrigger);
	}
	
	protected void executeMessages(Socket thisPlugsSocket, boolean enforceTrigger){
		Vector<Message> messages = this.MessageFactory(enforceTrigger);
		for(int i = 0; i < messages.size(); i++){
			//Debugger.getInstance().debugMessage(this.getClass(), "plug is sending midi message: " + myPlug.parameter.getPlugID() + " values: "  + ((javax.sound.midi.ShortMessage)((Message)messages.get(i)).getValue(Message.KEY_MidiMessage)).getData2());
			thisPlugsSocket.outgoing(messages.get(i));
		}
	}
				
	private void callOnCreation(){
		_myselfJustCreated = new Boolean(true);
		_myselfJustDestroyed = new Boolean(false);
		setLocalEnvVariable(VAR_LOCAL_JUST_CREATED, _myselfJustCreated);
		setLocalEnvVariable(VAR_LOCAL_JUST_DESTROYED, _myselfJustDestroyed);
		setLocalEnvVariable(VAR_LOCAL_PLUGNAME, _myConfiguration.Name);
	}
	
	/*
	 * The method should be called by the plug on destruction time
	 */
	public void destroy(){
		_myselfJustDestroyed = new Boolean(true);
		setLocalEnvVariable(VAR_LOCAL_JUST_DESTROYED, _myselfJustDestroyed);
		unRegisterAllLinkEnvironments();
	}

	public String getPlugName(){
		return _myConfiguration.Name;
	}
	
	public String getClassPath(){
		return _myConfiguration.Path;
	}

	public void previousActionReset(){
		if(_myPlugGotLinked){
			_myPlugGotLinked = new Boolean(false);
			setLocalEnvVariable(VAR_LOCAL_PLUGGOTLINKED, _myPlugGotLinked);
		}
		if(_myPlugGotUnLinked){
			_myPlugGotUnLinked = new Boolean(false);
			setLocalEnvVariable(VAR_LOCAL_PLUGGOTUNLINKED, _myPlugGotUnLinked);
		}
		if(_myselfJustCreated != null && _myselfJustCreated){
			_myselfJustCreated = new Boolean(false);
			setLocalEnvVariable(VAR_LOCAL_JUST_CREATED, _myselfJustCreated);
		}
		if(_myButtonIsToggled){
			_myButtonIsToggled = new Boolean(false);
			setLocalEnvVariable(VAR_LOCAL_BUTTONISTOGGLED, _myButtonIsToggled);
		}
		if(_mySwitchIsToggled){
			_mySwitchIsToggled = new Boolean(false);
			setLocalEnvVariable(VAR_LOCAL_SWITCHISTOGGLED, _mySwitchIsToggled);	
		}
		if(_myPipeValueHasChanged){
			_myPipeValueHasChanged = new Boolean(false);
			setLocalEnvVariable(VAR_LOCAL_PIPEVALUE_HASCHANGED, _myPipeValueHasChanged);
		}
		if(_myPluggingIsToggled){
			_myPluggingIsToggled = new Boolean(false);
			setLocalEnvVariable(VAR_LOCAL_PLUGGINGISTOGGLED, _myPluggingIsToggled);
			//exception from the rest: it is beeing set through a notification message
			setLocalEnvVariable(VAR_LINK_PLUGGINGISTOGGLED, _myPluggingIsToggled);
		}
		//exception from the rest: it is beeing set through a notification message
		if(_myLinkPluggingIsToggled){
			_myLinkPluggingIsToggled = new Boolean(false);
			setLocalEnvVariable(VAR_LINK_PLUGGINGISTOGGLED, _myLinkPluggingIsToggled);
		}
		for(int i = 0; i < _myPipeValueXXHasChanged.length; i++){
			if(_myPipeValueXXHasChanged[i]){
				_myPipeValueXXHasChanged[i] = new Boolean(false);
				setLocalEnvVariable(VAR_LOCAL_PIPEVALUE_XX_CHANGED[i], _myPipeValueXXHasChanged[i]);
			}
		}
		if(_myPipeValueHasChanged){
			_myPipeValueHasChanged = new Boolean(false);
			setLocalEnvVariable(VAR_LOCAL_PIPEVALUE_HASCHANGED, _myPipeValueHasChanged);
		}
		if(_myNatebuCentralButtonHasChanged){
			_myNatebuCentralButtonHasChanged = new Boolean(false);
			setLocalEnvVariable(VAR_NATEBU_CENTERBUTTON_HAS_CHANGED, _myNatebuCentralButtonHasChanged);
		}
		if(_myNatebuLeftSelectorHasChanged){
			_myNatebuLeftSelectorHasChanged = new Boolean(false);
			setLocalEnvVariable(VAR_NATEBU_LEFTSELECTOR_HAS_CHANGED, _myNatebuLeftSelectorHasChanged);
		}
		if(_myNatebuRightSelectorHasChanged){
			_myNatebuRightSelectorHasChanged = new Boolean(false);
			setLocalEnvVariable(VAR_NATEBU_RIGHTSELECTOR_HAS_CHANGED, _myNatebuRightSelectorHasChanged);
		}
		if(_myNatebuLeftPotiHasChanged){
			_myNatebuLeftPotiHasChanged = new Boolean(false);
			setLocalEnvVariable(VAR_NATEBU_LEFTPOTI_HAS_CHANGED, _myNatebuLeftPotiHasChanged);
		}
		if(_myNatebuRightPotiHasChanged){
			_myNatebuRightPotiHasChanged = new Boolean(false);
			setLocalEnvVariable(VAR_NATEBU_RIGHTPOTI_HAS_CHANGED, _myNatebuRightPotiHasChanged);
		}
		if(_myNatebuRightSwitchHasChanged){
			_myNatebuRightSwitchHasChanged = new Boolean(false);
			setLocalEnvVariable(VAR_NATEBU_RIGHTSWITCH_HAS_CHANGED, _myNatebuRightSwitchHasChanged);
		}
		if(_myNatebuBottomSwitchHasChanged){
			_myNatebuBottomSwitchHasChanged = new Boolean(false);
			setLocalEnvVariable(VAR_NATEBU_BOTTOMSWITCH_HAS_CHANGED, _myNatebuBottomSwitchHasChanged);
		}
		if(_myNatebuAInputHasChanged){
			_myNatebuAInputHasChanged = new Boolean(false);
			setLocalEnvVariable(VAR_NATEBU_A_INPUT_HAS_CHANGED, _myNatebuAInputHasChanged);
		}
		if(_myNatebuBInputHasChanged){
			_myNatebuBInputHasChanged = new Boolean(false);
			setLocalEnvVariable(VAR_NATEBU_B_INPUT_HAS_CHANGED, _myNatebuBInputHasChanged);
		}
		if(_myNatebuUIHasChanged){
			_myNatebuUIHasChanged = new Boolean(false);
			setLocalEnvVariable(VAR_NATEBU_UI_HAS_CHANGED, _myNatebuUIHasChanged);
		}
		if(_myNatebuValueHasChanged){
			_myNatebuValueHasChanged = new Boolean(false);
			setLocalEnvVariable(VAR_LOCAL_NATEBUVALUES_HASCHANGED, _myNatebuValueHasChanged);
		}
		if(_myMetronomeHasClicked){
			_myMetronomeHasClicked = new Boolean(false);
			setLocalEnvVariable(VAR_LOCAL_METRONOME_HASCLICKED, _myMetronomeHasClicked);
		}
		if(_myNatebuWeightHasChanged){
			_myNatebuWeightHasChanged = new Boolean(false);
			setLocalEnvVariable(VAR_LOCAL_NATEBUWEIGHT_HASCHANGED, _myNatebuWeightHasChanged);
		}
	}


	public void registerLinkEnvironment(AbstractRuntimeEnvironment linked){
		if(_myLinkedEnv == null){
			_myLinkedEnv = new Vector<AbstractRuntimeEnvironment>();
		}
		_myLinkedEnv.add(linked);
		LinkedEnvRefresh();
	}
	

	public void unRegisterLinkEnvironment(AbstractRuntimeEnvironment linked){
		if(_myLinkedEnv != null)
			_myLinkedEnv.remove(linked);
	}
	

	private void setLocalEnvVariable(String name, Object value){
		try{
			_myRunEnv.setVariable(name, value);
		} catch(ExpressionEvaluationException e){

		}
	}
	

	private void setLinkedEnvVariable(String name, Object value){
		if(_myLinkedEnv != null){
			try{
				for(int i = 0; i < _myLinkedEnv.size(); i++){
					((AbstractRuntimeEnvironment)_myLinkedEnv.get(i)).setVariable(name, value);
				}
			} catch(ExpressionEvaluationException e){

			}
		}
	}
	
	private void unRegisterAllLinkEnvironments(){
		if(_myLinkedEnv != null){
			_myLinkedEnv.removeAllElements();
		}
	}
	

	public void reSurrect(){
		if(_myLink != null)
			_myLink.parameter.registerLinkEnvironment(_myRunEnv);
	}
	
	private void LinkedEnvRefresh(){
		setLinkedEnvVariable(VAR_LINK_SOCKETID, _mySocketId);
		setLinkedEnvVariable(VAR_LINK_PLUGID, _myPlugId);
		setLinkedEnvVariable(VAR_LINK_PLUGNAME, _myConfiguration.Name);
		setLinkedEnvVariable(VAR_LINK_DIRECTION, _myDirection);
		setLinkedEnvVariable(VAR_LINK_X_DIRECTION, _myXDirection);
		setLinkedEnvVariable(VAR_LINK_Y_DIRECTION, _myYDirection);
		setLinkedEnvVariable(VAR_LINK_X_POS, _myXPos);
		setLinkedEnvVariable(VAR_LINK_Y_POS, _myYPos);
		setLinkedEnvVariable(VAR_LINK_PLUGISPLUGGED, _myPlugIsPlugged);
		setLinkedEnvVariable(VAR_LINK_PLUGISUNPLUGGED, _myPlugIsUnPlugged);
	}

	private void LocalEnvRefresh(){
		setLocalEnvVariable(VAR_LOCAL_SOCKETID, _mySocketId);
		setLocalEnvVariable(VAR_LOCAL_PLUGID, _myPlugId);
		setLocalEnvVariable(VAR_LOCAL_PLUGNAME, _myConfiguration.Name);
		setLocalEnvVariable(VAR_LOCAL_DIRECTION, _myDirection);
		setLocalEnvVariable(VAR_LOCAL_X_DIRECTION, _myXDirection);
		setLocalEnvVariable(VAR_LOCAL_Y_DIRECTION, _myYDirection);
		setLocalEnvVariable(VAR_LOCAL_X_POS, _myXPos);
		setLocalEnvVariable(VAR_LOCAL_Y_POS, _myYPos);

		setLocalEnvVariable(VAR_LOCAL_JUST_CREATED, _myselfJustCreated);
		setLocalEnvVariable(VAR_LOCAL_JUST_DESTROYED, _myselfJustDestroyed);

		setLocalEnvVariable(VAR_LOCAL_PLUGISPLUGGED, _myPlugIsPlugged);
		setLocalEnvVariable(VAR_LOCAL_PLUGISUNPLUGGED, _myPlugIsUnPlugged);
		setLocalEnvVariable(VAR_LOCAL_PLUGGINGISTOGGLED, _myPluggingIsToggled);
		//exception from the rest: it is beeing set through a notification message
		setLocalEnvVariable(VAR_LINK_PLUGGINGISTOGGLED, _myLinkPluggingIsToggled);

		setLocalEnvVariable(VAR_LOCAL_BUTTONISPRESSED, _myButtonIsPressed);
		setLocalEnvVariable(VAR_LOCAL_BUTTONISRELEASED, _myButtonIsReleased);
		setLocalEnvVariable(VAR_LOCAL_BUTTONISTOGGLED, _myButtonIsToggled);
		setLocalEnvVariable(VAR_LOCAL_LAMPISON, _myLampIsOn);
		setLocalEnvVariable(VAR_LOCAL_LAMPISOFF, _myLampIsOff);
		setLocalEnvVariable(VAR_LOCAL_SWITCHISON, _mySwitchIsOn);
		setLocalEnvVariable(VAR_LOCAL_SWITCHISOFF, _mySwitchIsOff);
		setLocalEnvVariable(VAR_LOCAL_SWITCHISTOGGLED, _mySwitchIsToggled);

		setLocalEnvVariable(VAR_LOCAL_NATEBUVALUE, _myNatebuValue);
		setLocalEnvVariable(VAR_LOCAL_NATEBUVELOCITY, _myNatebuVelocity);
		setLocalEnvVariable(VAR_LOCAL_NATEBUACCELERATION, _myNatebuAcceleration);
		setLocalEnvVariable(VAR_LOCAL_NATEBUVALUES_HASCHANGED, _myNatebuValueHasChanged);
		for(int i = 0; i < 8; i++){
			setLocalEnvVariable(VAR_NATEBU_BOTTOMSWITCH[i], _myNatebuSwitchBottom[i]);
			setLocalEnvVariable(VAR_NATEBU_RIGHTSWITCH[i], _myNatebuSwitchRight[i]);		
		}
		setLocalEnvVariable(VAR_NATEBU_SELECTOR_LEFT, _myNatebuSelectorLeft);
		setLocalEnvVariable(VAR_NATEBU_SELECTOR_RIGHT, _myNatebuSelectorRight);
		setLocalEnvVariable(VAR_NATEBU_POTI_LEFT, _myNatebuPotiLeft);
		setLocalEnvVariable(VAR_NATEBU_POTI_RIGHT, _myNatebuPotiRight);
		setLocalEnvVariable(VAR_NATEBU_UI_HAS_CHANGED, _myNatebuUIHasChanged);
		setLocalEnvVariable(VAR_NATEBU_CENTRAL_BUTTON_IS_PRESSED, _myNatebuCentralButtonIsPressed);
		setLocalEnvVariable(VAR_NATEBU_PLUG_A_INPUT_1_IS_ON, _myNatebuPlugAInput1IsOn);
		setLocalEnvVariable(VAR_NATEBU_PLUG_A_INPUT_2_IS_ON, _myNatebuPlugAInput2IsOn);
		setLocalEnvVariable(VAR_NATEBU_PLUG_B_INPUT_1_IS_ON, _myNatebuPlugBInput1IsOn);
		setLocalEnvVariable(VAR_NATEBU_PLUG_B_INPUT_2_IS_ON, _myNatebuPlugBInput2IsOn);

		setLocalEnvVariable(VAR_NATEBU_CENTERBUTTON_HAS_CHANGED, _myNatebuCentralButtonHasChanged);
		setLocalEnvVariable(VAR_NATEBU_LEFTSELECTOR_HAS_CHANGED, _myNatebuLeftSelectorHasChanged);
		setLocalEnvVariable(VAR_NATEBU_RIGHTSELECTOR_HAS_CHANGED, _myNatebuRightSelectorHasChanged);
		setLocalEnvVariable(VAR_NATEBU_LEFTPOTI_HAS_CHANGED, _myNatebuLeftPotiHasChanged);
		setLocalEnvVariable(VAR_NATEBU_RIGHTPOTI_HAS_CHANGED, _myNatebuRightPotiHasChanged);
		setLocalEnvVariable(VAR_NATEBU_RIGHTSWITCH_HAS_CHANGED, _myNatebuRightSwitchHasChanged);
		setLocalEnvVariable(VAR_NATEBU_BOTTOMSWITCH_HAS_CHANGED, _myNatebuBottomSwitchHasChanged);
		setLocalEnvVariable(VAR_NATEBU_A_INPUT_HAS_CHANGED, _myNatebuAInputHasChanged);
		setLocalEnvVariable(VAR_NATEBU_B_INPUT_HAS_CHANGED, _myNatebuBInputHasChanged);

		setLocalEnvVariable(VAR_LOCAL_NATEBUWEIGHT, _myNatebuWeight);
		setLocalEnvVariable(VAR_LOCAL_NATEBUWEIGHT_POS_X, _myNatebuWeightPosX);
		setLocalEnvVariable(VAR_LOCAL_NATEBUWEIGHT_POS_Y, _myNatebuWeightPosY);
		setLocalEnvVariable(VAR_LOCAL_NATEBUWEIGHT_HASCHANGED, _myNatebuWeightHasChanged);

		setLocalEnvVariable(VAR_LOCAL_METRONOME_HASCLICKED, _myMetronomeHasClicked);

		setLocalEnvVariable(VAR_LOCAL_PLUGISLINKED, _myPlugIsLinked);
		setLocalEnvVariable(VAR_LOCAL_PLUGGOTLINKED, _myPlugGotLinked);
		setLocalEnvVariable(VAR_LOCAL_PLUGGOTUNLINKED, _myPlugGotUnLinked);

		setLocalEnvVariable(VAR_LOCAL_PIPEVALUE_HASCHANGED, _myPipeValueHasChanged);
		for(int channel = 0; channel < _myPipeValue.length; channel++){
			setLocalEnvVariable(VAR_LOCAL_PIPEVALUE[channel], _myPipeValue[channel]);
			setLocalEnvVariable(VAR_LOCAL_PIPEVALUE_XX_CHANGED[channel], _myPipeValueXXHasChanged[channel]);
			setLocalEnvVariable(VAR_LOCAL_PIPE_XX_ISUSED[channel], _myPipeXXIsUsed[channel]);
		}
	}

	public Effect getIconEffect(){
		return _myConfiguration.displayIcon;
	}

	public Color getIconColor(){
		return _myConfiguration.displayIconColor;
	}

	public String getIconLabel(){
		return _myConfiguration.displayIconLabel;
	}

	public String getIconLabelPipe(int pipe){
		return _myConfiguration.displayIconLabelPipes[pipe];
	}


	public void clearNatebuInterface(){
		resetNatebuInterface(new Px1MessageNatebu());
	}

	public void resetNatebuInterface(Px1MessageNatebu msg){		
		_myNatebuRightSwitchHasChanged = new Boolean(msg.rightSwitchHasChanged);
		_myNatebuSwitchRight = new Integer[8];
		for(int i = 0; i < 8; i++){
			_myNatebuSwitchRight[i] = new Integer(msg.getRightSwitch(i));
			setLocalEnvVariable(VAR_NATEBU_RIGHTSWITCH[i], _myNatebuSwitchRight[i]);		
		}
		setLocalEnvVariable(VAR_NATEBU_RIGHTSWITCH_HAS_CHANGED, _myNatebuRightSwitchHasChanged);

		_myNatebuBottomSwitchHasChanged = new Boolean(msg.bottomSwitchHasChanged);
		_myNatebuSwitchBottom = new Integer[8];
		for(int i = 0; i < 8; i++){
			_myNatebuSwitchBottom[i] = new Integer(msg.getBottomSwitch(i));
			setLocalEnvVariable(VAR_NATEBU_BOTTOMSWITCH[i], _myNatebuSwitchBottom[i]);
		}
		setLocalEnvVariable(VAR_NATEBU_BOTTOMSWITCH_HAS_CHANGED, _myNatebuBottomSwitchHasChanged);

		_myNatebuCentralButtonHasChanged = new Boolean(msg.centralButtonHasChanged);
		_myNatebuCentralButtonIsPressed = new Boolean(msg.centralButton);
		setLocalEnvVariable(VAR_NATEBU_CENTERBUTTON_HAS_CHANGED, _myNatebuCentralButtonHasChanged);
		setLocalEnvVariable(VAR_NATEBU_CENTRAL_BUTTON_IS_PRESSED, _myNatebuCentralButtonIsPressed);

		_myNatebuLeftSelectorHasChanged = new Boolean(msg.leftSelectorHasChanged);
		_myNatebuSelectorLeft = new Integer(msg.leftSelector);
		setLocalEnvVariable(VAR_NATEBU_LEFTSELECTOR_HAS_CHANGED, _myNatebuLeftSelectorHasChanged);
		setLocalEnvVariable(VAR_NATEBU_SELECTOR_LEFT, _myNatebuSelectorLeft);

		_myNatebuRightSelectorHasChanged = new Boolean(msg.rightSelectorHasChanged);
		_myNatebuSelectorRight = new Integer(msg.rightSelector);
		setLocalEnvVariable(VAR_NATEBU_RIGHTSELECTOR_HAS_CHANGED, _myNatebuRightSelectorHasChanged);
		setLocalEnvVariable(VAR_NATEBU_SELECTOR_RIGHT, _myNatebuSelectorRight);

		_myNatebuLeftPotiHasChanged = new Boolean(msg.leftPotiHasChanged);
		_myNatebuPotiLeft = new Integer(msg.leftPoti);
		setLocalEnvVariable(VAR_NATEBU_LEFTPOTI_HAS_CHANGED, _myNatebuLeftPotiHasChanged);
		setLocalEnvVariable(VAR_NATEBU_POTI_LEFT, _myNatebuPotiLeft);

		_myNatebuRightPotiHasChanged = new Boolean(msg.rightPotiHasChanged);
		_myNatebuPotiRight = new Integer(msg.rightPoti);
		setLocalEnvVariable(VAR_NATEBU_RIGHTPOTI_HAS_CHANGED, _myNatebuRightPotiHasChanged);
		setLocalEnvVariable(VAR_NATEBU_POTI_RIGHT, _myNatebuPotiRight);

		_myNatebuAInputHasChanged = new Boolean(msg.AInputHasChanged);
		_myNatebuPlugAInput1IsOn = new Boolean(msg.plugAInput1);
		_myNatebuPlugAInput2IsOn = new Boolean(msg.plugAInput2);
		setLocalEnvVariable(VAR_NATEBU_A_INPUT_HAS_CHANGED, _myNatebuAInputHasChanged);
		setLocalEnvVariable(VAR_NATEBU_PLUG_A_INPUT_1_IS_ON, _myNatebuPlugAInput1IsOn);
		setLocalEnvVariable(VAR_NATEBU_PLUG_A_INPUT_2_IS_ON, _myNatebuPlugAInput2IsOn);

		_myNatebuBInputHasChanged = new Boolean(msg.BInputHasChanged);
		_myNatebuPlugBInput1IsOn = new Boolean(msg.plugBInput1);
		_myNatebuPlugBInput2IsOn = new Boolean(msg.plugBInput2);
		setLocalEnvVariable(VAR_NATEBU_B_INPUT_HAS_CHANGED, _myNatebuBInputHasChanged);
		setLocalEnvVariable(VAR_NATEBU_PLUG_B_INPUT_1_IS_ON, _myNatebuPlugBInput1IsOn);
		setLocalEnvVariable(VAR_NATEBU_PLUG_B_INPUT_2_IS_ON, _myNatebuPlugBInput2IsOn);

		_myNatebuUIHasChanged = new Boolean(msg.uiDataHasChanged());
		setLocalEnvVariable(VAR_NATEBU_UI_HAS_CHANGED, _myNatebuUIHasChanged);
	}


	public void setNatebuInterface(Px1MessageNatebu msg){
		if(msg.rightSwitchHasChanged){
			_myNatebuRightSwitchHasChanged = new Boolean(msg.rightSwitchHasChanged);
			_myNatebuSwitchRight = new Integer[8];
			for(int i = 0; i < 8; i++){
				_myNatebuSwitchRight[i] = new Integer(msg.getRightSwitch(i));
				setLocalEnvVariable(VAR_NATEBU_RIGHTSWITCH[i], _myNatebuSwitchRight[i]);		
			}
			setLocalEnvVariable(VAR_NATEBU_RIGHTSWITCH_HAS_CHANGED, _myNatebuRightSwitchHasChanged);
		}
		if(msg.bottomSwitchHasChanged){
			_myNatebuBottomSwitchHasChanged = new Boolean(msg.bottomSwitchHasChanged);
			_myNatebuSwitchBottom = new Integer[8];
			for(int i = 0; i < 8; i++){
				_myNatebuSwitchBottom[i] = new Integer(msg.getBottomSwitch(i));
				setLocalEnvVariable(VAR_NATEBU_BOTTOMSWITCH[i], _myNatebuSwitchBottom[i]);
			}
			setLocalEnvVariable(VAR_NATEBU_BOTTOMSWITCH_HAS_CHANGED, _myNatebuBottomSwitchHasChanged);
		}
		if(msg.centralButtonHasChanged){
			_myNatebuCentralButtonHasChanged = new Boolean(msg.centralButtonHasChanged);
			_myNatebuCentralButtonIsPressed = new Boolean(msg.centralButton);
			setLocalEnvVariable(VAR_NATEBU_CENTERBUTTON_HAS_CHANGED, _myNatebuCentralButtonHasChanged);
			setLocalEnvVariable(VAR_NATEBU_CENTRAL_BUTTON_IS_PRESSED, _myNatebuCentralButtonIsPressed);
		}
		if(msg.leftSelectorHasChanged){
			_myNatebuLeftSelectorHasChanged = new Boolean(msg.leftSelectorHasChanged);
			_myNatebuSelectorLeft = new Integer(msg.leftSelector);
			setLocalEnvVariable(VAR_NATEBU_LEFTSELECTOR_HAS_CHANGED, _myNatebuLeftSelectorHasChanged);
			setLocalEnvVariable(VAR_NATEBU_SELECTOR_LEFT, _myNatebuSelectorLeft);
		}
		if(msg.rightSelectorHasChanged){
			_myNatebuRightSelectorHasChanged = new Boolean(msg.rightSelectorHasChanged);
			_myNatebuSelectorRight = new Integer(msg.rightSelector);
			setLocalEnvVariable(VAR_NATEBU_RIGHTSELECTOR_HAS_CHANGED, _myNatebuRightSelectorHasChanged);
			setLocalEnvVariable(VAR_NATEBU_SELECTOR_RIGHT, _myNatebuSelectorRight);
		}
		if(msg.leftPotiHasChanged){
			_myNatebuLeftPotiHasChanged = new Boolean(msg.leftPotiHasChanged);
			_myNatebuPotiLeft = new Integer(msg.leftPoti);
			setLocalEnvVariable(VAR_NATEBU_LEFTPOTI_HAS_CHANGED, _myNatebuLeftPotiHasChanged);
			setLocalEnvVariable(VAR_NATEBU_POTI_LEFT, _myNatebuPotiLeft);
		}
		if(msg.rightPotiHasChanged){
			_myNatebuRightPotiHasChanged = new Boolean(msg.rightPotiHasChanged);
			_myNatebuPotiRight = new Integer(msg.rightPoti);
			setLocalEnvVariable(VAR_NATEBU_RIGHTPOTI_HAS_CHANGED, _myNatebuRightPotiHasChanged);
			setLocalEnvVariable(VAR_NATEBU_POTI_RIGHT, _myNatebuPotiRight);
		}
		if(msg.AInputHasChanged){
			_myNatebuAInputHasChanged = new Boolean(msg.AInputHasChanged);
			_myNatebuPlugAInput1IsOn = new Boolean(msg.plugAInput1);
			_myNatebuPlugAInput2IsOn = new Boolean(msg.plugAInput2);
			setLocalEnvVariable(VAR_NATEBU_A_INPUT_HAS_CHANGED, _myNatebuAInputHasChanged);
			setLocalEnvVariable(VAR_NATEBU_PLUG_A_INPUT_1_IS_ON, _myNatebuPlugAInput1IsOn);
			setLocalEnvVariable(VAR_NATEBU_PLUG_A_INPUT_2_IS_ON, _myNatebuPlugAInput2IsOn);
		}
		if(msg.BInputHasChanged){
			_myNatebuBInputHasChanged = new Boolean(msg.BInputHasChanged);
			_myNatebuPlugBInput1IsOn = new Boolean(msg.plugBInput1);
			_myNatebuPlugBInput2IsOn = new Boolean(msg.plugBInput2);
			setLocalEnvVariable(VAR_NATEBU_B_INPUT_HAS_CHANGED, _myNatebuBInputHasChanged);
			setLocalEnvVariable(VAR_NATEBU_PLUG_B_INPUT_1_IS_ON, _myNatebuPlugBInput1IsOn);
			setLocalEnvVariable(VAR_NATEBU_PLUG_B_INPUT_2_IS_ON, _myNatebuPlugBInput2IsOn);
		}
		if(msg.uiDataHasChanged()){
			_myNatebuUIHasChanged = new Boolean(msg.uiDataHasChanged());
			setLocalEnvVariable(VAR_NATEBU_UI_HAS_CHANGED, _myNatebuUIHasChanged);
		}
	}


	protected void setSocketID(int socketId){
		_mySocketId = new Integer(socketId);
		setLocalEnvVariable(VAR_LOCAL_SOCKETID, _mySocketId);
		setLinkedEnvVariable(VAR_LINK_SOCKETID, _mySocketId);
	}

	public Integer getSocketID(){
		return _mySocketId;
	}

	protected void setPlugID(int plugId){
		_myPlugId = new Integer(plugId);
		setLocalEnvVariable(VAR_LOCAL_PLUGID, _myPlugId);
		setLinkedEnvVariable(VAR_LINK_PLUGID, _myPlugId);
	}

	public Integer getPlugID(){
		return _myPlugId;
	}

	protected void setDirection(int direction){
		_myDirection = new Integer(direction);
		setLocalEnvVariable(VAR_LOCAL_DIRECTION, _myDirection);
		setLinkedEnvVariable(VAR_LINK_DIRECTION, _myDirection);
		switch(direction){
		case 4:
			_myXDirection = new Integer(-1);
			_myYDirection = new Integer(0);
			break;
		case 1:
			_myXDirection = new Integer(0);
			_myYDirection = new Integer(-1);
			break;
		case 2:
			_myXDirection = new Integer(1);
			_myYDirection = new Integer(0);
			break;
		case 3:
			_myXDirection = new Integer(0);
			_myYDirection = new Integer(1);
			break;
		default:
			_myXDirection = new Integer(0);
		_myYDirection = new Integer(0);
		break;
		}
		setLocalEnvVariable(VAR_LOCAL_X_DIRECTION, _myXDirection);
		setLinkedEnvVariable(VAR_LINK_X_DIRECTION, _myXDirection);

		setLocalEnvVariable(VAR_LOCAL_Y_DIRECTION, _myYDirection);
		setLinkedEnvVariable(VAR_LINK_Y_DIRECTION, _myYDirection);
	}

	public Integer getDirection(){
		return _myDirection;
	}

	public Integer getXDirection(){
		return _myXDirection;
	}

	public Integer getYDirection(){
		return _myYDirection;
	}

	protected void setPosition(int xPos, int yPos){
		_myXPos = new Integer(xPos);
		_myYPos = new Integer(yPos);
		setLocalEnvVariable(VAR_LOCAL_X_POS, _myXPos);
		setLinkedEnvVariable(VAR_LINK_X_POS, _myXPos);
		setLocalEnvVariable(VAR_LOCAL_Y_POS, _myYPos);
		setLinkedEnvVariable(VAR_LINK_Y_POS, _myYPos);
	}

	public Integer getXPos(){
		return _myXPos;
	}

	public Integer getYPos(){
		return _myYPos;
	}

	protected void setButton(int button){
		if(!(new Boolean((button == 1)? true: false)).equals(_myButtonIsPressed)){
			_myButtonIsPressed = new Boolean((button == 1)? true: false);
			_myButtonIsReleased = new Boolean((button == 0)? true: false);
			_myButtonIsToggled = new Boolean(true);
			setLocalEnvVariable(VAR_LOCAL_BUTTONISPRESSED, _myButtonIsPressed);
			setLocalEnvVariable(VAR_LOCAL_BUTTONISRELEASED, _myButtonIsReleased);
			setLocalEnvVariable(VAR_LOCAL_BUTTONISTOGGLED, _myButtonIsToggled);
		}
	}

	public boolean buttonIsPressed(){
		return _myButtonIsPressed.booleanValue();
	}

	public boolean buttonIsReleased(){
		return _myButtonIsReleased.booleanValue();
	}

	public boolean buttonIsToggled(){
		return _myButtonIsToggled.booleanValue();
	}

	protected void setLamp(int lamp){
		if(!(new Boolean((lamp == 1)? true: false)).equals(_myLampIsOn)){
			_myLampIsOn = new Boolean((lamp == 1)? true: false);
			_myLampIsOff = new Boolean((lamp == 0)? true: false);
			setLocalEnvVariable(VAR_LOCAL_LAMPISON, _myLampIsOn);
			setLocalEnvVariable(VAR_LOCAL_LAMPISOFF, _myLampIsOff);
		}
	}

	public boolean lampIsOn(){
		return _myLampIsOn.booleanValue();
	}

	public boolean lampIsOff(){
		return _myLampIsOff.booleanValue();
	}

	protected void setIsPlugged(){
		setPluggedCondition(true);
	}

	protected void setIsUnPlugged(){
		setPluggedCondition(false);
	}

	protected void setPluggedCondition(int con){
		setPluggedCondition((con == 1)? true: false);
	}

	protected void setPluggedCondition(boolean con){
		if(_myPlugIsPlugged == null || _myPlugIsPlugged.booleanValue() != con){
			_myPlugIsPlugged = new Boolean(con);
			_myPlugIsUnPlugged = new Boolean(!con);
			_myPluggingIsToggled = new Boolean(true);
			setLocalEnvVariable(VAR_LOCAL_PLUGISPLUGGED, _myPlugIsPlugged);
			setLinkedEnvVariable(VAR_LINK_PLUGISPLUGGED, _myPlugIsPlugged);
			setLocalEnvVariable(VAR_LOCAL_PLUGISUNPLUGGED, _myPlugIsUnPlugged);
			setLinkedEnvVariable(VAR_LINK_PLUGISUNPLUGGED, _myPlugIsUnPlugged);
			setLocalEnvVariable(VAR_LOCAL_PLUGGINGISTOGGLED, _myPluggingIsToggled);
		}
	}
	
	//exception from the rest: it is beeing set through a notification message
	// and not through the links PlugParameter
	public void setLinkedPluggedToggle(){
		_myLinkPluggingIsToggled = new Boolean(true);
		setLocalEnvVariable(VAR_LINK_PLUGGINGISTOGGLED, _myLinkPluggingIsToggled);
	}

	public boolean plugIsPlugged(){
		return _myPlugIsPlugged.booleanValue();
	}

	public boolean plugIsUnPlugged(){
		return _myPlugIsUnPlugged.booleanValue();
	}

	public boolean pluggingIsToggled(){
		return _myPluggingIsToggled.booleanValue();
	}
	

	protected void setSwitch(boolean switdge){
		if(_mySwitchIsOn == null || _mySwitchIsOn != switdge){
			_mySwitchIsOn = new Boolean(switdge);
			_mySwitchIsOff = new Boolean(!switdge);
			_mySwitchIsToggled = new Boolean(true);
			setLocalEnvVariable(VAR_LOCAL_SWITCHISON, _mySwitchIsOn);
			setLocalEnvVariable(VAR_LOCAL_SWITCHISOFF, _mySwitchIsOff);
			setLocalEnvVariable(VAR_LOCAL_SWITCHISTOGGLED, _mySwitchIsToggled);
		}
	}

	public void toggleSwitch(){
		_mySwitchIsOn = new Boolean(!_mySwitchIsOn.booleanValue());
		_mySwitchIsOff = new Boolean(!_mySwitchIsOff.booleanValue());
		_mySwitchIsToggled = new Boolean(true);
		setLocalEnvVariable(VAR_LOCAL_SWITCHISON, _mySwitchIsOn);
		setLocalEnvVariable(VAR_LOCAL_SWITCHISOFF, _mySwitchIsOff);
		setLocalEnvVariable(VAR_LOCAL_SWITCHISTOGGLED, _mySwitchIsToggled);
	}

	public boolean switchIsOn(){
		return _mySwitchIsOn.booleanValue();
	}

	public boolean switchIsOff(){
		return _mySwitchIsOff.booleanValue();
	}

	public boolean switchIsToggled(){
		return _mySwitchIsToggled.booleanValue();
	}
	

	public void setLink(Plug link){
		if(link != null){
			if(_myLink != null){
				_myLink.parameter.unRegisterLinkEnvironment(_myRunEnv);
				_myPlugGotUnLinked = new Boolean(true);
				setLocalEnvVariable(VAR_LOCAL_PLUGGOTUNLINKED, _myPlugGotUnLinked);
			}
			_myLink = link;
			_myLink.parameter.registerLinkEnvironment(_myRunEnv);
			_myPlugIsLinked = new Boolean(true);
			setLocalEnvVariable(VAR_LOCAL_PLUGISLINKED, _myPlugIsLinked);
			_myPlugGotLinked = new Boolean(true);
			setLocalEnvVariable(VAR_LOCAL_PLUGGOTLINKED, _myPlugGotLinked);
		}
	}

	public void unLink(){
		if(_myLink != null)
			_myLink.parameter.unRegisterLinkEnvironment(_myRunEnv);
		_myLink = null;
		_myPlugIsLinked = new Boolean(false);
		setLocalEnvVariable(VAR_LOCAL_PLUGISLINKED, _myPlugIsLinked);
		_myPlugGotLinked = new Boolean(false);
		setLocalEnvVariable(VAR_LOCAL_PLUGGOTLINKED, _myPlugGotLinked);
		_myPlugGotUnLinked = new Boolean(true);
		setLocalEnvVariable(VAR_LOCAL_PLUGGOTUNLINKED, _myPlugGotUnLinked);
	}

	public Plug getLink(){
		return _myLink;
	}

	public boolean plugIsLinked(){
		return _myPlugIsLinked.booleanValue();
	}

	public boolean plugIsUnLinked(){
		return !_myPlugIsLinked.booleanValue();
	}

	public boolean setMetronomeClick(boolean condition){
		Boolean val = new Boolean(condition);
		if(_myMetronomeHasClicked == null || val.booleanValue() != _myMetronomeHasClicked.booleanValue()){
			_myMetronomeHasClicked = val;
			setLocalEnvVariable(VAR_LOCAL_METRONOME_HASCLICKED, _myMetronomeHasClicked);
			return true;
		}
		return false;
	}

	public boolean setNatebuWeight(float weight, float PosX, float PosY){
		Float val = new Float(ch.maybites.tools.Calc.trim(0f, weight, 255f));
		Float x = new Float(ch.maybites.tools.Calc.trim(-127f, PosX, 127f));
		Float y = new Float(ch.maybites.tools.Calc.trim(-127f, PosY, 127f));
		if(_myNatebuWeight == null || val.floatValue() != _myNatebuWeight.floatValue()){
			_myNatebuWeight = val;
			_myNatebuWeightPosX = x;
			_myNatebuWeightPosY = y;
			_myNatebuWeightHasChanged = new Boolean(true);
			setLocalEnvVariable(VAR_LOCAL_NATEBUWEIGHT, _myNatebuWeight);
			setLocalEnvVariable(VAR_LOCAL_NATEBUWEIGHT_POS_X, _myNatebuWeightPosX);
			setLocalEnvVariable(VAR_LOCAL_NATEBUWEIGHT_POS_Y, _myNatebuWeightPosY);
			setLocalEnvVariable(VAR_LOCAL_NATEBUWEIGHT_HASCHANGED, _myNatebuWeightHasChanged);
			return true;
		}
		return false;
	}

	public boolean setNatebuValue(float value){
		Float val = new Float(ch.maybites.tools.Calc.trim(0f, value, 255f));
		if(_myNatebuValue == null){
			_myNatebuValue = new Float(0);
			_myNatebuVelocity = new Float(0);
			_myNatebuAcceleration = new Float(0);
			_myNatebuValueHasChanged = new Boolean(false);
			setLocalEnvVariable(VAR_LOCAL_NATEBUVALUE, _myNatebuValue);
			setLocalEnvVariable(VAR_LOCAL_NATEBUVELOCITY, _myNatebuVelocity);
			setLocalEnvVariable(VAR_LOCAL_NATEBUACCELERATION, _myNatebuAcceleration);
			setLocalEnvVariable(VAR_LOCAL_NATEBUVALUES_HASCHANGED, _myNatebuValueHasChanged);
		}
		if(val.floatValue() != _myNatebuValue.floatValue()){
			float newVelocity = val - _myNatebuValue;
			_myNatebuAcceleration = newVelocity - _myNatebuVelocity;
			_myNatebuVelocity = newVelocity;
			_myNatebuValue = val;
			_myNatebuValueHasChanged = new Boolean(true);
			setLocalEnvVariable(VAR_LOCAL_NATEBUVALUE, _myNatebuValue);
			setLocalEnvVariable(VAR_LOCAL_NATEBUVELOCITY, _myNatebuVelocity);
			setLocalEnvVariable(VAR_LOCAL_NATEBUACCELERATION, _myNatebuAcceleration);
			setLocalEnvVariable(VAR_LOCAL_NATEBUVALUES_HASCHANGED, _myNatebuValueHasChanged);
			return true;
		}
		return false;
	}

	public float getNatebuValue(){
		return _myNatebuValue;
	}

	public boolean setPipeUsedConndition(int channel, boolean condition){
		if(channel >= 0 && channel < _myPipeXXIsUsed.length){
			if(_myPipeXXIsUsed[channel] == null || condition != _myPipeXXIsUsed[channel].booleanValue()){
				_myPipeXXIsUsed[channel] = new Boolean(condition);
				setLocalEnvVariable(VAR_LOCAL_PIPE_XX_ISUSED[channel], _myPipeXXIsUsed[channel]);
				return true;
			}
		}
		return false;
	}

	public boolean setPipeValue(int channel, float value){
		if(channel >= 0 && channel < _myPipeValue.length){
			Float val = new Float(ch.maybites.tools.Calc.trim(0f, value, 255f));
			if(_myPipeValue[channel] == null || val.floatValue() != _myPipeValue[channel].floatValue()){
				_myPipeValue[channel] = val;
				_myPipeValueXXHasChanged[channel] = new Boolean(true);
				_myPipeValueHasChanged = new Boolean(true);
				setLocalEnvVariable(VAR_LOCAL_PIPEVALUE[channel], _myPipeValue[channel]);
				setLocalEnvVariable(VAR_LOCAL_PIPEVALUE_XX_CHANGED[channel], _myPipeValueXXHasChanged[channel]);
				setLocalEnvVariable(VAR_LOCAL_PIPEVALUE_HASCHANGED, _myPipeValueHasChanged);
				return true;
			}
		}
		return false;
	}

	public boolean addPipeValue(int channel, float value){
		return setPipeValue(channel, value + _myPipeValue[channel]);
	}

	public Float getPipeValue(int channel){
		return _myPipeValue[channel];
	}

	public boolean getPipeValueXXChanged(int channel){
		return _myPipeValueXXHasChanged[channel].booleanValue();
	}

	public boolean pipeValueIsChanged(){
		return _myPipeValueHasChanged.booleanValue();
	}

	private  void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException {
		stream.defaultReadObject(); 
		_myPipeValue = new Float[9];
		_myPipeValueXXHasChanged = new Boolean[9];
		_myPipeXXIsUsed = new Boolean[9];
		for(int i = 0; i < _myPipeValue.length; i++){
			_myPipeValue[i] = (Float)stream.readObject();
			_myPipeValueXXHasChanged[i] = (Boolean)stream.readObject();
			_myPipeXXIsUsed[i] = (Boolean)stream.readObject();
		}
		_myNatebuSwitchBottom = new Integer[8];
		_myNatebuSwitchRight = new Integer[8];
		for(int i = 0; i < 8; i++){
			_myNatebuSwitchBottom[i] = (Integer)stream.readObject();
			_myNatebuSwitchRight[i] = (Integer)stream.readObject();
		}
		_myRunEnv = new StandaloneRuntimeEnvironment();
		LocalEnvRefresh();
		parse();	
	}

	private synchronized void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
		stream.defaultWriteObject(); 
		for(int i = 0; i < _myPipeValue.length; i++){
			stream.writeObject(_myPipeValue[i]);
			stream.writeObject(_myPipeValueXXHasChanged[i]);
			stream.writeObject(_myPipeXXIsUsed[i]);
		}
		for(int i = 0; i < _myNatebuSwitchBottom.length; i++){
			stream.writeObject(_myNatebuSwitchBottom[i]);
			stream.writeObject(_myNatebuSwitchRight[i]);
		}
	}
}
