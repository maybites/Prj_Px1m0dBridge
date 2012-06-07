package ch.maybites.px1m0d.plug.gestures;

import ch.maybites.px1m0d.plug.Plug;
import ch.maybites.px1m0d.plug.gestures.listeners.*;
import ch.maybites.px1m0d.message.*;

/*
 * "GstrFunctionLiaisonForButtons" handles the liaisons between plugs and buttons. this means it helps the
 * buttons to recognize if a liaison needs to be established, what kind of liaison, and
 * helps closing it again
 */
public class GstrLiaisonFunctionForButtons extends GstrLiaisonFunction implements GstrMultipleClickLstnr{
	private static final long serialVersionUID = 1L;

	public GstrLiaisonFunctionForButtons(Plug p){
		super(p);
		requestType = Px1MessageInternalLiaison.LIAISON_TYPE_RequestForFunctionByButton;
		responseType = Px1MessageInternalLiaison.LIAISON_TYPE_RequestForFunctionByPlug;
	}
	
	public void multiplePressed(int clicks) {
		open();
	}
	
	public void multipleReleased(int clicks) {
		close();
	}
}
