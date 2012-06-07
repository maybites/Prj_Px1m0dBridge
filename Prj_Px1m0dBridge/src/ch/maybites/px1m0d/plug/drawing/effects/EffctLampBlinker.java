package ch.maybites.px1m0d.plug.drawing.effects;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.drawing.*;

/*
 * draws directon box and shows through color alpha transparency
 * if lamp is switched on
 */
public class EffctLampBlinker extends Effect{
	private static final long serialVersionUID = 1L;

	private Painter myPainter;

	private int[] mySequence = null;

	private int seqCounter, stepCounter;

	private long _myTimeStep, _myNextStepTime;

	private boolean _myLoop, _lampConditionOnStart, _lampIsLinkedWithSwitch;

	/*
	 * The LampBlinker toggles the lamp
	 * 
	 * @parameter	timestep	defines the step size of the sequence in milliseconds
	 * @parameter	sequence	sequence of toggle events. timed in steps from the
	 * 							beginning, i.e. {0, 10, 15, 20}. except the last event, 
	 * 							which is only used to set the length of the sequence.
	 * 							That means, 0, 10 and 15 toggle the lamp, the sequence 
	 * 							runns until 20 and if loop is true, the sequence starts again
	 * 							without toggling the lamp.
	 * @parameter 	loop		loops the sequence if true
	 */
	public EffctLampBlinker(long timestep, int[] sequence, boolean loop){
		mySequence = sequence;
		_myTimeStep = timestep;
		_myLoop = loop;
		isDead = true;
	}
	
	private void reset(){
		isDead = false;
		seqCounter = 0;
		stepCounter = 0;
		_myNextStepTime = System.currentTimeMillis() + _myTimeStep;
	}

	public void registerToPainter(Painter p){
		reset();
		myPainter = p;
		_lampConditionOnStart = myPainter.myPlug.parameter.lampIsOn();
		if((myPainter.myPlug.parameter.lampIsOn() && myPainter.myPlug.parameter.switchIsOn()) ||
				(myPainter.myPlug.parameter.lampIsOff() && myPainter.myPlug.parameter.switchIsOff()))
			_lampIsLinkedWithSwitch = true;
		else
			_lampIsLinkedWithSwitch = false;
	}

	public void update(){
		if(!isDead){
			if(_myNextStepTime < System.currentTimeMillis() && mySequence != null){
//				Debugger.getInstance().debugMessage(getClass(), " beginning -> isDead: " + isDead);
				if(mySequence.length > (seqCounter + 1)){
					if(mySequence[seqCounter] == stepCounter){
						toggleLamp();
//						Debugger.getInstance().debugMessage(getClass(), " toggle lamp of socket: " + myPainter.myPlug.parameter.SocketID() + " at : " + System.currentTimeMillis() + " ms");
						seqCounter++;
					} else {
						stepCounter++;
						_myNextStepTime = System.currentTimeMillis() + _myTimeStep;
					}
				} else if(mySequence.length == (seqCounter + 1)){
					if(mySequence[seqCounter] == stepCounter){
						if(_myLoop){
							seqCounter = 0;
							stepCounter = 0;
						} else {
							mySequence = null;
							myPainter.myPlug.switchLamp(_lampConditionOnStart);
//							Debugger.getInstance().debugMessage(getClass(), " kill effect");
							isDead = true;
//							Debugger.getInstance().debugMessage(getClass(), " end -> isDead: " + isDead);
						}
					} else {
						stepCounter++;
						_myNextStepTime = System.currentTimeMillis() + _myTimeStep;
					}
				} 
			}
		}
	}
		
	/*
	 * stops the effect
	 */
	public void kill(){
		if(!isDead){
			isDead = true;
			if(_lampIsLinkedWithSwitch)
				myPainter.myPlug.switchLamp(myPainter.myPlug.parameter.switchIsOn());
			else
				myPainter.myPlug.switchLamp(_lampConditionOnStart);
		}
	}

	private void toggleLamp(){
		myPainter.myPlug.toggleLamp();
	}

	public void draw(){
	}

	public void destroy(){
		Debugger.getInstance().debugMessage(getClass(), " kill effect");
	}

	public void reAnimate(){
		//		Debugger.getInstance().debugMessage(getClass(), " reAnimate socket: " + myPainter.myPlug.mySocketID + " at : " + System.currentTimeMillis() + " ms");
	}

	public void comatize(){
		//		Debugger.getInstance().debugMessage(getClass(), " comatize socket: " + myPainter.myPlug.mySocketID + " at : " + System.currentTimeMillis() + " ms");
	}
}
