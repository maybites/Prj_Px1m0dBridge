package ch.maybites.px1m0d.message;

import javax.sound.midi.*;

/*
 */
public class Px1MessageMetronome extends Px1MessageMidi{
	private static final long serialVersionUID = 1L;

	private static int MESSAGE_LENGTH = 2;
	
	public Px1MessageMetronome(MidiMessage m){
		super.myRoute = Message.ROUTE_BRIDGE;
		super.myType = Message.TYPE_METRONOME;
		super.myReceiver = Message.ROUTE_BOARD;
		super.socketID_TO = -1;
	}

	public static boolean identify(MidiMessage m){
		if(m.getClass().getName().equals("javax.sound.midi.ShortMessage")){
			byte[] b = m.getMessage();
			for(int i = 0; i < b.length; i++){
				System.out.print(b[i] + " ");
			}
			System.out.println();
			
			if(b.length == MESSAGE_LENGTH){
				if(b[1] == 0x77){ // its a Sysex - Metronome Message
					return true;
				}
			}
		}
		return false;
	}

}
