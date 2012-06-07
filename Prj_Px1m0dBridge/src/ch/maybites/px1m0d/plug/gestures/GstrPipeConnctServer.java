package ch.maybites.px1m0d.plug.gestures;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.*;
import ch.maybites.px1m0d.plug.drawing.effects.EffctLampBlinker;
import ch.maybites.px1m0d.plug.gestures.listeners.*;
import ch.maybites.px1m0d.message.*;

import java.util.*;

/*
 * "GstrPipeConnctServer" handles the pipe connection between plugs. this means it helps the
 * plugs to recognise if a connection has been established, what kind of connection, and
 * helps managing it
 * 
 * it requires to listen to all the implemented interfaces
 */
public class GstrPipeConnctServer implements Gesture, GstrButtonLstnr, GstrLiaisonPipeServerLstnr{
	private static final long serialVersionUID = 1L;
	private Plug myPlug;
	private Vector<GstrPipeConnctManagerLstnr> myListeners;

	private Vector<PipeConnection> myConnections;

	private SymbolFunction functionMessage;
	
	public GstrPipeConnctServer(Plug p){
		myPlug = p;
		myListeners = new Vector<GstrPipeConnctManagerLstnr>();
		myConnections = new Vector<PipeConnection>();
	}

	public void registerListener(GstrPipeConnctManagerLstnr l){
		myListeners.add(l);
	}

	public void incomming(Message msg){
		if(msg.myType == Message.TYPE_FUNCTION){
			Debugger.getInstance().verboseMessage(getClass(),"Plug with mySocketID: "+ myPlug.parameter.getSocketID() +" receives Function Message.. ");
			Px1MessageInternalFunction m = (Px1MessageInternalFunction)msg;
			if(m.isSymbolFromServerMessage()){
				Debugger.getInstance().debugMessage(getClass(), "SymbolFunction channel: " + m.getSymbolMessage().getChannel() + " function: " + m.getSymbolMessage().getFunction() % 10);
				if(m.getSymbolMessage().isCreatePipeConnectionFunction() || m.getSymbolMessage().isDestroyPipeConnectionFunction()){
					functionMessage = m.getSymbolMessage();
					/**
					debug.debugMessage(getClass(),"functionMessage.getDepth() = " + functionMessage.getDepth());
					debug.debugMessage(getClass(),"                getChannel() = " + functionMessage.getChannel());
					debug.debugMessage(getClass(),"                getFunction() = " + functionMessage.getFunction());
					debug.debugMessage(getClass(),"                getSubFunction() = " + functionMessage.getSubFunction());
					**/
				} else {
					Debugger.getInstance().debugMessage(getClass(),"functionMessage disregarded, invalid functionChannel");
				}
			}
		}else if(msg.myType == Message.TYPE_COMMAND){
			Debugger.getInstance().verboseMessage(this.getClass(),"Plug with mySocketID: "+ myPlug.parameter.getSocketID() +" receives Command Message.. ");
			Debugger.getInstance().verboseMessage(this.getClass(),"myConnections.size(): " + myConnections.size());
			Px1MessageInternalCommand m = (Px1MessageInternalCommand)msg;
			if(m.isReMovePipeMessage()){
				long uid = m.getUniqueID();
				for(int i = (myConnections.size() - 1); i >= 0; i--){
					if(((PipeConnection)myConnections.get(i)).getUniqueID_TO() == uid){
						removedPipeConnection((PipeConnection)myConnections.get(i));
						myConnections.remove(i);
					}
				}
			} else if(m.isSuspendPipeMessage()){
				long uid = m.getUniqueID();
				for(int i = (myConnections.size() - 1); i >= 0; i--){
					if(((PipeConnection)myConnections.get(i)).getUniqueID_TO() == uid){
						suspendPipeConnection((PipeConnection)myConnections.get(i));
						//myConnections.remove(i);
					}
				}
			} else if(m.isReWirePipeMessage()){
				long uid = m.getUniqueID();
				for(int i = 0; i < myConnections.size(); i++){
					PipeConnection pc = (PipeConnection)myConnections.get(i);
					if(pc.getUniqueID_TO() == uid){
						int socketID = m.socketID_FROM;
						pc.rewireSocketID(socketID);
						reWiredPipeConnection(pc);
					}
				}
			}
		}
	}

	public void reSurrect(){;}

	public void destroy(){
		for(int i = (myConnections.size() - 1); i >= 0; i--){
			PipeConnection pc = (PipeConnection)myConnections.get(i);
			Px1MessageInternalCommand cmd = new Px1MessageInternalCommand();
			cmd.routeInternalyToUID(pc.getUniqueID_TO());
			cmd.createReMovePipeMessage(pc);
			myPlug.outgoing(cmd);
			myConnections.remove(i);
		}
	}

	public void comatize() {;}

	public void reAnimate() {;}

	public void createdServerLiaisonForPipe(int socketID_Client, long uniqueID_Client) {
		if(functionMessage != null){
			PipeConnection con = new PipeConnection(functionMessage, socketID_Client, uniqueID_Client, myPlug.myUniqueID);
//			Debugger.getInstance().debugMessage(getClass(),"createdServerLiaisonForPipe() channel: " + con.getChannel() + " uniqueID: " + uniqueID_Client);
			Px1MessageInternalCommand cmd = new Px1MessageInternalCommand();
			cmd.routeInternalyToSocketID(socketID_Client);
			if(functionMessage.isCreatePipeConnectionFunction() && !myConnections.contains(con)){
				Debugger.getInstance().debugMessage(getClass(),"add Pipe channel: " + con.getChannel() + " from socketid = " + myPlug.parameter.getSocketID() + "  to socketID: " + socketID_Client+ "  to uniquePlugID: " + uniqueID_Client);
				myConnections.add(con);
				createAddPipeEffect();
				cmd.createEstablishPipeMessage(con);
				myPlug.outgoing(cmd);
				createdPipeConnection(con);
			} else if(functionMessage.isDestroyPipeConnectionFunction() && myConnections.contains(con)){
				Debugger.getInstance().debugMessage(getClass(),"remove Pipe channel: " + con.getChannel() + " from socketid = " + myPlug.parameter.getSocketID() + "  to socketID: " + socketID_Client);
				myConnections.remove(con);
				createRemovePipeEffect();
				cmd.createReMovePipeMessage(con);
				myPlug.outgoing(cmd);
				removedPipeConnection(con);
			} else {
				Debugger.getInstance().verboseMessage(this.getClass(),"call for pipe connection disregarded for uniqueID: " + uniqueID_Client);
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

	public void liaisonForPipeClosedByClient() {;}

	public void buttonPressed() {;}

	public void buttonReleased() {
		functionMessage = null;
	}

	private void createdPipeConnection(PipeConnection con){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrPipeConnctManagerLstnr)myListeners.get(i)).createdPipeConnection(con);
		}
	}

	private void removedPipeConnection(PipeConnection con){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrPipeConnctManagerLstnr)myListeners.get(i)).removedPipeConnection(con);
		}
	}

	private void suspendPipeConnection(PipeConnection con){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrPipeConnctManagerLstnr)myListeners.get(i)).suspendPipeConnection(con);
		}
	}

	private void reWiredPipeConnection(PipeConnection con){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrPipeConnctManagerLstnr)myListeners.get(i)).reWiredPipeConnection(con);
		}
	}

}
