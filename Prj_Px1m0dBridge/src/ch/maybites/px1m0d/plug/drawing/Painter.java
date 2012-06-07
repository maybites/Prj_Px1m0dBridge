package ch.maybites.px1m0d.plug.drawing;

import processing.core.*;
import java.util.ArrayList;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.*;
import java.io.*;
import mathematik.*;

/*
 *  Base Class to make the Plug visible 
 */
public class Painter implements Serializable{
	private static final long serialVersionUID = 1L;
	public Plug myPlug;
	private EffectQueue myEffects;
	private transient Canvas canvas;

	public Vector3f position = new Vector3f(); 

	int height, width;
	public float spacing;

	public Painter(Plug s){
		myPlug = s;
		myEffects = new EffectQueue();
		canvas = Canvas.getInstance();
		setup();
	}

	public Canvas getCanvas(){
		return canvas;
	}

	public void addEffect(Effect e){
		e.registerToPainter(this);
		myEffects.add(e);
	}

	public void removeEffect(Effect e){
		e.destroy();
		myEffects.remove(e);
	}

	private void setup(){
		width = canvas.width();
		height = canvas.height();
		spacing = height / 9;
		position = getSocketVector(myPlug.parameter.getSocketID());
	}
	
	public Vector3f getSocketVector(int socketID){
		return new Vector3f((3.5 - (socketID % 8)) * spacing, (3.5 - (socketID / 8)) * spacing, 0);
	}
	
	public void update(){
		for(int i = (myEffects.size() - 1); i >= 0; i--){
			myEffects.get(i).update();
			if(myEffects.get(i).isDead){
				myEffects.remove(i);
			}
		}
	}

	public void draw(){
		for(int i = 0; i < myEffects.size(); i++){
			myEffects.get(i).draw();
		}
	}

	public void destroy(){
		for(int i = 0; i < myEffects.size(); i++){
			myEffects.get(i).destroy();
		}
		myEffects.destroy();
	}

	public void comatize(){
		for(int i = 0; i < myEffects.size(); i++){
			myEffects.get(i).comatize();
		}
	}

	public void reAnimate(){
		setup();
		for(int i = 0; i < myEffects.size(); i++){
			myEffects.get(i).reAnimate();
		}
	}

	/*
	 * is beeing called if the scene has been saved.
	 */
	public void reSurrect(){
		canvas = Canvas.getInstance();
		for(int i = 0; i < myEffects.size(); i++){
			myEffects.get(i).reSurrect();
		}
	}
}
