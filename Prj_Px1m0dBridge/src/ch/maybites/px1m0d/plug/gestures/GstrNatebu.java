package ch.maybites.px1m0d.plug.gestures;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.Plug;
import ch.maybites.px1m0d.plug.gestures.listeners.GstrNatebuLstnr;
import ch.maybites.px1m0d.message.*;

import java.util.*;

public class GstrNatebu implements Gesture{
	private static final long serialVersionUID = 1L;
	private Plug myPlug;
	private Vector<GstrNatebuLstnr> myListeners;
	
	public GstrNatebu(Plug p){
		myListeners = new Vector<GstrNatebuLstnr>();
		myPlug = p;
	}

	public void registerListener(GstrNatebuLstnr l){
		myListeners.add(l);
	}
	
	public void incomming(Message msg){
		if(msg.myType == (Message.TYPE_NATEBU)){
			Px1MessageNatebu m = (Px1MessageNatebu)msg;
			if(m.uiDataHasChanged()){
				natebuUIChange(m);
			}
			natebuValue(
					m.getArrayValue(myPlug.parameter.getSocketID()),
					0,
					m.xPosWeight,
					m.yPosWeight);
		}
	}
	
	public void reSurrect(){;}

	public void destroy(){;}

	public void comatize(){;}

	public void reAnimate(){;}
	
	private void natebuUIChange(Px1MessageNatebu m){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrNatebuLstnr)myListeners.get(i)).natebuUIChange();
		}
		myPlug.parameter.setNatebuInterface(m);
	}
	
	private void natebuValue(float value, float weight, float wPosX, float wPosY){
		//Debugger.getInstance().debugMessage(myPlug.getClass(), "time: " + System.currentTimeMillis() + "plug is receiving natebu message: " + value);
		for(int i = 0; i < myListeners.size(); i++){
			((GstrNatebuLstnr)myListeners.get(i)).natebuValue(value);
		}
		myPlug.parameter.setNatebuValue(value);
		myPlug.parameter.setNatebuWeight(weight, wPosX, wPosY);
	}
}
