package ch.maybites.px1m0d.message;

import javax.sound.midi.*;

import ch.maybites.px1m0d.Debugger;

/*
 * 
 */
public class Px1MessageMidi extends Message{
	private static final long serialVersionUID = 1L;
	protected MidiMessage _message;

	public Px1MessageMidi(){
		super.myType = Message.TYPE_MIDI;
	}

	public Px1MessageMidi(MidiMessage m){
		this();
		_message = m;
	}

	public Px1MessageMidi(int gate, int receiver){
		this();
		super.myRoute = gate;
		super.myReceiver = receiver;
	}

	public Px1MessageMidi(int gate, int receiver, MidiMessage m){
		this(gate, receiver);
		_message = m;
	}
		
	public Px1MessageMidi(int gate, int receiver, int control, int val1, int val2){
		this(gate, receiver);
		ShortMessage m = new ShortMessage();
		try{
			m.setMessage(control, val1, val2);
		} catch(InvalidMidiDataException e){
			Debugger.getInstance().errorMessage(this.getClass(),e.toString());
		}
		_message = m;
	}

	public MidiMessage getMidiMessage(){
		return _message;
	}
	
	public String printLine(){
		String 	s = "";
		s += " Midi:";
		byte[] b = _message.getMessage();
		int val;
		for(int i = 0; i < b.length; i++){
			val = (i == 0) ? (b[i] + 256) : b[i];
			s += " " + val;
		}
		return s;
	}
	
	public String print(){
		String 	s  = ">Midi Message START<\n";
		s += " - Type is " + this.myType + "\n";
		s += " - Receiver is " + this.myReceiver + "\n";
		if(_message != null){
			s += " - MidiMessage - class is " + _message.getClass().getName() + "\n";
			s += " - Content is ";
			for(int i = 0; i < _message.getMessage().length; i++){
				s += _message.getMessage()[i] + " ; ";
			}
			s += "\n";
		}
		s += ">Midi Message END<\n";
		return s;
	}	
}
