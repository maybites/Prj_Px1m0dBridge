package ch.maybites.px1m0d.plug.drawing;

import ch.maybites.px1m0d.plug.*;
import processing.core.*;
import java.io.*;

public abstract class Effect implements Serializable{
	public boolean isDead = false;
	
	public abstract void registerToPainter(Painter p);
	
	public abstract void update();
	
	public abstract void draw();
	
	public abstract void destroy();
	
	public void comatize(){;}

	public void reAnimate(){;}

	public void reSurrect(){;}

	private void kill(){
		isDead = true;
	}
	
}
