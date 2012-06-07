package ch.maybites.px1m0d.plug.gestures.listeners;

import ch.maybites.px1m0d.message.*;

public interface GstrLiaisonFunctionLstnr {
	
	public void createdClientLiaisonForFunction(int socketID_Server);

	public void createdServerLiaisonForFunction(int socketID_Client);
	
	public void liaisonForFunctionClosedByClient();

	public void closingLiaisonForFunction(Px1MessageInternalLiaison msg);
		
}
