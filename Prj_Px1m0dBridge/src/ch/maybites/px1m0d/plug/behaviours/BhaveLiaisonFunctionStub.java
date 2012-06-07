package ch.maybites.px1m0d.plug.behaviours;

import ch.maybites.px1m0d.message.*;
import ch.maybites.px1m0d.plug.*;
import ch.maybites.px1m0d.plug.gestures.listeners.*;
/*
 * This Class is used by the Plugs in order to make the GstrLiaisonFuntionForPlugs work 
 * properly. It simply sends the liaison closure message, that is passed on by the 
 * closingLiaisonForFunction() to this object.
 */
public class BhaveLiaisonFunctionStub implements Behaviour, GstrLiaisonFunctionLstnr {
	private static final long serialVersionUID = 1L;
	Plug myPlug;

	public BhaveLiaisonFunctionStub(Plug p){
		myPlug = p;
	}

	public void closingLiaisonForFunction(Px1MessageInternalLiaison msg) {
		myPlug.outgoing(msg);
	}

	public void liaisonForFunctionClosedByClient() {;}

	public void createdClientLiaisonForFunction(int socketID_Server) {;}

	public void createdServerLiaisonForFunction(int socketID_Client) {;}

	public void action() {;}

	public void destroy() {;}

	public void reSurrect() {;}

	public void comatize() {;}

	public void reAnimate() {;}

	public boolean takeControl() {
		return false;
	}

}
