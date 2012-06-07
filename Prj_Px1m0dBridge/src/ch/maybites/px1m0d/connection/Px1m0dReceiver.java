package ch.maybites.px1m0d.connection;

import javax.sound.midi.*;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.message.*;


public class Px1m0dReceiver implements Receiver{

	private Connector connector;

	public Px1m0dReceiver(Connector c){
		connector = c;
	}

	public void send(MidiMessage theMessage, long timeStamp){
		if(Px1MessageSocket.identify(theMessage)){
			connector.receiveFromPxlm0d(new Px1MessageSocket(theMessage));
		} else if(Px1MessageSystemReset.identify(theMessage)){
			connector.receiveFromPxlm0d(new Px1MessageSystemReset(theMessage));
			Debugger.getInstance().infoMessage(this.getClass(),"SystemReset Received. create message...");
		} else{
			/**
			Debugger.getInstance().verboseMessage(this.getClass(),"Px1M0d Midi Receiver: Unknown Message received. Message class: " + theMessage.getClass().getName() + "  Message droped: [");
			for(int i = 0; i < theMessage.getMessage().length; i++){
				int val = (i == 0) ? (theMessage.getMessage()[i] + 256) : theMessage.getMessage()[i];
				Debugger.getInstance().verboseMessage(this.getClass(),val + " ");
			}
			Debugger.getInstance().verboseMessage(this.getClass(),"]");
			**/
		}
	}

	public void close(){
		;
	}
}
