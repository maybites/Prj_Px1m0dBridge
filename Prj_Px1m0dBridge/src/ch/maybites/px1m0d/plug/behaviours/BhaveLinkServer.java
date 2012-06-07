package ch.maybites.px1m0d.plug.behaviours;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.message.Px1MessageInternalCommand;
import ch.maybites.px1m0d.message.Px1MessageInternalLiaison;
import ch.maybites.px1m0d.message.Px1MessageInternalNotify;
import ch.maybites.px1m0d.plug.gestures.listeners.*;
import ch.maybites.px1m0d.plug.*;
import ch.maybites.px1m0d.plug.drawing.*;
import ch.maybites.px1m0d.plug.drawing.effects.EffctLampBlinker;

import java.util.*;

public class BhaveLinkServer implements Behaviour, GstrLiaisonLinkLstnr, GstrPluggingLstnr{ 
	private Plug myPlug;
	private Painter myPainter;
	
	private Vector<Long> _myLinkConnections;
	
	public BhaveLinkServer(Plug p, Painter pt){
		myPlug = p;
		myPainter = pt;
		_myLinkConnections = new Vector<Long>();
	}
	
	public void action(){;}
	
	public boolean takeControl(){return false;}
	
	public void reSurrect(){;}

	public void comatize() {
		for(int i = 0; i < _myLinkConnections.size(); i++){
			Px1MessageInternalCommand link = new Px1MessageInternalCommand();
			link.routeInternalyToUID(((Long)_myLinkConnections.get(i)).longValue());
			link.createSuspendLinkMessage(myPlug);
			myPlug.outgoing(link);
		}
	}

	public void reAnimate() {
		for(int i = 0; i < _myLinkConnections.size(); i++){
			Px1MessageInternalCommand link = new Px1MessageInternalCommand();
			link.routeInternalyToUID(((Long)_myLinkConnections.get(i)).longValue());
			link.createReWireLinkMessage(myPlug);
			myPlug.outgoing(link);
		}
	}

	public void destroy(){
		Debugger.getInstance().debugMessage(getClass(), "destroy() SocketID = " + myPlug.parameter.getSocketID());
		for(int i = 0; i < _myLinkConnections.size(); i++){
			Px1MessageInternalCommand link = new Px1MessageInternalCommand();
			link.routeInternalyToUID(((Long)_myLinkConnections.get(i)).longValue());
			link.createReMoveLinkMessage(myPlug);
			myPlug.outgoing(link);
		}
	}

	public void closingLiaisonForLink(Px1MessageInternalLiaison msg) {myPlug.outgoing(msg);}

	public void createdClientLiaisonForLink(int socketID_Server, long uniqueID_Server) {
		Debugger.getInstance().debugMessage(getClass(), "createdClientLiaisonForLink("+socketID_Server+") SocketID = " + myPlug.parameter.getSocketID());
		Long UID = new Long(uniqueID_Server);
		if(_myLinkConnections.contains(UID)){
			_myLinkConnections.remove(UID);
			createRemoveLinkEffect();
			Px1MessageInternalCommand link = new Px1MessageInternalCommand();
			link.routeInternalyToUID(UID.longValue());
			link.createReMoveLinkMessage(myPlug);
			myPlug.outgoing(link);
		} else {
			_myLinkConnections.add(UID);
			createAddLinkEffect();
			Px1MessageInternalCommand link = new Px1MessageInternalCommand();
			link.routeInternalyToSocketID(socketID_Server);
			link.createEstablishLinkMessage(myPlug);
			myPlug.outgoing(link);
		}
	}

	private void createRemoveLinkEffect(){
		int[] sequence = {0, 5, 10, 15, 20, 25, 30};
		myPlug.painter.addEffect(new EffctLampBlinker(50, sequence, false));
	}
	
	private void createAddLinkEffect(){
		int[] sequence = {0, 5, 10, 15, 20};
		myPlug.painter.addEffect(new EffctLampBlinker(50, sequence, false));
	}

	public void createdServerLiaisonForLink(int socketID_Client, long uniqueID_Client) {;}

	public void liaisonForLinkClosedByClient() {;}

	public void linkMessage(Px1MessageInternalCommand msg) {;}

	public void pluggedIN() {
		for(int i = 0; i < _myLinkConnections.size(); i++){
			Px1MessageInternalNotify link = new Px1MessageInternalNotify();
			link.routeInternalyToUID(((Long)_myLinkConnections.get(i)).longValue());
			link.createPluggNoticeForLinkedPlug();
			myPlug.outgoing(link);
		}
	}

	public void pluggedOUT() {
		for(int i = 0; i < _myLinkConnections.size(); i++){
			Px1MessageInternalNotify link = new Px1MessageInternalNotify();
			link.routeInternalyToUID(((Long)_myLinkConnections.get(i)).longValue());
			link.createUnPluggNoticeForLinkedPlug();
			myPlug.outgoing(link);
		}
	}

}
