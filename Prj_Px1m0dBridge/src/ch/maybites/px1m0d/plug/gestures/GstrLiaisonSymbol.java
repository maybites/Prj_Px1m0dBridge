package ch.maybites.px1m0d.plug.gestures;

import ch.maybites.px1m0d.plug.Plug;
import ch.maybites.px1m0d.plug.gestures.listeners.*;
import ch.maybites.px1m0d.message.*;

import java.util.*;

/*
 * "GstrLiaison" handles the liaisons between plugs. this means it helps the
 * plugs to recognise if a liaison needs to be established, what kind of liaison, and
 * helps closing it again
 */
public class GstrLiaisonSymbol implements Gesture, GstrMultipleClickLstnr{

	private static final long serialVersionUID = 1L;
	private Plug myPlug;
	private Vector<GstrLiaisonSymbolLstnr> myListeners;

	private boolean isClient;
	private boolean isServer;
	private int myClient;
	private int myServer;

	public int messageType = Message.TYPE_SYMBOL;

	public int requestRoute = Message.ROUTE_BOARD_HOOD;
	public int requestType = Px1MessageInternalLiaison.LIAISON_TYPE_RequestForSymbol;
	public int closedType  = Px1MessageInternalLiaison.LIAISON_TYPE_ClosedForSymbol;


	boolean openForLiaison;

	public GstrLiaisonSymbol(Plug p){
		myPlug = p;
		myListeners = new Vector<GstrLiaisonSymbolLstnr>();
		openForLiaison = false;		
	}

	public void registerListener(GstrLiaisonSymbolLstnr l){
		myListeners.add(l);
	}

	private void open(){
		openForLiaison = true;
		Px1MessageInternalLiaison ml = new Px1MessageInternalLiaison();
		ml.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_RequestForSymbol);
		ml.routeInternalyToNeighbour(myPlug.parameter.getSocketID());
		myPlug.outgoing(ml);
	}

	private void close(){
		openForLiaison = false;
		// if a client liaison is open, it has to be closed
		if(isClient){
			Px1MessageInternalLiaison ml = new Px1MessageInternalLiaison();
			ml.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_ClosedForSymbol);
			ml.routeInternalyToSocketID(myServer);
			closingLiaisonForSymbol(ml);
			isClient = false;
		}
	}

	public void incomming(Message msg){
		if(msg.myType == (Message.TYPE_LIAISON)){
			Px1MessageInternalLiaison ml = (Px1MessageInternalLiaison)msg;
			if(ml.isLiaisonType(Px1MessageInternalLiaison.LIAISON_TYPE_RequestForSymbol)){
				if(openForLiaison && !isServer){
					isServer = true;
					myClient = msg.socketID_FROM;
					createdServerLiaisonForSymbol(myClient);
					Px1MessageInternalLiaison mlNew = new Px1MessageInternalLiaison();
					mlNew.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_AcknolegedForSymbol);
					mlNew.routeInternalyToSocketID(myClient);
					myPlug.outgoing(mlNew);

				}
			}
			if(ml.isLiaisonType(Px1MessageInternalLiaison.LIAISON_TYPE_AcknolegedForSymbol)){
				myServer = ml.socketID_FROM;
				if(openForLiaison && !isClient){
					isClient = true;
					createdClientLiaisonForSymbol(myServer);
				} else { 
					/* if this button gets an acknoleged by the server but rejects
					 * it now, it has to let the server know that the liaison should be closed again.
					 */
					Px1MessageInternalLiaison mlNew = new Px1MessageInternalLiaison();
					mlNew.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_ClosedForSymbol);
					mlNew.routeInternalyToSocketID(myServer);
					myPlug.outgoing(mlNew);
				}

			}
			if(ml.isLiaisonType(Px1MessageInternalLiaison.LIAISON_TYPE_ClosedForSymbol)){
				if(isServer){
					isServer = false;
					liaisonForSymbolClosedByClient();
				}
			}
		}
		if(msg.myType == (Message.TYPE_SYMBOL)){
			symbolMessage((Px1MessageInternalSymbol)msg);
		}

	}

	private void createdServerLiaisonForSymbol(int socketID_FROM){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrLiaisonSymbolLstnr)myListeners.get(i)).createdServerLiaisonForSymbol(socketID_FROM);
		}
	}

	private void createdClientLiaisonForSymbol(int socketID_FROM){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrLiaisonSymbolLstnr)myListeners.get(i)).createdClientLiaisonForSymbol(socketID_FROM);
		}
	}

	private void closingLiaisonForSymbol(Px1MessageInternalLiaison msg){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrLiaisonSymbolLstnr)myListeners.get(i)).closingLiaisonForSymbol(msg);
		}
	}

	private void liaisonForSymbolClosedByClient(){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrLiaisonSymbolLstnr)myListeners.get(i)).liaisonForSymbolClosedByClient();
		}
	}

	private void symbolMessage(Px1MessageInternalSymbol msg){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrLiaisonSymbolLstnr)myListeners.get(i)).symbolMessage(msg);
		}
	}

	public void reSurrect(){;}

	public void destroy(){;}

	public void comatize(){
		if(isClient){
			Px1MessageInternalLiaison ml = new Px1MessageInternalLiaison();
			ml.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_ClosedForSymbol);
			ml.routeInternalyToSocketID(myServer);
			closingLiaisonForSymbol(ml);
			isClient = false;
		}
		if(isServer){
			isServer = false;
			liaisonForSymbolClosedByClient();
		}
	}

	public void reAnimate(){;}

	public void multiplePressed(int noOfClicks) {
		open();
	}

	public void multipleReleased(int noOfClicks) {
		close();
	}
}
