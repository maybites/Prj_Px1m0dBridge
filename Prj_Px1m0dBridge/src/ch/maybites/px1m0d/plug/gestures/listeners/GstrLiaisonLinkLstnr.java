package ch.maybites.px1m0d.plug.gestures.listeners;

import ch.maybites.px1m0d.message.*;

public interface GstrLiaisonLinkLstnr {
	
	public void createdClientLiaisonForLink(int socketID_Server, long uniqueID_Server);

	public void createdServerLiaisonForLink(int socketID_Client, long uniqueID_Client);
	
	public void liaisonForLinkClosedByClient();

	public void closingLiaisonForLink(Px1MessageInternalLiaison msg);
	
	public void linkMessage(Px1MessageInternalCommand msg);
	
}
