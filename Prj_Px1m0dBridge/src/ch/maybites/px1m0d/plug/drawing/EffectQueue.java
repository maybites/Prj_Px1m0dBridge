package ch.maybites.px1m0d.plug.drawing;

import java.util.Vector;
import java.io.*;

public class EffectQueue implements Serializable{
	Vector myStorage;

	public EffectQueue(){
		myStorage = new Vector(10);
	}

	public void add(Effect e){
		myStorage.add(e);
	}

	public Effect get(int index){
		return (Effect)myStorage.get(index);
	}

	public void remove(int index){
		myStorage.remove(index);
	}
	
	public void remove(Effect e){
		myStorage.removeElement(e);
	}

	public int size(){
		return myStorage.size();
	}
	
	public void destroy(){
		myStorage.clear();
	}
}
