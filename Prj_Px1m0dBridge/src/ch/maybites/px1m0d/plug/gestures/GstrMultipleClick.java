package ch.maybites.px1m0d.plug.gestures;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.Plug;
import ch.maybites.px1m0d.plug.gestures.listeners.GstrMultipleClickLstnr;
import ch.maybites.px1m0d.message.*;

import java.util.*;

public class GstrMultipleClick implements Gesture{
	private static final long serialVersionUID = 1L;
	private Vector<GstrMultipleClickLstnr> myListeners;
	protected Plug myPlug;
	protected transient Thread myDBTimer;

	private int INTERRUPT = 10;  //Interrupt time of internal thread

//	protected int myButton = Plug.OFF;
	protected int myTime = 300; //ms

	protected long myTiming;

	protected boolean hasSentPressed = false;

	protected int counterPressed = 0;
	/*
	 * @param	p		instance of plug
	 * @param	time	time in ms to wait for a second click
	 */
	public GstrMultipleClick(Plug p, int time){
		myListeners = new Vector<GstrMultipleClickLstnr>();
		myTime = time;
		myDBTimer = null;
		myPlug = p;
	}

	public void registerListener(GstrMultipleClickLstnr l){
		myListeners.add(l);
	}

	public void incomming(Message msg){
		if(msg.myType == (Message.TYPE_SOCKET)){
			if(myPlug.parameter.buttonIsToggled()){
				if(myPlug.parameter.buttonIsPressed()){
					startTimer();
					startTiming();
				}
			}
		}
	}

	public void reSurrect(){;}

	public void destroy(){
		myDBTimer = null;
	}

	public void comatize(){;}

	public void reAnimate(){;}

	protected void multiplePressed(int count){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrMultipleClickLstnr)myListeners.get(i)).multiplePressed(count);
		}
	}

	protected void multipleReleased(int count){
		for(int i = 0; i < myListeners.size(); i++){
			((GstrMultipleClickLstnr)myListeners.get(i)).multipleReleased(count);
		}
	}

	protected void startTiming(){
		if(timeIsOver())
			counterPressed = 1;
		else
			counterPressed ++;
		myTiming = System.currentTimeMillis();
	}

	protected boolean timeIsOver(){
		return (System.currentTimeMillis() > myTiming + myTime)? true: false;
	}

	protected void reset(){
		hasSentPressed = false;
		counterPressed = 0;
		stopTimer();
	}

	private void startTimer(){
		if(myDBTimer == null){
			try{
				myDBTimer = new Thread(new MultipleClickTimer());
				myDBTimer.start();
				myDBTimer.setPriority(Thread.MIN_PRIORITY);
			} catch(Exception e){
				e.printStackTrace(System.out);
			}
		}
	}

	private void stopTimer(){
		myDBTimer = null;
	}

	public class MultipleClickTimer implements Runnable{
		/*
		 * http://java.sun.com/j2se/1.4.2/docs/guide/misc/threadPrimitiveDeprecation.html
		 */
		transient Thread thisThread;
		public void run(){
			//int counter = 0;
			//long t = System.currentTimeMillis();

			thisThread = Thread.currentThread();
			while(thisThread == myDBTimer){	
				/*
				counter++;
				if(counter > 10){
					Debugger.getInstance().verboseMessage(this.getClass(),"Double Click 10 cycles in: " + (System.currentTimeMillis() - t) + " ms");
					t = System.currentTimeMillis();
					counter = 0;
				}
				 */

				try {
					thisThread.sleep(INTERRUPT);
				} catch (InterruptedException e){;}

				if(timeIsOver()){
					if(!hasSentPressed){
						multiplePressed(counterPressed);
						hasSentPressed = true;
					}
					if(myPlug.parameter.buttonIsReleased()){
						multipleReleased(counterPressed);
						reset();
					}
				}
			}
		}		
	}
}
