package ch.maybites.px1m0d.plug.behaviours;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.message.Message;
import ch.maybites.px1m0d.message.Px1MessageInternalPipe;
import ch.maybites.px1m0d.plug.gestures.listeners.*;
import ch.maybites.px1m0d.plug.*;
import ch.maybites.px1m0d.plug.drawing.Painter;
import ch.maybites.px1m0d.plug.drawing.effects.*;
import gestalt.shape.Color;

import java.util.*;

/*
 * Sends the Pipe Messages to the connected Plugs.
 */
public class BhavePipeConnctServer implements Behaviour,  GstrPipeConnctReceiverLstnr, GstrPipeConnctManagerLstnr, GstrSwitchLstnr, GstrNatebuLstnr, GstrMetronomeLstnr{
	private static final long serialVersionUID = 1L;
	protected Plug myPlug;
	protected Painter myPainter;
	protected EffctPipes myPipeVisuals;

	protected Vector<PipeConnection> myConnections;

	protected boolean pipeChannelHasChanged = false;

	public BhavePipeConnctServer(Plug p, Painter pt){
		myPlug = p;
		myPainter = pt;
		myConnections = new Vector<PipeConnection>();
		Color c = p.parameter.getIconColor();
		myPipeVisuals = new EffctPipes(myPlug, c);
		myPainter.addEffect(myPipeVisuals);
	}

	public void action(){
		Vector<Message> messages = myPlug.parameter.MessageFactory();
		for(int i = 0; i < messages.size(); i++){
			// the first defined message must be a pipe message.
			// it will be sent to all connections
			if(i == 0 && messages.get(i).myType == Message.TYPE_PIPE){
				if(myPlug.parameter.switchIsOn()){
					for(int j = 0; j < myConnections.size(); j++){
						PipeConnection pc = myConnections.elementAt(j);
						Px1MessageInternalPipe pipeMessage = ((Px1MessageInternalPipe)messages.get(i)).clone();
						pipeMessage.setChannel(pc.getChannel());
						pipeMessage.routeInternalyToSocketID(pc.getSocketID_TO());
						myPlug.outgoing(pipeMessage);
						myPipeVisuals.valueChange(pc.getSocketID_TO(), pc.getChannel(), pipeMessage.getAbsoluteValue());
						//Debugger.getInstance().debugMessage(getClass(),"Plug with mySocketID: "+ myPlug.parameter.getSocketID() +" sends pipe value Message to " + pc.getSocketID_TO());
					}
				}
			} else {
				//Debugger.getInstance().debugMessage(this.getClass(), "plug is sending midi message: " + myPlug.parameter.getPlugID() + " values: "  + ((javax.sound.midi.ShortMessage)((Message)messages.get(i)).getValue(Message.KEY_MidiMessage)).getData2());
				myPlug.outgoing(messages.get(i));
			}
		}
		pipeChannelHasChanged = false;
	}

	public boolean takeControl(){
		return pipeChannelHasChanged;
	}

	public void createdPipeConnection(PipeConnection con) {
		myConnections.add(con);
		myPipeVisuals.addChannel(con.getSocketID_TO(), con.getChannel());
		Debugger.getInstance().debugMessage(getClass(), "createdPipeConnection("+con.getSocketID_TO()+") from UniqueID" + myPlug.myUniqueID);
	}

	public void reWiredPipeConnection(PipeConnection con) {
		myConnections.add(con);
		myPipeVisuals.addChannel(con.getSocketID_TO(), con.getChannel());
		Debugger.getInstance().debugMessage(getClass(), "reWiredPipeConnection("+con.getSocketID_TO()+") from UniqueID" + myPlug.myUniqueID);
	}

	public void removedPipeConnection(PipeConnection con) {
		myConnections.remove(con);
		myPipeVisuals.removeChannel(con.getSocketID_TO(), con.getChannel());
		Debugger.getInstance().debugMessage(getClass(), "removedPipeConnection("+con.getSocketID_TO()+") from UniqueID" + myPlug.myUniqueID);
	}

	public void suspendPipeConnection(PipeConnection con) {
		myConnections.remove(con);
		myPipeVisuals.removeChannel(con.getSocketID_TO(), con.getChannel());
		Debugger.getInstance().debugMessage(getClass(), "suspendPipeConnection("+con.getSocketID_TO()+") from UniqueID" + myPlug.myUniqueID);
	}

	public void pipeValueChange(int pipe) {
		pipeChannelHasChanged = true;
	}

	public void reSurrect(){;}

	public void destroy(){;}

	public void comatize() {;}

	public void reAnimate() {;}

	public void switchedOFF() {
		myPipeVisuals.animate(false);
	}

	public void switchedON() {
		myPipeVisuals.animate(true);
	}

	public void natebuUIChange() {
		pipeChannelHasChanged = true;
	}

	public void natebuValue(float value) {
		pipeChannelHasChanged = true;
	}

	public void metronomeClicked() {
		pipeChannelHasChanged = true;
	}

}
