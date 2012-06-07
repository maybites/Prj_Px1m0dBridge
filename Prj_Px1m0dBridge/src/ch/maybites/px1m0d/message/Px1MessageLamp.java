package ch.maybites.px1m0d.message;

import javax.sound.midi.*;

import ch.maybites.px1m0d.Debugger;

public class Px1MessageLamp extends Px1MessageMidi{
	private static final long serialVersionUID = 1L;
	
	public Px1MessageLamp(byte lamp, byte flag){
		super();
		super.myRoute = Message.ROUTE_OUTSIDE;
		super.myReceiver = Message.RECEIVER_PX1M0D;
		ShortMessage message = new ShortMessage();
		try{
			message.setMessage(144, lamp, flag);
		} catch(InvalidMidiDataException e){
			Debugger.getInstance().errorMessage(this.getClass(),e.toString());
		}
		this.socketID_FROM = lamp;
		super._message = message;
	}		
}
