package ch.maybites.px1m0d.plug.gestures;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.Plug;
import ch.maybites.px1m0d.plug.SymbolFunction;
import ch.maybites.px1m0d.plug.drawing.effects.EffctLampBlinker;
import ch.maybites.px1m0d.plug.drawing.effects.icons.*;
import ch.maybites.px1m0d.plug.gestures.listeners.GstrPipeChannelLstnr;
import ch.maybites.px1m0d.message.*;

import java.util.*;

/*
 * "GstrPipeChannelListening" tells if the plug is listening to
 * global PipeMessages
 */
public class GstrPipeGlobalListening implements Gesture{
	private static final long serialVersionUID = 1L;
	private Vector<GstrPipeChannelLstnr> myListeners;
	private Plug _myPlug;
	
	private EffctLampBlinker _listeningBlinker;
	private EffctLabelDisplay _labelDiplay;
	
	private boolean isListening = false;
		
	public GstrPipeGlobalListening(Plug p){
		myListeners = new Vector<GstrPipeChannelLstnr>();
		_myPlug = p;
		int[] sequence = {0, 10, 20};
		_listeningBlinker = new EffctLampBlinker(50, sequence, true);
		_labelDiplay = new EffctLabelDisplay(p.parameter.getIconColor());
	}

	public void registerListener(GstrPipeChannelLstnr l){
		myListeners.add(l);
	}
	
	public void incomming(Message msg){
		if(msg.myType == Message.TYPE_FUNCTION){
			Px1MessageInternalFunction m = (Px1MessageInternalFunction)msg;
			if(m.isSymbolFromServerMessage()){
				SymbolFunction sm = m.getSymbolMessage();
				if(sm.isGlobalPipeListeningFunction()){
					Debugger.getInstance().verboseMessage(getClass(), "SymbolFunction channel: " + sm.getChannel() + " function: " + sm.getFunction() % 10);
					if(isListening){
						stopListeningForPipe();
						isListening = false;
					} else {
						startListeningForPipe(sm.getFunction() % 10);
						isListening = true;
					}
				}
			}
		}
	}
	
	public void reSurrect(){;}

	public void destroy(){;}

	public void comatize(){
		stopListeningForPipe();
		isListening = false;
	}

	public void reAnimate(){;}

	private void startListeningForPipe(int channel){
		_myPlug.painter.addEffect(_listeningBlinker);
		_myPlug.painter.addEffect(_labelDiplay);
		for(int i = 0; i < myListeners.size(); i++){
			((GstrPipeChannelLstnr)myListeners.get(i)).startListeningForPipe(channel);
		}
	}

	private void stopListeningForPipe(){
		_listeningBlinker.kill();
		_labelDiplay.kill();
		for(int i = 0; i < myListeners.size(); i++){
			((GstrPipeChannelLstnr)myListeners.get(i)).stopListeningForPipe();
		}
	}

}
