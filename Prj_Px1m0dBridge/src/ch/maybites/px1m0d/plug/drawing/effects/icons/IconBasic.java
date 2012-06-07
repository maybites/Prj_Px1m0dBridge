package ch.maybites.px1m0d.plug.drawing.effects.icons;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.GlobalPreferences;
import ch.maybites.px1m0d.plug.drawing.*;
import data.Resource;
import gestalt.*;
import gestalt.shape.*;
import gestalt.shape.material.*;
import gestalt.texture.Bitmaps;
import gestalt.texture.FontProducer;
import gestalt.texture.bitmap.IntegerBitmap;
import mathematik.Vector2i;
import mathematik.Vector3f;


/*
 * draws directon box and shows through color alpha transparency
 * if lamp is switched on
 */
public class IconBasic extends Effect{
	private static final long serialVersionUID = 1L;

	private Painter myPainter;

	public final static Vector3f switchSize = new Vector3f(0.8f, 0.8f, 0.0f);
	public final static Vector3f switchTrans = new Vector3f(0.0f, 0.0f, 0f);
	private Color mySwitchColor;

	public final static Vector3f buttonSize = new Vector3f(0.9f, 0.9f, 0.1f);
	private Color myButtonColor;

	public final static Vector3f directionSize = new Vector3f(0.1f, 0.1f, 0.0f);
	public final static Vector3f directionTrans = new Vector3f(0.45f, 0.45f, 0f);
	private Color myDirectionColor;

	final float buttonPressedFactor = 1.2f;
	
	transient Cube _myDirection;
	transient Material _myDirectionMaterial;

	transient Cube _myButton;
	transient Material _myButtonsMaterial;

	transient Cube _mySwitch;
	transient Material _mySwitchMaterial;


	public IconBasic(Color c){
		mySwitchColor = new Color();
		mySwitchColor.set(c);

		myButtonColor = new Color();
		myButtonColor.set(c);

		myDirectionColor = new Color();
		myDirectionColor.set(c);
	}

	private void setup(){
		_myButtonsMaterial.color.set(myButtonColor.r, myButtonColor.g, myButtonColor.b, myButtonColor.a);
		_myButtonsMaterial.transparent = true;
		_myButtonsMaterial.wireframe = true;
		/* create an empty dummy bitmap */

		_myButton.position().x = myPainter.position.x;
		_myButton.position().y = myPainter.position.y;
		_myButton.scale().x = buttonSize.x * myPainter.spacing;
		_myButton.scale().y = buttonSize.y * myPainter.spacing;
		_myButton.scale().z = buttonSize.z * myPainter.spacing;
		_myButton.setMaterialRef(_myButtonsMaterial);
		//_myButton.material().addPlugin(myTexture);

		_mySwitchMaterial.color.set(mySwitchColor.r, mySwitchColor.g, mySwitchColor.b, mySwitchColor.a);
		_mySwitchMaterial.transparent = true;
		_mySwitchMaterial.wireframe = true;
		/* create an empty dummy bitmap */

		_mySwitch.position().x = myPainter.position.x;
		_mySwitch.position().y = myPainter.position.y;
		_mySwitch.position().z = myPainter.position.z + (switchTrans.z * myPainter.spacing);
		_mySwitch.scale().x = switchSize.x * myPainter.spacing;
		_mySwitch.scale().y = switchSize.y * myPainter.spacing;
		_mySwitch.scale().z = switchSize.z * myPainter.spacing;
		_mySwitch.setMaterialRef(_mySwitchMaterial);
		//_myButton.material().addPlugin(myTexture);

		_myDirectionMaterial.color.set(myDirectionColor.r, myDirectionColor.g, myDirectionColor.b, myDirectionColor.a);
		_myDirectionMaterial.transparent = true;
		/* create an empty dummy bitmap */

		_myDirection.position().x = myPainter.position.x + (directionTrans.x * myPainter.spacing * myPainter.myPlug.parameter.getXDirection());
		_myDirection.position().y = myPainter.position.y + (directionTrans.y * myPainter.spacing * myPainter.myPlug.parameter.getYDirection());
		_myDirection.position().z = myPainter.position.z + (directionTrans.z * myPainter.spacing);
		_myDirection.scale().x = directionSize.x * myPainter.spacing + Math.abs(myPainter.myPlug.parameter.getYDirection()) * directionSize.x * myPainter.spacing / 2;
		_myDirection.scale().y = directionSize.y * myPainter.spacing + Math.abs(myPainter.myPlug.parameter.getXDirection()) * directionSize.y * myPainter.spacing / 2;
		_myDirection.scale().z = directionSize.z * myPainter.spacing;
		_myDirection.setMaterialRef(_myDirectionMaterial);
		//_myButton.material().addPlugin(myTexture);


	}
	
	private void reset(){
		_myButtonsMaterial = myPainter.getCanvas().getPlugin().drawablefactory().material();
		_myButton = myPainter.getCanvas().getPlugin().drawablefactory().cube();
		_mySwitchMaterial = myPainter.getCanvas().getPlugin().drawablefactory().material();
		_mySwitch = myPainter.getCanvas().getPlugin().drawablefactory().cube();
		_myDirectionMaterial = myPainter.getCanvas().getPlugin().drawablefactory().material();
		_myDirection = myPainter.getCanvas().getPlugin().drawablefactory().cube();

		setup();
		myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).add(_myDirection);
		myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).add(_mySwitch);
		myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).add(_myButton);
	}

	public void registerToPainter(Painter p){
		myPainter = p;
		reset();
        Debugger.getInstance().debugMessage(this.getClass(), "successfully registered to painter"); 
	}

	public void update(){
		;
	}

	public void draw(){

		if(myPainter.myPlug.parameter.buttonIsReleased()){
			_myButton.setActive(false);
			//_myButtonsMaterial.color.a = myButtonColor.a;
		} else {
			_myButton.setActive(true);
			//_myButtonsMaterial.color.a = 1.0f;
		}

		if(myPainter.myPlug.parameter.switchIsOn()){
			_mySwitchMaterial.wireframe = false;
		}else{
			_mySwitchMaterial.wireframe = true;
		}

		if(myPainter.myPlug.parameter.lampIsOn()){
			_myDirectionMaterial.wireframe = false;
		}else{
			_myDirectionMaterial.wireframe = true;
		}

	}

	public void comatize(){
		_myButton.setActive(false);
		_myDirection.setActive(false);
		_mySwitch.setActive(false);
	}
	
	public void reAnimate(){
		setup();
		_myButton.setActive(true);
		_myDirection.setActive(true);
		_mySwitch.setActive(true);
	}

	public void reSurrect(){
		reset();
	}

	public void destroy(){
		myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).remove(_myButton);
		myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).remove(_myDirection);
		myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).remove(_mySwitch);
	}
}
