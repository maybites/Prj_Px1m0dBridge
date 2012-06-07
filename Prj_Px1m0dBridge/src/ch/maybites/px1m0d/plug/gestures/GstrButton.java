package ch.maybites.px1m0d.plug.gestures;

import ch.maybites.px1m0d.plug.Plug;
import ch.maybites.px1m0d.plug.gestures.listeners.GstrButtonLstnr;
import ch.maybites.px1m0d.message.*;

import java.util.*;

public class GstrButton implements Gesture{
	private static final long serialVersionUID = 1L;
	private Plug myPlug;
	private Vector<GstrButtonLstnr> myListeners;
	
	public GstrButton(Plug p){
		myListeners = new Vector<GstrButtonLstnr>();
		myPlug = p;
	}

	public void registerListener(GstrButtonLstnr l){
		myListeners.add(l);
	}
	
	public void incomming(Message msg){
		if(msg.myType == (Message.TYPE_SOCKET)){
			if(myPlug.parameter.buttonIsToggled()){
				if(myPlug.parameter.buttonIsPressed()){
					buttonPressed();
				}
				if(myPlug.parameter.buttonIsReleased()){
					buttonReleased();
				}
			}
		}
	}
	
	public void reSurrect(){;}

	public void destroy(){;}

	public void comatize(){;}

	public void reAnimate(){;}
	
	private void buttonPressed(){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrButtonLstnr)myListeners.get(i)).buttonPressed();
		}
	}

	private void buttonReleased(){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrButtonLstnr)myListeners.get(i)).buttonReleased();
		}
	}

}
