package ch.maybites.px1m0d.plug.drawing.effects;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.Plug;
import ch.maybites.px1m0d.plug.drawing.*;
import ch.maybites.px1m0d.plug.gestures.listeners.*;
/*
 * Gesture to reflect the switching condition on the px1m0ds lamp as well
 */
public class EffctLampSwitch extends Effect implements GstrSwitchLstnr, GstrPluggingLstnr{
	private static final long serialVersionUID = 1L;

	private Painter myPainter;

	public EffctLampSwitch(){;}

	public void registerToPainter(Painter p){
		myPainter = p;
	}

	public void update(){;}

	public void draw(){;}

	public void destroy(){;}

	public void switchedOFF() {
		myPainter.myPlug.switchLampOff();
	}

	public void switchedON() {
		myPainter.myPlug.switchLampOn();
	}

	public void pluggedIN() {
//		Debugger.getInstance().debugMessage(getClass(), " pluggedIN()");
		if(myPainter.myPlug.parameter.switchIsOn())
			myPainter.myPlug.switchLampOn();
		int[] sequence = {0, 5, 10};
		myPainter.addEffect(new EffctLampBlinker(50, sequence, false));
	}

	public void pluggedOUT() {;	}
}
