package ch.maybites.px1m0d.plug;

import ch.maybites.px1m0d.message.*;
import ch.maybites.px1m0d.plug.behaviours.Agent;
import ch.maybites.px1m0d.plug.drawing.Painter;
import ch.maybites.px1m0d.plug.gestures.Watcher;
import ch.maybites.px1m0d.plug.config.*;
import processing.core.*;
import java.io.*;
import java.util.*;

public abstract class Plug implements Serializable{
	private static final long serialVersionUID = 1L;
	public Socket socket;	
	public Painter painter;
	public Watcher watcher;
	public Agent agent;
	public PlugParameter parameter;
	
	/* amount in millisecond until this plug is destroyed from the time of
	 * embalment...
	 */
	
	public long timeToKill = 5000;
	public long enbalmentTime = 0;
	
	public long myUniqueID;
	
	public boolean isDestroyed = false;
		
	private boolean isManufacturing = false;
	
	public Plug(Socket sm, PlugConfig config, Px1MessageSocket m){
		myUniqueID = (new Random(System.currentTimeMillis())).nextLong();
		socket = sm;
		parameter = new PlugParameter(config);
		if(m != null){
			socketUpdate(m);
		} else {
			parameter.setPosition(sm.myXpos, sm.myYpos);
			parameter.setSocketID(sm.myId);
		}
	}
	
	//should be done by the PlugFactory
	public void refreshConfiguration(PlugConfig config){
		parameter.refreshConfiguration(config);
	}
	
	public void outgoing(Message m){
		socket.outgoing(m.setSenderID(parameter.getSocketID(), parameter.getPlugID(), myUniqueID));
	}
	
	public void synchronize(){
		parameter.previousActionReset();//IMPORTANT!!!!
	}
			
	public void socketUpdate(Px1MessageSocket m){
		parameter.setPosition(m.getColumn(), m.getRow());
		parameter.setLamp(m.getPlugLamp());
		parameter.setButton(m.getSocketButton());
		parameter.setPluggedCondition(m.getIsPlugged());
		parameter.setPlugID(m.getPlugID());
		parameter.setSocketID(m.getSocketID());
		parameter.setDirection(m.getPlugDir());
	}

	public void socketUnplug(Px1MessageSocket m){
		parameter.setButton(m.getSocketButton());
		parameter.setPluggedCondition(m.getIsPlugged());
	}

	public void incomming(Message m){
		if(watcher != null)
			watcher.incomming(m);
	}

	public void resetParameter(Px1MessageNatebu msg){
		parameter.resetNatebuInterface((Px1MessageNatebu)msg);
	}

	public void setParameter(Px1MessageNatebu msg){
		parameter.setNatebuInterface((Px1MessageNatebu)msg);
	}

	public void update(){
		if(agent != null)
			agent.update();
		if(painter != null)
			painter.update();
	}

	public void draw(PApplet canvas){
		if(painter != null)
			painter.draw();
	}
	
	public void destroy(){
		parameter.destroy();
		if(watcher != null)
			watcher.destroy();
		if(agent != null)
			agent.destroy();
		if(painter != null)
			painter.destroy();
		isDestroyed = true;
	}
	
	/*
	 * comatizes this plug, in case is should be reanimated within the timelimit set by
	 * timeToKill
	 */
	public void comatize(){
		enbalmentTime = System.currentTimeMillis() + this.timeToKill;
		if(agent != null)
			agent.comatize();
		if(watcher != null)
			watcher.comatize();
		if(painter != null)
			painter.comatize();
		parameter.previousActionReset();//IMPORTANT - since no synchronisation() call reach the plug 
	}	
	
	/*
	 * animates this plug after it got comatized
	 */
	public void reAnimate(Socket s){
		socket = s;
		if(painter != null)
			painter.reAnimate();
		if(agent != null)
			agent.reAnimate();
		if(watcher != null)
			watcher.reAnimate();
	}

	/*
	 * resurrects this plug after it was saved
	 */
	public void reSurrect(){
		parameter.reSurrect();
		if(watcher != null)
			watcher.reSurrect();
		if(agent != null)
			agent.reSurrect();
		if(painter != null)
			painter.reSurrect();
	}

	public boolean timeToDestroy(){
		return(enbalmentTime < System.currentTimeMillis())? true: false;
	}
	
	public void toggleLamp(){
		if(parameter.lampIsOn()){
			switchLampOff();
		} else {
			switchLampOn();
		}
	}
	
	public void switchLamp(int flag){
		parameter.setLamp(flag);
		Px1MessageLamp m = new Px1MessageLamp(this.parameter.getSocketID().byteValue(), (byte)flag);
		this.outgoing(m);
	}

	public void switchLamp(boolean flag){
		switchLamp((flag == true)? 1:0);
	}

	public void switchLampOn(){
		switchLamp(true);
	}

	public void switchLampOff(){
		switchLamp(false);
	}

}

