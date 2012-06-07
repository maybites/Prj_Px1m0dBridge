package ch.maybites.px1m0d.plug.gestures;

import ch.maybites.px1m0d.message.*;
import java.io.*;

public interface Gesture extends Serializable{
	
	public void incomming(Message msg);
	
	public void destroy();
	
	public void reSurrect();
	
	public void comatize();
	
	public void reAnimate();
	
}
