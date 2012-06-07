package ch.maybites.px1m0d.plug.gestures.listeners;

import ch.maybites.px1m0d.message.*;

public interface GstrLiaisonPipeReceiverLstnr {
	
	public void createdClientLiaisonForPipe(int socketID_Server, long uniqueID_Server);

	public void closingLiaisonForPipe();
	
}
