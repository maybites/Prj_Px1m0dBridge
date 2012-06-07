package ch.maybites.px1m0d.plug.gestures;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.Plug;
import ch.maybites.px1m0d.plug.gestures.listeners.GstrMetronomeLstnr;
import ch.maybites.px1m0d.message.*;

import java.util.*;

public class GstrMetronome implements Gesture{
	private static final long serialVersionUID = 1L;
	private Plug myPlug;
	private Vector<GstrMetronomeLstnr> myListeners;
	
	public GstrMetronome(Plug p){
		myListeners = new Vector<GstrMetronomeLstnr>();
		myPlug = p;
	}

	public void registerListener(GstrMetronomeLstnr l){
		myListeners.add(l);
	}
	
	public void incomming(Message msg){
		if(msg.myType == (Message.TYPE_METRONOME)){
			metronomeClick();
		}
	}
	
	public void reSurrect(){;}

	public void destroy(){;}

	public void comatize(){;}

	public void reAnimate(){;}
	
	private void metronomeClick(){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrMetronomeLstnr)myListeners.get(i)).metronomeClicked();
		}
		myPlug.parameter.setMetronomeClick(true);
	}
}
