package ch.maybites.px1m0d.plug.gestures;

import ch.maybites.px1m0d.plug.Plug;
import ch.maybites.px1m0d.plug.gestures.listeners.GstrSwitchLstnr;
import ch.maybites.px1m0d.message.*;
import java.util.*;

/*
 * "GstrSwitch" recognizes the manipulation of the button and other gestures 
 * it calls the switchON() and switchOFF() methods after it can guarantee, that
 * the button has first been pressed and then released, without being interrupted
 * by other external caused gestures
 */
public class GstrSwitch implements Gesture{
	private Plug myPlug;
	private Vector<GstrSwitchLstnr> myListeners;
	
	private boolean myButtonIsPressed = false;
	private boolean buttonPressed = false;
	private boolean buttonReleased = false;
	
	public GstrSwitch(Plug p){
		myPlug = p;
		myListeners = new Vector<GstrSwitchLstnr>();
	}

	public void registerListener(GstrSwitchLstnr l){
		myListeners.add(l);
	}
	
	public void incomming(Message msg){
		if(msg.myType == (Message.TYPE_SOCKET)){
			if(myPlug.parameter.buttonIsToggled()){
				if(myPlug.parameter.buttonIsPressed()){
					buttonPressed = true;
					buttonReleased = false;
				}
				if(myPlug.parameter.buttonIsReleased()){
					buttonReleased = true;
				}
				myButtonIsPressed = myPlug.parameter.buttonIsPressed();
			}
		} else if(msg.myType == Message.TYPE_LIAISON){
			/* additional gestures could render the switch preference, 
			 * that of having the button pressed and released without
			 * any interruption of external caused gestures, false.
			 */
			reset();
		}
		
		if(buttonPressed && buttonReleased){
			if(myPlug.parameter.switchIsOn()){
				switchedOFF();
			} else {
				switchedON();
			}
			myPlug.parameter.toggleSwitch();
			reset();
		}
	}

	private void reset(){
		buttonPressed = false;
		buttonReleased = false;
	}

	public void reSurrect(){;}

	public void destroy(){;}
	
	public void comatize(){;}

	public void reAnimate(){;}

	private void switchedON(){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrSwitchLstnr)myListeners.get(i)).switchedON();
		}
	}

	private void switchedOFF(){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrSwitchLstnr)myListeners.get(i)).switchedOFF();
		}
	}

}
