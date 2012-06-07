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
public abstract class GstrLiaisonFunction implements Gesture{
	private static final long serialVersionUID = 1L;
	private Plug myPlug;
	private Vector<GstrLiaisonFunctionLstnr> myListeners;

	private boolean isClient;
	private boolean isServer;
	private int myClient;
	private int myServer;

	public int requestType;
	public int responseType;

	private boolean openForLiaison;
	
	public GstrLiaisonFunction(Plug p){
		myPlug = p;
		myListeners = new Vector<GstrLiaisonFunctionLstnr>();
		openForLiaison = false;	
		isClient = false;
		isServer = false;
	}

	public void registerListener(GstrLiaisonFunctionLstnr l){
		myListeners.add(l);
	}

	public void open(){
		openForLiaison = true;
		Px1MessageInternalLiaison ml = new Px1MessageInternalLiaison();
		ml.createLiaison(requestType);
		ml.routeInternalyToBoard();
		//Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:send request for Function Liaison");
		myPlug.outgoing(ml);
	}
	
	public void close(){
		openForLiaison = false;
		// if a client liaison is open, it has to be closed
		if(isClient){
			Px1MessageInternalLiaison ml = new Px1MessageInternalLiaison();
			ml.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_ClosedForFunction);
			ml.routeInternalyToSocketID(myServer);
			Debugger.getInstance().verboseMessage(this.getClass(),"closing for Function Liaison sent by Socket:" + myPlug.parameter.getSocketID() + " to Socket: "+myServer);
			closingLiaisonForFunction(ml);
			isClient = false;
		}
	}

	public void incomming(Message msg){
		if(msg.myType == (Message.TYPE_LIAISON)){
			Px1MessageInternalLiaison ml = (Px1MessageInternalLiaison)msg;
			if(ml.isLiaisonType(responseType)){
				if(openForLiaison && !isServer){
					isServer = true;
					myClient = ml.socketID_FROM;
					Debugger.getInstance().verboseMessage(this.getClass(), "opening for Function Liaison received by Socket:" + myPlug.parameter.getSocketID() + " from Socket: " + myClient);
					createdServerLiaisonForFunction(myClient);
					Px1MessageInternalLiaison newML = new Px1MessageInternalLiaison();
					newML.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_AcknolegedForFunction);
					newML.routeInternalyToSocketID(myClient);
					Debugger.getInstance().verboseMessage(this.getClass(), "sending accknowledgement for Function Liaison by Socket:" + myPlug.parameter.getSocketID());
					myPlug.outgoing(newML);
				}
			}else if(ml.isLiaisonType(Px1MessageInternalLiaison.LIAISON_TYPE_AcknolegedForFunction)){
				myServer = ml.socketID_FROM;
				if(openForLiaison && !isClient){
					isClient = true;
					Debugger.getInstance().verboseMessage(this.getClass(), "accknowledgement for Function Liaison received by Socket:" + myPlug.parameter.getSocketID() + " from Socket: " + myServer);
					createdClientLiaisonForFunction(myServer);
				} else { 
					/* if this button gets an acknoleged by the server but rejects
					 * it now, it has to let the server know that the liaison should be closed again.
					 */
					Px1MessageInternalLiaison newML = new Px1MessageInternalLiaison();
					newML.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_ClosedForFunction);
					newML.routeInternalyToSocketID(myServer);
					Debugger.getInstance().verboseMessage(this.getClass(), "dropping of Function Liaison by Socket:" + myPlug.parameter.getSocketID());
					myPlug.outgoing(newML);
				}
			}else if(ml.isLiaisonType(Px1MessageInternalLiaison.LIAISON_TYPE_ClosedForFunction)){
				if(isServer){
					isServer = false;
					Debugger.getInstance().verboseMessage(this.getClass(), "closing of Function Liaison received by Socket:" + myPlug.parameter.getSocketID());
					liaisonForFunctionClosedByClient();
				}
			}
		}
	}

	private void createdServerLiaisonForFunction(int socketID_FROM){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrLiaisonFunctionLstnr)myListeners.get(i)).createdServerLiaisonForFunction(socketID_FROM);
		}
	}

	private void createdClientLiaisonForFunction(int socketID_FROM){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrLiaisonFunctionLstnr)myListeners.get(i)).createdClientLiaisonForFunction(socketID_FROM);
		}
	}

	private void closingLiaisonForFunction(Px1MessageInternalLiaison msg){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrLiaisonFunctionLstnr)myListeners.get(i)).closingLiaisonForFunction(msg);
		}
	}

	private void liaisonForFunctionClosedByClient(){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrLiaisonFunctionLstnr)myListeners.get(i)).liaisonForFunctionClosedByClient();
		}
	}

	public void reSurrect(){;}
	
	public void destroy(){;}

	public void comatize(){
		if(isClient){
			Px1MessageInternalLiaison ml = new Px1MessageInternalLiaison();
			ml.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_ClosedForFunction);
			ml.routeInternalyToSocketID(myServer);
			closingLiaisonForFunction(ml);
			isClient = false;
		}
		if(isServer){
			isServer = false;
			liaisonForFunctionClosedByClient();
		}
	}
	
	public void reAnimate(){;}
	

}
