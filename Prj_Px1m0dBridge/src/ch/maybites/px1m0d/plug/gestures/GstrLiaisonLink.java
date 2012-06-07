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
public class GstrLiaisonLink implements Gesture, GstrButtonLstnr{
	private static final long serialVersionUID = 1L;
	private Plug myPlug;
	private Vector<GstrLiaisonLinkLstnr> myListeners;

	private boolean isClient;
	private boolean isServer;
	private int myClient, myServer;
	private long myClientUID, myServerUID;

	private boolean openForLiaison;

	public GstrLiaisonLink(Plug p){
		myPlug = p;
		myListeners = new Vector<GstrLiaisonLinkLstnr>();
		openForLiaison = false;		
	}

	public void registerListener(GstrLiaisonLinkLstnr l){
		myListeners.add(l);
	}

	public void open(){
		openForLiaison = true;
		Px1MessageInternalLiaison ml = new Px1MessageInternalLiaison();
		ml.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_RequestForLink);
		ml.routeInternalyToBoard();
		myPlug.outgoing(ml);
	}

	public void close(){
		openForLiaison = false;
		// if a client liaison is open, it has to be closed
		if(isClient){
			Px1MessageInternalLiaison ml = new Px1MessageInternalLiaison();
			ml.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_ClosedForLink);
			ml.routeInternalyToSocketID(myServer);
			closingLiaisonForLink(ml);
			isClient = false;
		}
	}

	public void incomming(Message msg){
		if(msg.myType == (Message.TYPE_LIAISON)){
			Px1MessageInternalLiaison ml = (Px1MessageInternalLiaison)msg;
			if(ml.isLiaisonType(Px1MessageInternalLiaison.LIAISON_TYPE_RequestForLink)){
				if(openForLiaison && !isServer){
					isServer = true;
					myClient = ml.socketID_FROM;
					myClientUID = ml.plugUniqueID_FROM;
					createdServerLiaisonForLink(myClient, myClientUID);
					Px1MessageInternalLiaison mlNew = new Px1MessageInternalLiaison();
					mlNew.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_AcknolegedForLink);
					mlNew.routeInternalyToSocketID(myClient);
					myPlug.outgoing(mlNew);
				}
			}else if(ml.isLiaisonType(Px1MessageInternalLiaison.LIAISON_TYPE_AcknolegedForLink)){
				myServer = msg.socketID_FROM;
				myServerUID = ml.plugUniqueID_FROM;
				if(openForLiaison && !isClient){
					isClient = true;
					createdClientLiaisonForLink(myServer, myServerUID);
				} else { 
					/* if this button gets an acknoleged by the server but rejects
					 * it now, it has to let the server know that the liaison should be closed again.
					 */
					Px1MessageInternalLiaison mlNew = new Px1MessageInternalLiaison();
					mlNew.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_ClosedForLink);
					mlNew.routeInternalyToSocketID(myServer);
					myPlug.outgoing(mlNew);
				}
			}else if(ml.isLiaisonType(Px1MessageInternalLiaison.LIAISON_TYPE_ClosedForLink)){
				if(isServer){
					isServer = false;
					liaisonForLinkClosedByClient();
				}
			}
		}
		if(msg.myType == (Message.TYPE_COMMAND)){
			linkMessage((Px1MessageInternalCommand)msg);
		}

	}

	private void createdServerLiaisonForLink(int socketID_FROM, long uniqueID_FROM){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrLiaisonLinkLstnr)myListeners.get(i)).createdServerLiaisonForLink(socketID_FROM, uniqueID_FROM);
		}
	}

	private void createdClientLiaisonForLink(int socketID_FROM, long uniqueID_FROM){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrLiaisonLinkLstnr)myListeners.get(i)).createdClientLiaisonForLink(socketID_FROM, uniqueID_FROM);
		}
	}

	private void closingLiaisonForLink(Px1MessageInternalLiaison msg){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrLiaisonLinkLstnr)myListeners.get(i)).closingLiaisonForLink(msg);
		}
	}

	private void liaisonForLinkClosedByClient(){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrLiaisonLinkLstnr)myListeners.get(i)).liaisonForLinkClosedByClient();
		}
	}

	private void linkMessage(Px1MessageInternalCommand msg){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrLiaisonLinkLstnr)myListeners.get(i)).linkMessage(msg);
		}
	}

	public void reSurrect(){;}

	public void destroy(){;}

	public void comatize(){
		if(isClient){
			Px1MessageInternalLiaison ml = new Px1MessageInternalLiaison();
			ml.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_ClosedForLink);
			ml.routeInternalyToSocketID(myServer);
			closingLiaisonForLink(ml);
			isClient = false;
		}
		if(isServer){
			isServer = false;
			liaisonForLinkClosedByClient();
		}
	}

	public void reAnimate(){;}


	public void buttonPressed() {
		open();
	}

	public void buttonReleased() {
		close();
	}
}