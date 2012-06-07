package ch.maybites.px1m0d;

import processing.core.*; 
import ch.maybites.px1m0d.connection.Connector;
import ch.maybites.px1m0d.plug.drawing.Canvas;
import controlP5.*;
import processing.serial.*;
import gestalt.Gestalt;
import gestalt.p5.*;
import mathematik.*;

import ch.maybites.px1m0d.tools.*;

public class Px1m0d extends PApplet{
	private static final long serialVersionUID = 1L;
	
	ConfigGUI myGUI;
	Connector myConnector;
	Dispatcher myDispatcher;
	Thread myIndependentDispatcher;
	FileParameters myParameters;
	PlugFactory myPlugFactory;

	GestaltPlugIn gestalt;

	float angleX, angleY, transX, transY, transZ;

	public void setup()
	{
		size(640, 640, OPENGL);
		
		GlobalPreferences.getInstance().setDataPath(this.dataPath(""));
		Canvas.setup(this);
		gestalt = Canvas.getInstance().getPlugin();
		gestalt.camera().setMode(Gestalt.CAMERA_MODE_LOOK_AT);
		gestalt.camera().position().set(0f, 0f, -600f);
		gestalt.camera().lookat().add(0f, 0f, 0f);
		//DisplayCapabilities.listDisplayDevices();

		//		Canvas.getInstance().getPlugin().bin(Gestalt.BIN_3D).add(_myCube);

		Debugger.getInstance().infoMessage(this.getClass(), "loading settings and preferences...");
		myParameters = new FileParameters(this);
		Debugger.getInstance().infoMessage(this.getClass(), "... settings and preferences successfully loaded");

		Debugger.getInstance().infoMessage(this.getClass(), "loading plug selection and default mappings...");
		myPlugFactory = new PlugFactory(this);
		Debugger.getInstance().infoMessage(this.getClass(), "... plug selection and default mappings successfully loaded");

		myDispatcher = new Dispatcher(myPlugFactory);
		myConnector = new Connector(myParameters, myDispatcher, this);
		myConnector.setDefaultConnections();
		myGUI = new ConfigGUI(this, myConnector, myParameters, myPlugFactory, myDispatcher);
		myGUI.setup(); // has to be done after declaration.

		Debugger.setLevelToVerbose();
		//Debugger.setLevelToInfo();
	
		myConnector.appleScript.speakVoice("Bridge started");

		angleX = 0;
		angleY = 0;
		transX = 0;
		transY = 0;
		transZ = 0;

	}

	public void draw()
	{
		background(0);
		myDispatcher.draw(this);
	}
		
	public void mouseDragged(){
		gestalt.camera().position().x += (mouseX - pmouseX);
		gestalt.camera().position().y += (mouseY - pmouseY);
	}

	public void controlEvent(ControlEvent theEvent) {
		myGUI.controlEvent(theEvent);
	}

	public Serial startSerialConnection(String id){
		return new Serial(this, id, 115200);
	}

	public void serialEvent(Serial message){
		myConnector.receiveSerialMessage(message);
	}

	static public void main(String args[]) {     
	    PApplet.main(new String[] { "ch.maybites.px1m0d.Px1m0d" });  
	}

	public void destroy(){
		myConnector.deconnect();
		super.destroy();
	}
}