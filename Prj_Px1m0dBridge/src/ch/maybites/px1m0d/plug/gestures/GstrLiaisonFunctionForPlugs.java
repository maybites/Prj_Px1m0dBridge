package ch.maybites.px1m0d.plug.gestures;

import ch.maybites.px1m0d.plug.Plug;
import ch.maybites.px1m0d.plug.gestures.listeners.*;
import ch.maybites.px1m0d.message.*;

/*
 * "GstrFunctionLiaisonForPlug" handles the liaisons between plugs und buttons. this means it helps the
 * plugs to recognise if a liaison needs to be established, what kind of liaison, and
 * helps closing it again
 */
public class GstrLiaisonFunctionForPlugs extends GstrLiaisonFunction implements GstrButtonLstnr{
	private static final long serialVersionUID = 1L;

	public GstrLiaisonFunctionForPlugs(Plug p){
		super(p);
		requestType = Px1MessageInternalLiaison.LIAISON_TYPE_RequestForFunctionByPlug;
		responseType = Px1MessageInternalLiaison.LIAISON_TYPE_RequestForFunctionByButton;
	}
	
	public void buttonPressed() {
		open();
	}

	public void buttonReleased() {
		close();
	}
}
