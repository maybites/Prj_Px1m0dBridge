package ch.maybites.px1m0d.plug.behaviours;

import java.io.*;

public interface Behaviour extends Serializable{

	public boolean takeControl();
	
	public void action();
	
	public void destroy();
	
	public void reSurrect();
	
	public void comatize();
	
	public void reAnimate();
}
