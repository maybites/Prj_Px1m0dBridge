package ch.maybites.px1m0d.message;

import javax.sound.midi.*;

public class Px1MessageSystemReset extends Px1MessageMidi{
	private static final long serialVersionUID = 1L;

	private static int MESSAGE_LENGTH = 8;
	
	public Px1MessageSystemReset(MidiMessage m){
		super.myRoute = Message.ROUTE_BRIDGE;
		super.myType = Message.TYPE_PX1M0DRESETED;
		super.myReceiver = Message.RECEIVER_BRIDGE;
		super._message = m;
	}

	public static boolean identify(MidiMessage m){
		if(m.getClass().getName().equals("javax.sound.midi.SysexMessage")){
			byte[] b = m.getMessage();
			if(b.length == MESSAGE_LENGTH){
				if(b[1] == 0x00 && b[2] == 0x00){ // its a Sysex - SytemReset Message
					return true;
				}
			}
		}
		return false;
	}

}
