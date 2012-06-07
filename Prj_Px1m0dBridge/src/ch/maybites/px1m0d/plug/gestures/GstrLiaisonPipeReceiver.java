package ch.maybites.px1m0d.plug.gestures;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.Plug;
import ch.maybites.px1m0d.plug.gestures.listeners.*;
import ch.maybites.px1m0d.message.*;

import java.util.*;

/*
 * "GstrLiaisonPipeReceiver" handles the liaisons between plugs from the receiving end. 
 * this means it helps the plugs to recognise if a pipe liaison needs to be established.
 * usually it is a stub, meaning it doesnt pass on any infos regarding the liaison, even 
 * though it could via the  GstrLiaisonPipeReceiverLstnr interface. it only tells a liaison
 * requestor that this plug is willing to accept pipe messages established through user
 * gestures. it is the more sophisticated sister of GstrLiaisonPipePing. 
 */
public class GstrLiaisonPipeReceiver implements Gesture, GstrButtonLstnr{
	private static final long serialVersionUID = 1L;
	private Plug myPlug;
	private Vector<GstrLiaisonPipeReceiverLstnr> myListeners;

	private boolean isClient;
	private boolean isServer;
	private int myClient;
	private int myServer;
	private long myClientsUID;
	private long myServersUID;

	private boolean openForLiaison;

	public GstrLiaisonPipeReceiver(Plug p){
		myPlug = p;
		myListeners = new Vector<GstrLiaisonPipeReceiverLstnr>();
		openForLiaison = false;		
		isClient = false;
		isServer = false;
	}

	public void registerListener(GstrLiaisonPipeReceiverLstnr l){
		myListeners.add(l);
	}

	public void open(){
		openForLiaison = true;
		Px1MessageInternalLiaison ml = new Px1MessageInternalLiaison();
		ml.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_RequestForPipe);
		ml.routeInternalyToBoard();
		Debugger.getInstance().verboseMessage(this.getClass(), "request for Pipe Liaison sent by Socket: " + myPlug.parameter.getSocketID());
		myPlug.outgoing(ml);
	}

	public void close(){
		openForLiaison = false;
		// if a client liaison is open, it has to be closed
		if(isClient){
			Px1MessageInternalLiaison ml = new Px1MessageInternalLiaison();
			ml.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_ClosedForPipe);
			ml.routeInternalyToSocketID(myServer);
			Debugger.getInstance().verboseMessage(this.getClass(), "closing for Pipe Liaison sent by Socket: " + myPlug.parameter.getSocketID() + " to Socket: "+myServer);
			myPlug.outgoing(ml);
			closingLiaisonForPipe();
			isClient = false;
		}
	}

	public void incomming(Message msg){
		if(msg.myType == (Message.TYPE_LIAISON)){
			Px1MessageInternalLiaison ml = (Px1MessageInternalLiaison)msg;
			if(ml.isLiaisonType(Px1MessageInternalLiaison.LIAISON_TYPE_AcknolegedForPipe)){
				myServer = ml.socketID_FROM;
				myServersUID = ml.plugUniqueID_FROM;
				if(openForLiaison && !isClient){
					isClient = true;
					Debugger.getInstance().verboseMessage(this.getClass(), "accknowledgement for Pipe Liaison received by Socket:" + myPlug.parameter.getSocketID() + " from Socket: " + myServer);
					createdClientLiaisonForPipe(myServer, myServersUID);
				} else { 
					/* if this button gets an acknoleged by the server but rejects
					 * it now, it has to let the server know that the liaison should be closed again.
					 */
					Px1MessageInternalLiaison mlNew = new Px1MessageInternalLiaison();
					mlNew.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_ClosedForPipe);
					mlNew.routeInternalyToSocketID(myServer);
					Debugger.getInstance().verboseMessage(this.getClass(), "dropping of Pipe Liaison by Socket:" + myPlug.parameter.getSocketID());
					myPlug.outgoing(mlNew);
				}
			}
		}
	}

	private void createdClientLiaisonForPipe(int socketID_FROM, long uniqueID_FROM){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrLiaisonPipeReceiverLstnr)myListeners.get(i)).createdClientLiaisonForPipe(socketID_FROM, uniqueID_FROM);
		}
	}

	private void closingLiaisonForPipe(){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrLiaisonPipeReceiverLstnr)myListeners.get(i)).closingLiaisonForPipe();
		}
	}

	public void reSurrect(){;}

	public void destroy(){;}

	public void comatize(){
		if(isClient){
			Px1MessageInternalLiaison ml = new Px1MessageInternalLiaison();
			ml.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_ClosedForPipe);
			ml.routeInternalyToSocketID(myServer);
			Debugger.getInstance().verboseMessage(this.getClass(), "comatize(): closing for Pipe Liaison sent by Socket: " + myPlug.parameter.getSocketID() + " to Socket: "+myServer);
			myPlug.outgoing(ml);
			closingLiaisonForPipe();
			isClient = false;
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
