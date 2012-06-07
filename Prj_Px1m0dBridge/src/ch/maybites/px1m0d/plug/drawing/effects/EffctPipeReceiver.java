package ch.maybites.px1m0d.plug.drawing.effects;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.*;
import ch.maybites.px1m0d.plug.drawing.Effect;
import ch.maybites.px1m0d.plug.drawing.Painter;
import gestalt.*;
import gestalt.shape.*;
import java.util.*;
import mathematik.*;


/*
 * draws directon box and shows through color alpha transparency
 * if lamp is switched on
 */
public class EffctPipeReceiver extends Effect{
	private static final long serialVersionUID = 1L;
	private Plug _myPlug;
	private Painter myPainter;
	private Vector<PipeChannelReceiverPad> pipes;
	
	private Color _myColor;
	
	public EffctPipeReceiver(Plug plug){
		_myPlug = plug;
		pipes = new Vector<PipeChannelReceiverPad>();
		_myColor = new Color(0.0f, 0.0f, 0.0f, .5f);
	}

	private void setup(){;}

	public void registerToPainter(Painter p){
		myPainter = p;
		_myColor.r += (float)myPainter.myPlug.parameter.getXPos() / 20f;
		_myColor.g += (float)myPainter.myPlug.parameter.getYPos() / 20f;
	}

	public void addChannel(int channel){
		//Debugger.getInstance().verboseMessage(this.getClass(), "add new Channel Receive Effect for Socket: " + myPainter.myPlug.parameter.getSocketID() + " on channel: "+ channel);
		PipeChannelReceiverPad newPipe = new PipeChannelReceiverPad(myPainter, myPainter.myPlug.parameter.getSocketID(), channel, _myColor);
		newPipe.refreshGeometry();
		if(_myPlug.parameter.switchIsOff())
			newPipe.animate(false);
		pipes.add(newPipe);
	}
		
	public void removeOneChannel(int channel){
		//Debugger.getInstance().verboseMessage(this.getClass(), "remove Channel Receive Effect for Socket: " + myPainter.myPlug.parameter.getSocketID() + " on channel: "+ channel);
		for(int i = (pipes.size() - 1); i >= 0; i--){
			if(((PipeChannelReceiverPad)pipes.get(i)).isThisPipeChannel(channel)){
				((PipeChannelReceiverPad)pipes.get(i)).destroy();
				pipes.remove(i);
				return;
			}
		}
	}
	
	public void removeAllChannel(int channel){
		//Debugger.getInstance().verboseMessage(this.getClass(), "remove Channel Receive Effect for Socket: " + myPainter.myPlug.parameter.getSocketID() + " on channel: "+ channel);
		for(int i = (pipes.size() - 1); i >= 0; i--){
			if(((PipeChannelReceiverPad)pipes.get(i)).isThisPipeChannel(channel)){
				((PipeChannelReceiverPad)pipes.get(i)).destroy();
				pipes.remove(i);
			}
		}
	}
	
	public void animate(boolean condition){
		for(int i = 0; i < pipes.size(); i++){
			((PipeChannelReceiverPad)pipes.get(i)).animate(condition);
		}
	}
	
	public void update(){
		for(int i = 0; i < pipes.size(); i++){
			((PipeChannelReceiverPad)pipes.get(i)).update(1.0f);
		}
	}

	public void draw(){
		;
	}

	public void comatize(){
		for(int i = 0; i < pipes.size(); i++){
			((PipeChannelReceiverPad)pipes.get(i)).comatize();
		}
	}
	
	public void reAnimate(){
		for(int i = 0; i < pipes.size(); i++){
			((PipeChannelReceiverPad)pipes.get(i)).refreshGeometry();
			((PipeChannelReceiverPad)pipes.get(i)).reAnimate();
		}
	}

	public void reSurrect(){
		for(int i = 0; i < pipes.size(); i++){
			((PipeChannelReceiverPad)pipes.get(i)).reSurrect();
		}
	}

	public void destroy(){
		for(int i = (pipes.size() - 1); i >= 0; i--){
			((PipeChannelReceiverPad)pipes.get(i)).destroy();
			pipes.remove(i);
		}
	}
}
