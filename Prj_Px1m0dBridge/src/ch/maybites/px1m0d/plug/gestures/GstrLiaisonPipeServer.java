package ch.maybites.px1m0d.plug.gestures;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.Plug;
import ch.maybites.px1m0d.plug.gestures.listeners.*;
import ch.maybites.px1m0d.message.*;

import java.util.*;

/*
 * "GstrLiaison" handles the liaisons between plugs. this means it helps the
 * plugs to recognise if a liaison needs to be established, what kind of liaison, and
 * helps closing it again
 */
public class GstrLiaisonPipeServer implements Gesture, GstrButtonLstnr{
	private static final long serialVersionUID = 1L;
	private Plug myPlug;
	private Vector<GstrLiaisonPipeServerLstnr> myListeners;

	private boolean isServer;
	private int myClient;
	private long myClientsUID;

	private boolean openForLiaison; // allows liaisons only while the button is pressed

	public GstrLiaisonPipeServer(Plug p){
		myPlug = p;
		myListeners = new Vector<GstrLiaisonPipeServerLstnr>();
		openForLiaison = false;		
		isServer = false;
	}

	public void registerListener(GstrLiaisonPipeServerLstnr l){
		myListeners.add(l);
	}

	public void incomming(Message msg){
		if(msg.myType == (Message.TYPE_LIAISON)){
			Px1MessageInternalLiaison ml = (Px1MessageInternalLiaison)msg;
			if(ml.isLiaisonType(Px1MessageInternalLiaison.LIAISON_TYPE_RequestForPipe)){
				if(openForLiaison && !isServer){
					isServer = true;
					myClient = ml.socketID_FROM;
					myClientsUID = ml.plugUniqueID_FROM;
					Debugger.getInstance().verboseMessage(this.getClass(), "opening for Pipe Liaison received by Socket:" + myPlug.parameter.getSocketID() + " from Socket: " + myClient);
					createdServerLiaisonForPipe(myClient, myClientsUID);
					Px1MessageInternalLiaison mlNew = new Px1MessageInternalLiaison();
					mlNew.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_AcknolegedForPipe);
					mlNew.routeInternalyToSocketID(myClient);
					Debugger.getInstance().verboseMessage(this.getClass(), "sending accknowledgement for Pipe Liaison by Socket:" + myPlug.parameter.getSocketID() + " to Socket: " + myClient);
					myPlug.outgoing(mlNew);
				}
			}else if(ml.isLiaisonType(Px1MessageInternalLiaison.LIAISON_TYPE_ClosedForPipe)){
				if(isServer){
					Debugger.getInstance().verboseMessage(this.getClass(), "closing of Pipe Liaison received by Socket:" + myPlug.parameter.getSocketID());
					isServer = false;
					liaisonForPipeClosedByClient();
				}
			}
		}
	}

	private void createdServerLiaisonForPipe(int socketID_FROM, long uniqueID_FROM){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrLiaisonPipeServerLstnr)myListeners.get(i)).createdServerLiaisonForPipe(socketID_FROM, uniqueID_FROM);
		}
	}

	private void liaisonForPipeClosedByClient(){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrLiaisonPipeServerLstnr)myListeners.get(i)).liaisonForPipeClosedByClient();
		}
	}

	public void reSurrect(){;}

	public void destroy(){;}

	public void comatize(){
		if(isServer){
			Debugger.getInstance().verboseMessage(this.getClass(), "comatize(): closing of Pipe Liaison received by Socket:" + myPlug.parameter.getSocketID());
			isServer = false;
			liaisonForPipeClosedByClient();
		}
	}

	public void reAnimate(){;}

	public void buttonPressed() {
		openForLiaison = true;
	}

	public void buttonReleased() {
		openForLiaison = false;
	}

}
