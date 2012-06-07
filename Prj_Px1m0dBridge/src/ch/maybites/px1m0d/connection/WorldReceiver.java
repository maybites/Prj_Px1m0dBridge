package ch.maybites.px1m0d.connection;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import ch.maybites.px1m0d.message.*;

public class WorldReceiver implements Receiver{
	
	private Connector connector;
	
	public WorldReceiver(Connector c){
		connector = c;
	}

	public void send(MidiMessage theMessage, long timeStamp){
		if(Px1MessageMetronome.identify(theMessage)){
			connector.receiveFromWorld(new Px1MessageMetronome(theMessage));
		} else {
			connector.receiveFromWorld(new Px1MessageMidi(Message.ROUTE_BOARD, Message.RECEIVER_PLUG, theMessage));
		}
	}
	
	public void close(){
		;
	}
}
