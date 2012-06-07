package ch.maybites.px1m0d.plug.drawing.effects.icons;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.drawing.*;
import gestalt.*;
import gestalt.shape.*;
import mathematik.*;

/*
 *  draws simple plug box and shows through 
 *  color alpha transparency if button is pressed
 */
public class EffctDummy extends Effect{
	private static final long serialVersionUID = 1L;
	public Painter myPainter;

	private Vector3f _mySize;
	private Color _myColor;
	
	final float buttonPressedFactor = 1.2f;

	transient Cube _myButton;
	transient Material _myButtonsMaterial;

	public EffctDummy(Vector3f s, Color c){
		_mySize = new Vector3f();
		_myColor = new Color();
		_mySize.set(s);
		_myColor.set(c);
	}

	public EffctDummy(Color c){
		this(new Vector3f(1.5f, 1.5f, 1.1f), c);
	}

	private void setup(){
		_myButtonsMaterial = myPainter.getCanvas().getPlugin().drawablefactory().material();
		_myButtonsMaterial.color.set(_myColor.r, _myColor.g, _myColor.b, _myColor.a);
		_myButtonsMaterial.transparent = true;
		_myButtonsMaterial.wireframe = true;

		_myButton = myPainter.getCanvas().getPlugin().drawablefactory().cube();
		_myButton.position().x = myPainter.position.x;
		_myButton.position().y = myPainter.position.y;
		_myButton.scale().x = _mySize.x * myPainter.spacing;
		_myButton.scale().y = _mySize.y * myPainter.spacing;
		_myButton.scale().z = _mySize.z * myPainter.spacing;
		_myButton.setMaterialRef(_myButtonsMaterial);

		myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).add(_myButton);
	}

	public void registerToPainter(Painter p){
		myPainter = p;
		setup();
	}

	public void update(){
		;
	}

	public void draw(){

		if(myPainter.myPlug.parameter.buttonIsReleased()){
			_myButtonsMaterial.color.a = _myColor.a;
		} else {
			_myButtonsMaterial.color.a = 0.8f;
		}
	}
	
	public void reSurrect(){
		setup();
		draw();
	}
	
	public void destroy(){
		_myButton.setActive(false);
		myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).remove(_myButton);
	}

}
