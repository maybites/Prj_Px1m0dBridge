package ch.maybites.px1m0d.plug.gestures;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.*;
import ch.maybites.px1m0d.plug.drawing.effects.EffctLampBlinker;
import ch.maybites.px1m0d.plug.gestures.listeners.*;
import ch.maybites.px1m0d.message.*;

import java.util.*;

/*
 * "GstrPipeConnctServerAuto" handles the pipe connection between plugs. this also creates
 * automatically connections with plugs in its neighbourhood and direction and
 * helps managing it
 * 
 * it requires to listen to all the implemented interfaces
 */
public class GstrPipeConnctServerAuto implements Gesture, GstrPluggingLstnr{
	private static final long serialVersionUID = 1L;
	private Plug myPlug;
	private Vector<GstrPipeConnctManagerLstnr> myListeners;

	private Vector<PipeConnection> myConnections;

	// contain all the connections that need to be made after analysing the
	// jobs in the configuration file
	private Vector<Integer> myPipeMessageConnections;
	
	public GstrPipeConnctServerAuto(Plug p){
		myPlug = p;
		myListeners = new Vector<GstrPipeConnctManagerLstnr>();
		myConnections = new Vector<PipeConnection>();
	}

	public void registerListener(GstrPipeConnctManagerLstnr l){
		myListeners.add(l);
	}

	public void incomming(Message msg){
		if(msg.myType == (Message.TYPE_LIAISON)){
			Px1MessageInternalLiaison ml = (Px1MessageInternalLiaison)msg;
			if(ml.isLiaisonType(Px1MessageInternalLiaison.LIAISON_TYPE_PingForPipeResponse)){
				for(int i = 0; i < myPipeMessageConnections.size(); i++){
					PipeConnection con = new PipeConnection(myPipeMessageConnections.get(i).intValue(),  myPlug.myUniqueID, ml.socketID_FROM, ml.plugUniqueID_FROM);
					Debugger.getInstance().verboseMessage(this.getClass(),"createdServerLiaisonForPipe() channel: " + con.getChannel() + " uniqueID: " + ml.plugUniqueID_FROM);
					Px1MessageInternalCommand cmd = new Px1MessageInternalCommand();
					cmd.routeInternalyToSocketID(ml.socketID_FROM);
					if(!myConnections.contains(con)){
						Debugger.getInstance().debugMessage(getClass(),"add Pipe channel: " + con.getChannel() + " from socketid = " + myPlug.parameter.getSocketID() + "  to socketID: " + ml.socketID_FROM);
						myConnections.add(con);
						createAddPipeEffect();
						cmd.createEstablishPipeMessage(con);
						myPlug.outgoing(cmd);
						createdPipeConnection(con);
					} else if(myConnections.contains(con)){
						Debugger.getInstance().debugMessage(getClass(),"remove Pipe channel: " + con.getChannel() + " from socketid = " + myPlug.parameter.getSocketID() + "  to socketID: " + ml.socketID_FROM);
						myConnections.remove(con);
						createRemovePipeEffect();
						cmd.createReMovePipeMessage(con);
						myPlug.outgoing(cmd);
						removedPipeConnection(con);
					}
				}
			}			
		}
		if(msg.myType == Message.TYPE_COMMAND){
			Debugger.getInstance().verboseMessage(getClass(),"Plug with mySocketID: "+ myPlug.parameter.getSocketID() +" receives Command Message.. ");
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
				createRemovePipeEffect();
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

	private void createRemovePipeEffect(){
		int[] sequence = {0, 5, 10, 15, 20, 25, 30};
		myPlug.painter.addEffect(new EffctLampBlinker(50, sequence, false));
	}
	
	private void createAddPipeEffect(){
		int[] sequence = {0, 5, 10, 15, 20};
		myPlug.painter.addEffect(new EffctLampBlinker(50, sequence, false));
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

	// it sends a ping for pipe request message to its immediate neighbour that lies in its 
	// direction
	public void pluggedIN() {
		myPipeMessageConnections = listPipeChannels();
		int xTarget = myPlug.parameter.getXPos() - myPlug.parameter.getXDirection();
		int yTarget = myPlug.parameter.getYPos() - myPlug.parameter.getYDirection();
		int targetSocketID = (yTarget % 8) * 8 + (xTarget % 8);
		Px1MessageInternalLiaison mlNew = new Px1MessageInternalLiaison();
		mlNew.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_PingForPipeRequest);
		mlNew.routeInternalyToSocketID(targetSocketID);
		myPlug.outgoing(mlNew);
	}

	public void pluggedOUT() {
		// TODO Auto-generated method stub
		
	}
	
	private Vector<Integer> listPipeChannels(){
		Vector<Message> messages = myPlug.parameter.MessageFactory(true);
		Vector<Integer> collection = new Vector<Integer>();
		for(int i = 0; i < messages.size(); i++){
			if(messages.get(i).myType == Message.TYPE_PIPE){
				Integer pc = new Integer(((Px1MessageInternalPipe)messages.get(i)).getChannel());
				if(!collection.contains(pc)){
					collection.add(pc);
				}
			}
		}
		return collection;
	}
}
