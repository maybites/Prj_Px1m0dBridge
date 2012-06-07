
package ch.maybites.px1m0d.plug.gestures;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.*;
import ch.maybites.px1m0d.plug.gestures.listeners.*;
import ch.maybites.px1m0d.message.*;
import ch.maybites.px1m0d.plug.drawing.effects.*;

import java.util.*;

/*
 * "GstrPipeConnctReceiver" handles the pipe connection between plugs. this means it helps the
 * plugs to recognize if a connection has been established, and helps managing it.
 */
public class GstrPipeConnctReceiver implements Gesture, GstrPipeChannelLstnr, GstrSwitchLstnr{
	private static final long serialVersionUID = 1L;
	private Plug myPlug;
	private Vector<GstrPipeConnctReceiverLstnr> myPipeListeners;

	private Vector<PipeConnection> myConnections;

	protected EffctPipeReceiver myPipeReceivingVisuals;

	private boolean isRelativePipeListening = false;

	private boolean dataGate = false;

	public GstrPipeConnctReceiver(Plug p){
		myPlug = p;
		myPipeReceivingVisuals = new EffctPipeReceiver(myPlug);
		myPlug.painter.addEffect(myPipeReceivingVisuals);
		myConnections = new Vector<PipeConnection>();
		myPipeListeners = new Vector<GstrPipeConnctReceiverLstnr>();
	}

	public void registerListener(GstrPipeConnctReceiverLstnr l){
		myPipeListeners.add(l);
	}

	public void incomming(Message msg){
		if(msg.myType == Message.TYPE_PIPE){
			Px1MessageInternalPipe m = (Px1MessageInternalPipe)msg;
			//Debugger.getInstance().verboseMessage(this.getClass(),"Plug with mySocketID: "+ myPlug.parameter.getSocketID() +" receives Pipe Message. Channel: " + m.getChannel() + " ABSValue: "+m.getAbsoluteValue()+ " RELValue: "+m.getRelativeValue());
			if(isRelativePipeListening){
				if(m.isRelativeMessage())
					pipeRelativeValue(m.getChannel(), m.getRelativeValue());
			}
			if(m.isAbsoluteMessage()){
				pipeAbsoluteValue(m.getChannel(), m.getAbsoluteValue());
			}
		} else if(msg.myType == Message.TYPE_COMMAND){
			Debugger.getInstance().verboseMessage(this.getClass(),"Plug with SocketID: "+ myPlug.parameter.getSocketID() +" receives Command Message.. ");
			Px1MessageInternalCommand m = (Px1MessageInternalCommand)msg;
			if(m.isReMovePipeMessage()){
				PipeConnection con = m.getPipeConnection();
				Debugger.getInstance().debugMessage(getClass(), " received Pipe-Removal-Message from UniqueID: " + con.getUniqueID_FROM());
				removeConncetion(con);
			} else if(m.isEstablishPipeMessage()){
				PipeConnection con = m.getPipeConnection();
				Debugger.getInstance().debugMessage(getClass(), " received Pipe-Established-Message from UniqueID: " + con.getUniqueID_FROM() + " to my UniqueID: " + myPlug.myUniqueID + " for channel: " + con.getChannel());
				addConnection(con);
			}
		}
	}

	private void createRemovePipeEffect(){
		int[] sequence = {0, 5, 10, 15, 20, 25, 30};
		myPlug.painter.addEffect(new EffctLampBlinker(50, sequence, false));
	}

	private void createAddPipeEffect(){
		int[] sequence = {0, 5, 10, 15, 20};
		myPlug.painter.addEffect(new EffctLampBlinker(50, sequence, false));
	}

	private void removeConncetion(PipeConnection con){
		if(myConnections.contains(con)){
			myConnections.remove(con);
			myPipeReceivingVisuals.removeOneChannel(con.getChannel());
			//check if there are more connections with same channel
			boolean hasMoreConnectionsOfSameChannel = false;
			for(int i = 0; i < myConnections.size(); i++){
				if(myConnections.get(i).getChannel() == con.getChannel()){
					hasMoreConnectionsOfSameChannel = true;
				}
			}
			// if there are none, the plug parameter can be set:
			if(!hasMoreConnectionsOfSameChannel){
				myPlug.parameter.setPipeUsedConndition(con.getChannel(), false);
				myPipeReceivingVisuals.removeAllChannel(con.getChannel());
			}
			createRemovePipeEffect();
		}
	}

	private void addConnection(PipeConnection con){
		if(!myConnections.contains(con)){
			myConnections.add(con);
			myPlug.parameter.setPipeUsedConndition(con.getChannel(), true);
			createAddPipeEffect();
			myPipeReceivingVisuals.addChannel(con.getChannel());
		}
	}

	private void pipeAbsoluteValue(int channel, float value){
		if(dataGate){
			if(myPlug.parameter.setPipeValue(channel, value))
				callListeners(channel);
		}
	}

	private void pipeRelativeValue(int channel, float value){
		if(myPlug.parameter.addPipeValue(channel, value))
			callListeners(channel);
		myPipeReceivingVisuals.addChannel(channel);
	}

	private void callListeners(int channel){
		for(int i = 0; i < myPipeListeners.size(); i++){
			((GstrPipeConnctReceiverLstnr)myPipeListeners.get(i)).pipeValueChange(channel);
		}		
	}

	// is called to listen to global/relative pipemessages
	public void startListeningForPipe(int channel) {
		Debugger.getInstance().infoMessage(this.getClass(), "start listening to global pipe messages on channel: " + channel);
		isRelativePipeListening = true;
	}

	// is called if no more listening to global/relative pipemessages is required
	public void stopListeningForPipe() {
		Debugger.getInstance().infoMessage(this.getClass(), "stop listening to global pipe messages");
		isRelativePipeListening = false;
	}

	public void switchedOFF() {
		dataGate = false;
	}

	public void switchedON() {
		dataGate = true;
	}

	public void reSurrect(){;}

	public void destroy(){
		for(int i = (myConnections.size() - 1); i >= 0; i--){
			PipeConnection con = (PipeConnection)myConnections.get(i);
			Px1MessageInternalCommand cmd = new Px1MessageInternalCommand();
			cmd.routeInternalyToUID(con.getUniqueID_FROM());
			cmd.createReMovePipeMessage(con);
			myPlug.outgoing(cmd);
			myConnections.remove(i);
		}
	}

	public void comatize() {
		for(int i = 0; i < myConnections.size(); i++){
			PipeConnection con = (PipeConnection)myConnections.get(i);
			Px1MessageInternalCommand cmd = new Px1MessageInternalCommand();
			cmd.routeInternalyToUID(con.getUniqueID_FROM());
			cmd.createSuspendPipeMessage(con);
			myPlug.outgoing(cmd);
		}
	}

	public void reAnimate() {
		for(int i = 0; i < myConnections.size(); i++){
			PipeConnection con = (PipeConnection)myConnections.get(i);
			Px1MessageInternalCommand cmd = new Px1MessageInternalCommand();
			cmd.routeInternalyToUID(con.getUniqueID_FROM());
			cmd.createReWirePipeMessage(con);
			myPlug.outgoing(cmd);
		}
	}

}
