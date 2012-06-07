package ch.maybites.px1m0d.plug.behaviours;

import java.util.Vector;
import java.io.*;

public class Agent implements Serializable{
	private Vector myBehaviours;
	
	public Agent(){
		myBehaviours = new Vector();
	}

	public void register(Behaviour g){
		myBehaviours.add(g);
	}
	
	public void update(){
		for(int i = 0; i < myBehaviours.size(); i++){
			if(((Behaviour)myBehaviours.get(i)).takeControl()){
				((Behaviour)myBehaviours.get(i)).action();
			}
		}
	}

	/*
	 * is beeing called when the plug has been unplugged.
	 */
	public void comatize(){
		for(int i = 0; i < myBehaviours.size(); i++){
			((Behaviour)myBehaviours.get(i)).comatize();
		}
	}

	/*
	 * is beeing called when the plug has been replugged.
	 */
	public void reAnimate(){
		for(int i = 0; i < myBehaviours.size(); i++){
			((Behaviour)myBehaviours.get(i)).reAnimate();
		}
	}

	
	/*
	 * is beeing called if the scene has been saved.
	 */
	public void reSurrect(){
		for(int i = 0; i < myBehaviours.size(); i++){
			((Behaviour)myBehaviours.get(i)).reSurrect();
		}
	}

	public void destroy(){
		for(int i = 0; i < myBehaviours.size(); i++){
			((Behaviour)myBehaviours.get(i)).destroy();
		}
		myBehaviours.clear();
	}
}
