package ch.maybites.px1m0d.plug.gestures;

import java.util.*;
import java.io.*;
import ch.maybites.px1m0d.message.*;
import ch.maybites.px1m0d.plug.*;

public class Watcher implements Serializable{
	private Vector myGestures;
	private Plug myPlug;
	
	public Watcher(Plug p){
		myPlug = p;
		myGestures = new Vector();
	}

	public void register(Gesture g){
		myGestures.add(g);
	}
	
	public void incomming(Message msg){
		for(int i = 0; i < myGestures.size(); i++){
			((Gesture)myGestures.get(i)).incomming(msg);
		}
	}

	/*
	 * is beeing called if the scene has been saved and is reloaded
	 */
	public void reSurrect(){
		for(int i = 0; i < myGestures.size(); i++){
			((Gesture)myGestures.get(i)).reSurrect();
		}
	}

	public void destroy(){
		for(int i = 0; i < myGestures.size(); i++){
			((Gesture)myGestures.get(i)).destroy();
		}
		myGestures.clear();
	}

	public void comatize(){
		for(int i = 0; i < myGestures.size(); i++){
			((Gesture)myGestures.get(i)).comatize();
		}
	}

	public void reAnimate(){
		for(int i = 0; i < myGestures.size(); i++){
			((Gesture)myGestures.get(i)).reAnimate();
		}
	}

}
