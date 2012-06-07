package ch.maybites.px1m0d.plug.gestures.listeners;

import ch.maybites.px1m0d.message.*;

public interface GstrLiaisonPipeServerLstnr {
	
	public void createdServerLiaisonForPipe(int socketID_Client, long uniqueID_Client);
	
	public void liaisonForPipeClosedByClient();
	
}
