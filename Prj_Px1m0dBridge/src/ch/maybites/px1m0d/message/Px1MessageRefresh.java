package ch.maybites.px1m0d.message;

import javax.sound.midi.*;

import ch.maybites.px1m0d.Debugger;

public class Px1MessageRefresh extends Px1MessageMidi{
	private static final long serialVersionUID = 1L;

	public Px1MessageRefresh(){
		super();
		super.myRoute = Message.ROUTE_OUTSIDE;
		super.myType = Message.TYPE_MIDI;
		super.myReceiver = Message.RECEIVER_PX1M0D;

		ShortMessage m = new ShortMessage();
		try{
			m.setMessage(192, 0, 0);
		} catch(InvalidMidiDataException e){
			Debugger.getInstance().errorMessage(this.getClass(),e.toString());
		}
		super._message = m;
	}		
}
