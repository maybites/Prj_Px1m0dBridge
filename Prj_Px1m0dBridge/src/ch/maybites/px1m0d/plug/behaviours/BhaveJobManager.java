package ch.maybites.px1m0d.plug.behaviours;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.gestures.listeners.*;
import ch.maybites.px1m0d.plug.*;
import ch.maybites.px1m0d.plug.drawing.*;
import de.cnc.expression.exceptions.*;
import ch.maybites.px1m0d.message.*;

import java.util.*;

public class BhaveJobManager implements Behaviour, GstrButtonLstnr, GstrSwitchLstnr, GstrPluggingLstnr, GstrNatebuLstnr, GstrMetronomeLstnr, GstrPipeConnctReceiverLstnr, GstrLiaisonLinkLstnr{
	private static final long serialVersionUID = 1L;
	private Plug myPlug;
	private Painter myPainter;

	boolean _myTakeControl = false;

	public BhaveJobManager(Plug p, Painter pt){
		myPlug = p;
		myPainter = pt;
	}

	public void action(){
		_myTakeControl = false;
		Vector<Message> messages = myPlug.parameter.MessageFactory();
		for(int i = 0; i < messages.size(); i++){
			//Debugger.getInstance().debugMessage(this.getClass(), "plug is sending midi message: " + myPlug.parameter.getPlugID() + " values: "  + ((javax.sound.midi.ShortMessage)((Message)messages.get(i)).getValue(Message.KEY_MidiMessage)).getData2());
			myPlug.outgoing(messages.get(i));
		}
	}

	public boolean takeControl(){
		return _myTakeControl;
	}

	public void switchedON(){
		_myTakeControl = true;
	}

	public void switchedOFF(){
		_myTakeControl = true;
	}

	public void pluggedIN(){
		_myTakeControl = true;
	}

	public void pluggedOUT(){
		_myTakeControl = true;
	}

	public void pipeValueChange(int pipe) {
		//Debugger.getInstance().debugMessage(this.getClass(), "pipeValueChange");
		_myTakeControl = true;
	}

	public void buttonPressed() {
		_myTakeControl = true;
	}

	public void buttonReleased() {
		_myTakeControl = true;
	}

	public void natebuUIChange() {
		_myTakeControl = true;
	}

	public void reSurrect(){;}

	public void destroy(){
		action();
	}

	public void comatize() {
		action();
	}

	public void reAnimate() {;}

	public void natebuValue(float value) {
		_myTakeControl = true;
	}

	public void metronomeClicked() {
		_myTakeControl = true;
	}

	public void closingLiaisonForLink(Px1MessageInternalLiaison msg) {
	}

	public void createdClientLiaisonForLink(int socketID_Server,
			long uniqueID_Server) {
		
	}

	public void createdServerLiaisonForLink(int socketID_Client,
			long uniqueID_Client) {
		
	}

	public void liaisonForLinkClosedByClient() {
	}

	public void linkMessage(Px1MessageInternalCommand msg) {
		_myTakeControl = true;
	}

}
