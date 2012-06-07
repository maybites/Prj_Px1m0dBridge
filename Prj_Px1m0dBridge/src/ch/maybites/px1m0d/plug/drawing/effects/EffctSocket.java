package ch.maybites.px1m0d.plug.drawing.effects;

import ch.maybites.px1m0d.plug.drawing.*;
import gestalt.*;
import gestalt.shape.*;
import mathematik.*;


/*
 * draws directon box and shows through color alpha transparency
 * if lamp is switched on
 */
public class EffctSocket extends Effect{
	private static final long serialVersionUID = 1L;

	private Painter myPainter;

	Vector3f myButtonSize;
	Color myButtonColor;

	final float buttonPressedFactor = 1.2f;

	transient Cube _myButton;
	transient Material _myButtonsMaterial;

	public EffctSocket(Color c){
		myButtonSize = new Vector3f(0.5f, 0.5f, 0.01f);
		myButtonColor = c;
	}

	private void setup(){
		_myButtonsMaterial.color.set(myButtonColor.r, myButtonColor.g, myButtonColor.b, myButtonColor.a);
		_myButtonsMaterial.transparent = true;
		/* create an empty dummy bitmap */

		_myButton.position().x = myPainter.position.x;
		_myButton.position().y = myPainter.position.y;
		_myButton.scale().x = myButtonSize.x * myPainter.spacing;
		_myButton.scale().y = myButtonSize.y * myPainter.spacing;
		_myButton.scale().z = myButtonSize.z * myPainter.spacing;
		_myButton.setMaterialRef(_myButtonsMaterial);
		//_myButton.material().addPlugin(myTexture);
	}

	private void reset(){
		_myButtonsMaterial = myPainter.getCanvas().getPlugin().drawablefactory().material();
		_myButton = myPainter.getCanvas().getPlugin().drawablefactory().cube();
		setup();
		myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).add(_myButton);
	}
	
	public void registerToPainter(Painter p){
		myPainter = p;
		reset();
	}

	public void update(){
		;
	}

	public void draw(){

		if(myPainter.myPlug.parameter.buttonIsReleased()){
			_myButtonsMaterial.color.a = myButtonColor.a;
		} else {
			_myButtonsMaterial.color.a = 1.0f;
		}

	}

	public void comatize(){
		_myButton.setActive(false);
	}
	
	public void reAnimate(){
		setup();
		_myButton.setActive(true);
	}
	
	public void reSurrect(){
		reset();
	}
	
	public void destroy(){
		myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).remove(_myButton);
	}
}
