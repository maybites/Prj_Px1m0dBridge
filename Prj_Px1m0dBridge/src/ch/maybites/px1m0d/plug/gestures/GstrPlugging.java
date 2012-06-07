package ch.maybites.px1m0d.plug.gestures;

import ch.maybites.px1m0d.plug.Plug;
import ch.maybites.px1m0d.plug.gestures.listeners.GstrPluggingLstnr;
import ch.maybites.px1m0d.message.*;

import java.util.*;

public class GstrPlugging implements Gesture{
	private static final long serialVersionUID = 1L;
	private Vector<GstrPluggingLstnr> myListeners;
	private Plug myPlug;

	public GstrPlugging(Plug p){
		myListeners = new Vector<GstrPluggingLstnr>();
		myPlug = p;
	}

	public void registerListener(GstrPluggingLstnr l){
		myListeners.add(l);
	}

	public void incomming(Message msg){
		if(msg.myType == (Message.TYPE_SOCKET)){
			if(myPlug.parameter.pluggingIsToggled()){
				if(myPlug.parameter.plugIsPlugged()){
					pluggedIN();
				}
				if(myPlug.parameter.plugIsUnPlugged()){
					pluggedOUT();
				}
			}
		}
		if(msg.myType == (Message.TYPE_NOTIFY)){
			Px1MessageInternalNotify ntf = (Px1MessageInternalNotify)msg;
			if(ntf.isPluggNoticeForLinkedPlug()){
				pluggedIN();
				myPlug.parameter.setLinkedPluggedToggle();
			}
			if(ntf.isUnPluggNoticeForLinkedPlug()){
				pluggedOUT();
				myPlug.parameter.setLinkedPluggedToggle();
			}
		}
	}

	public void reSurrect(){;}

	public void destroy(){;}

	public void comatize(){;}

	public void reAnimate(){;}

	private void pluggedIN(){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrPluggingLstnr)myListeners.get(i)).pluggedIN();
		}
	}

	private void pluggedOUT(){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrPluggingLstnr)myListeners.get(i)).pluggedOUT();
		}
	}

}
