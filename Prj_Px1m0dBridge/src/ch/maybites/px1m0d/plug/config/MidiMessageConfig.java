package ch.maybites.px1m0d.plug.config;

import proxml.XMLElement;
import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.message.*;
import ch.maybites.px1m0d.plug.config.exception.MessageCreationException;
import ch.maybites.px1m0d.plug.config.exception.PlugConfigurationException;
import de.cnc.expression.*;
import de.cnc.expression.exceptions.*;
import java.math.*;
import java.io.*;

public class MidiMessageConfig implements MessageConfig, Serializable{
	private static final long serialVersionUID = 1L;

	// the code has been adapted so its not case sensitive:
	private final String STATUS_TYPE_NoteOFF 			= "NoteOff";
	private final String STATUS_TYPE_NoteOn	 			= "NoteOn";
	private final String STATUS_TYPE_PolyKeyPressure 	= "PolyKeyPressure";
	private final String STATUS_TYPE_ControlChange 		= "ControlChange";
	private final String STATUS_TYPE_ProgramChange 		= "ProgramChange";
	private final String STATUS_TYPE_ChannelPressure 	= "ChannelPressure";
	private final String STATUS_TYPE_PitchWheelChange 	= "PitchBend";
	private final String STATUS_TYPE_SysEx 				= "SysEx";

	private final static String ATTR_STATUS_TYPE = "type";
	private final static String ATTR_STATUS_CHANNEL = "channel";

	private final static String TAG_STATUS = "status";
	private final static String TAG_DATA = "data";

	private String _myMidiStatusChannel, _myMidiStatusTypeName;
	private int _myMidiStatusType;

	private String[] _myMidiData;

	transient private XMLElement _myPlugElement;
	transient private Expression[] _myExpMidiData;
	transient private Expression _myExpMidiChannel;

	public MidiMessageConfig(XMLElement data) throws PlugConfigurationException{
		try{
			_myPlugElement = data;
			_myMidiStatusType = getMidiStatusType();
			_myMidiStatusChannel = getMidiStatusChannel();
			storeMidiData();
		} catch (proxml.InvalidAttributeException e){
			Debugger.getInstance().fatalMessage(this.getClass(), "Parsing Error in XML-Tree: ");
			data.printElementTree();
			throw new PlugConfigurationException(e.getMessage());
		}
	}

	public void parse(AbstractRuntimeEnvironment runEnv) throws ExpressionParseException{
		_myExpMidiChannel = Expression.parse(_myMidiStatusChannel, runEnv);
		_myExpMidiData = new Expression[_myMidiData.length];
		for(int i = 0; i < _myMidiData.length; i++){
			_myExpMidiData[i] = Expression.parse(_myMidiData[i], runEnv);
		}
	}

	public Message evaluate(AbstractRuntimeEnvironment runEnv) throws ExpressionEvaluationException, MessageCreationException{
		try{
			Px1MessageMidi message;
			int channel = ((Number)_myExpMidiChannel.eval(runEnv)).intValue() & 0x000F;
			byte[] data = evaluateData(runEnv);
			if(_myMidiStatusType != javax.sound.midi.ShortMessage.END_OF_EXCLUSIVE){
				javax.sound.midi.ShortMessage midiMessage = new javax.sound.midi.ShortMessage();
				if(data.length == 1){
					midiMessage.setMessage(_myMidiStatusType, channel, data[0], 0);
				} else if(data.length == 2){
					midiMessage.setMessage(_myMidiStatusType, channel, data[0], data[1]);
				} else {
					Debugger.getInstance().errorMessage(getClass(), "Midi Message requires one or two data bytes, but none has been specified.");
					return null;
				}
				message = new Px1MessageMidi(midiMessage);	
			} else {
				javax.sound.midi.SysexMessage midiMessage = new javax.sound.midi.SysexMessage();
				midiMessage.setMessage(data, data.length);
				message = new Px1MessageMidi(midiMessage);	
			}
			return message;
		}catch(javax.sound.midi.InvalidMidiDataException e){
			throw new MessageCreationException("Unable to create midimessage.", e.getCause());
		}
	}

	private byte[] evaluateData(AbstractRuntimeEnvironment runEnv) throws ExpressionEvaluationException{
		byte[] ret = new byte[_myExpMidiData.length];
		for(int i = 0; i < _myExpMidiData.length; i++){
			double value = ((Number)_myExpMidiData[i].eval(runEnv)).doubleValue();
			ret[i] = (byte)value;
		}
		return ret;
	}

	private int getMidiStatusType(){
		XMLElement[] children = _myPlugElement.getChildren();
		for(int i = 0; i < children.length; i++){
			if(children[i].getName().equals(TAG_STATUS)){
				_myMidiStatusTypeName = children[i].getAttribute(ATTR_STATUS_TYPE);
				return mapStatusTypes(_myMidiStatusTypeName);
			}
		}	
		return javax.sound.midi.ShortMessage.NOTE_OFF;
	}

	private int mapStatusTypes(String type){
		if(type.toLowerCase().equals(this.STATUS_TYPE_NoteOFF.toLowerCase())){
			return javax.sound.midi.ShortMessage.NOTE_OFF;
		} else if(type.toLowerCase().equals(this.STATUS_TYPE_NoteOn.toLowerCase())){
			return javax.sound.midi.ShortMessage.NOTE_ON;
		} else if(type.toLowerCase().equals(this.STATUS_TYPE_PitchWheelChange.toLowerCase())){
			return javax.sound.midi.ShortMessage.PITCH_BEND;
		} else if(type.toLowerCase().equals(this.STATUS_TYPE_ChannelPressure.toLowerCase())){
			return javax.sound.midi.ShortMessage.CHANNEL_PRESSURE;
		} else if(type.toLowerCase().equals(this.STATUS_TYPE_ControlChange.toLowerCase())){
			return javax.sound.midi.ShortMessage.CONTROL_CHANGE;
		} else if(type.toLowerCase().equals(this.STATUS_TYPE_PolyKeyPressure.toLowerCase())){
			return javax.sound.midi.ShortMessage.POLY_PRESSURE;
		} else if(type.toLowerCase().equals(this.STATUS_TYPE_ProgramChange.toLowerCase())){
			return javax.sound.midi.ShortMessage.PROGRAM_CHANGE;
		} else if(type.toLowerCase().equals(this.STATUS_TYPE_SysEx.toLowerCase())){
			return javax.sound.midi.ShortMessage.END_OF_EXCLUSIVE;
		} 
		return javax.sound.midi.ShortMessage.NOTE_OFF;
	}

	private String getMidiStatusChannel(){
		XMLElement[] children = _myPlugElement.getChildren();
		for(int i = 0; i < children.length; i++){
			if(children[i].getName().equals(TAG_STATUS)){
				return PlugConfig.translateBooleanChars(children[i].getAttribute(ATTR_STATUS_CHANNEL));
			}
		}	
		return null;
	}

	private void storeMidiData(){
		XMLElement[] children = _myPlugElement.getChildren();
		int countDataTags = 0;
		for(int i = 0; i < children.length; i++){
			if(children[i].getName().equals(TAG_DATA)){
				countDataTags++;
			}
		}	
		_myMidiData = new String[countDataTags];
		countDataTags = 0;
		for(int i = 0; i < children.length; i++){
			if(children[i].getName().equals(TAG_DATA)){
				_myMidiData[countDataTags] = PlugConfig.translateBooleanChars(children[i].getChild(0).getText());
				countDataTags++;
			}
		}	
	}

	public void print(){
		Debugger.getInstance().infoMessage(null, "                Midi Message:");
		Debugger.getInstance().infoMessage(null, "                     Status Type: " + _myMidiStatusTypeName);
		Debugger.getInstance().infoMessage(null, "                     Status Channel: " + _myMidiStatusChannel);
		for(int i = 0; i < _myMidiData.length; i++){
			Debugger.getInstance().infoMessage(null, "                      Data Value[" + i + "] : " + _myMidiData[i]);
		}
	}


}
