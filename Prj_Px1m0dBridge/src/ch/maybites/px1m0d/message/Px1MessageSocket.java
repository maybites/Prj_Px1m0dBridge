package ch.maybites.px1m0d.message;

import javax.sound.midi.*;

public class Px1MessageSocket extends Px1MessageMidi{
	private static final long serialVersionUID = 1L;
	
	private final int PX1M0D_ROW = 2;
	private final int PX1M0D_COLUMN = 3;
	private final int PX1M0D_SOCKETID = 4;
	private final int PX1M0D_PLUGID = 5;
	private final int PX1M0D_SOCKETBUTTON = 6;
	private final int PX1M0D_PLUGDIR = 7;
	private final int PX1M0D_PLUGLAMP = 8;
	private final int PX1M0D_ISPLUGGED = 9;
	private final int PX1M0D_AIN100TH = 10;
	private final int PX1M0D_AINREST =11;
	private final int PX1M0D_TOLERANCE = 12;

	private static int MESSAGE_LENGTH = 14;

	public Px1MessageSocket(MidiMessage m){
		super.myRoute = Message.ROUTE_SOCKETID;
		super.myType = Message.TYPE_SOCKET;
		super.myReceiver = Message.RECEIVER_SOCKET;
		_message = m;
		this.socketID_TO = m.getMessage()[PX1M0D_SOCKETID];
	}

	public static boolean identify(MidiMessage m){
		if(m.getClass().getName().equals("javax.sound.midi.SysexMessage")){
			byte[] b = m.getMessage();
			if(b.length == MESSAGE_LENGTH){
				if(b[1] == 0x10){ // its a Sysex - Socket Message
					return true;
				}
			}
		}
		return false;
	}

	public byte getPlugLamp(){
		return _message.getMessage()[PX1M0D_PLUGLAMP];
	}

	public byte getPlugDir(){
		return _message.getMessage()[PX1M0D_PLUGDIR];
	}

	public byte getSocketButton(){
		return _message.getMessage()[PX1M0D_SOCKETBUTTON];
	}

	public byte getRow(){
		return _message.getMessage()[PX1M0D_ROW];
	}

	public byte getColumn(){
		return _message.getMessage()[PX1M0D_COLUMN];
	}

	public byte getSocketID(){
		return _message.getMessage()[PX1M0D_SOCKETID];
	}

	public boolean getIsPlugged(){
		return (_message.getMessage()[PX1M0D_ISPLUGGED] == 1)?true: false;
	}

	public byte getPlugID(){
		return _message.getMessage()[PX1M0D_PLUGID];
	}
	
	public int getPlugAIN(){
		return _message.getMessage()[PX1M0D_AIN100TH] * 100 + _message.getMessage()[PX1M0D_AINREST];
	}

	public byte getPlugIDTolerance(){
		return _message.getMessage()[PX1M0D_TOLERANCE];
	}

	public int getMessageReceiverSocketID(){
		return _message.getMessage()[PX1M0D_SOCKETID];
	}
	
	public int getMessageReceiverSocketRow(){
		return _message.getMessage()[PX1M0D_ROW];
	}

	public int getMessageReceiverSocketColumn(){
		return _message.getMessage()[PX1M0D_COLUMN];
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
