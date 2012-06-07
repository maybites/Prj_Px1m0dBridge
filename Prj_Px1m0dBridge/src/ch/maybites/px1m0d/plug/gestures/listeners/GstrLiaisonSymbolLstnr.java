package ch.maybites.px1m0d.plug.gestures.listeners;

import ch.maybites.px1m0d.message.*;

public interface GstrLiaisonSymbolLstnr {
	
	public void createdClientLiaisonForSymbol(int socketID_Server);

	public void createdServerLiaisonForSymbol(int socketID_Client);
	
	public void liaisonForSymbolClosedByClient();

	public void closingLiaisonForSymbol(Px1MessageInternalLiaison msg);	
	
	public void symbolMessage(Px1MessageInternalSymbol msg);

}
