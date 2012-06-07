package ch.maybites.px1m0d.plug.drawing.effects;

import ch.maybites.px1m0d.plug.*;
import ch.maybites.px1m0d.plug.drawing.Effect;
import ch.maybites.px1m0d.plug.drawing.Painter;
import ch.maybites.px1m0d.plug.drawing.effects.icons.IconBasic;
import ch.maybites.px1m0d.plug.drawing.effects.icons.EffctDevicePlug;
import gestalt.*;
import gestalt.shape.*;
import java.util.*;
import mathematik.*;


/*
 * draws directon box and shows through color alpha transparency
 * if lamp is switched on
 */
public class EffctPipes extends Effect{
	private static final long serialVersionUID = 1L;
	private Plug _myPlug;
	private Painter myPainter;
	private Vector<PipeChannelSenderTube> pipes;
	
	private Color _myColor;
	
	public EffctPipes(Plug plug, Color c){
		_myPlug = plug;
		pipes = new Vector<PipeChannelSenderTube>();
		_myColor = c;
	}

	private void setup(){;}

	public void registerToPainter(Painter p){
		myPainter = p;
		_myColor.r += (float)myPainter.myPlug.parameter.getXPos() / 20f;
		_myColor.g += (float)myPainter.myPlug.parameter.getYPos() / 20f;
	}

	private void setBegin(PipeChannelSenderTube tube){
		tube.setBegin(
				myPainter.position.x + (IconBasic.directionTrans.x * myPainter.spacing * myPainter.myPlug.parameter.getXDirection()),
				myPainter.position.y + (IconBasic.directionTrans.y * myPainter.spacing * myPainter.myPlug.parameter.getYDirection()),
				myPainter.position.z + (IconBasic.directionTrans.z * myPainter.spacing));
	}
	
	public void addChannel(int SocketID_End, int channel){
		PipeChannelSenderTube newPipe = new PipeChannelSenderTube(myPainter, SocketID_End, channel, _myColor);
		setBegin(newPipe);
		Vector3f end = myPainter.getSocketVector(SocketID_End);
		newPipe.setEnd(end.x + EffctDevicePlug.getChannelShift(channel, myPainter)[0], end.y + EffctDevicePlug.getChannelShift(channel, myPainter)[1], end.z);
		newPipe.refreshGeometry();
		if(_myPlug.parameter.switchIsOff())
			newPipe.animate(false);
		pipes.add(newPipe);
	}
		
	public void removeChannel(int SocketID_End, int channel){
		for(int i = (pipes.size() - 1); i >= 0; i--){
			if(((PipeChannelSenderTube)pipes.get(i)).isThisPipeChannel(SocketID_End, channel)){
				((PipeChannelSenderTube)pipes.get(i)).destroy();
				pipes.remove(i);
			}
		}
	}
	
	public void valueChange(int SocketID_End, int channel, float value){
		for(int i = (pipes.size() - 1); i >= 0; i--){
			if(((PipeChannelSenderTube)pipes.get(i)).isThisPipeChannel(SocketID_End, channel)){
				((PipeChannelSenderTube)pipes.get(i)).valueChange(value);
			}
		}
		
	}
	
	public void animate(boolean condition){
		for(int i = 0; i < pipes.size(); i++){
			((PipeChannelSenderTube)pipes.get(i)).animate(condition);
		}
	}
	
	public void update(){
		;
	}

	public void draw(){
		for(int i = 0; i < pipes.size(); i++){
			((PipeChannelSenderTube)pipes.get(i)).update(1.0f);
		}
	}

	public void comatize(){
		for(int i = 0; i < pipes.size(); i++){
			((PipeChannelSenderTube)pipes.get(i)).comatize();
		}
	}
	
	public void reAnimate(){
		for(int i = 0; i < pipes.size(); i++){
			setBegin((PipeChannelSenderTube)pipes.get(i));
			((PipeChannelSenderTube)pipes.get(i)).refreshGeometry();
			((PipeChannelSenderTube)pipes.get(i)).reAnimate();
		}
	}

	public void reSurrect(){
		for(int i = 0; i < pipes.size(); i++){
			((PipeChannelSenderTube)pipes.get(i)).reSurrect();
		}
	}

	public void destroy(){
		for(int i = (pipes.size() - 1); i >= 0; i--){
			((PipeChannelSenderTube)pipes.get(i)).destroy();
			pipes.remove(i);
		}
	}
}
