package ch.maybites.px1m0d.plug.drawing.effects.icons;

import ch.maybites.px1m0d.plug.drawing.*;
import gestalt.*;
import gestalt.shape.*;
import gestalt.shape.material.*;
import gestalt.texture.bitmap.IntegerBitmap;
import mathematik.Vector3f;


/*
 * draws directon box and shows through color alpha transparency
 * if lamp is switched on
 */
public class EffctDevicePlug extends Effect{
	private static final long serialVersionUID = 1L;

	private Painter myPainter;

	private Vector3f mySwitchSize;
	private Vector3f mySwitchTrans;
	private Color mySwitchColor;

	private Vector3f myButtonSize;
	private Color myButtonColor;

	private Vector3f myDirectionSize;
	private Vector3f myDirectionTrans;
	private Color myDirectionColor;

	final float buttonPressedFactor = 1.2f;

	transient Cube _myDirection;
	transient Material _myDirectionMaterial;

	transient Material _myButtonsMaterial;

	transient Cube _mySwitch;
	transient Material _mySwitchMaterial;

	transient Plane[] _myChannelPlanes;
	transient Material[] _myChannelPlaneMaterials;

	public EffctDevicePlug(Color c){
		mySwitchSize = new Vector3f(0.8f, 0.8f, 0.1f);
		mySwitchTrans = new Vector3f(0.0f, 0.0f, 0.0f);
		mySwitchColor = new Color();
		mySwitchColor.set(c);
		mySwitchColor.a = 0.1f;

		myButtonSize = new Vector3f(0.5f, 0.5f, 0.01f);
		myButtonColor = new Color();
		myButtonColor.set(c);
		myButtonColor.a = 0.10f;

		myDirectionSize = new Vector3f(0.2f, 0.05f, 0.01f);
		myDirectionTrans = new Vector3f(0.43f, 0.43f, 0f);
		myDirectionColor = new Color();
		myDirectionColor.set(c);
		myDirectionColor.a = .4f;
	}

	private void setup(){
		_myButtonsMaterial.color.set(myButtonColor.r, myButtonColor.g, myButtonColor.b, myButtonColor.a);
		_myButtonsMaterial.transparent = true;
		/* create an empty dummy bitmap */

		_mySwitchMaterial.color.set(mySwitchColor.r, mySwitchColor.g, mySwitchColor.b, mySwitchColor.a);
		_mySwitchMaterial.transparent = true;
		_mySwitchMaterial.wireframe = true;
		/* create an empty dummy bitmap */

		_mySwitch.position().x = myPainter.position.x;
		_mySwitch.position().y = myPainter.position.y;
		_mySwitch.position().z = myPainter.position.z + (mySwitchTrans.z * myPainter.spacing);
		_mySwitch.scale().x = mySwitchSize.x * myPainter.spacing;
		_mySwitch.scale().y = mySwitchSize.y * myPainter.spacing;
		_mySwitch.scale().z = mySwitchSize.z * myPainter.spacing;
		_mySwitch.setMaterialRef(_mySwitchMaterial);
		//_myButton.material().addPlugin(myTexture);

		_myDirectionMaterial.color.set(myDirectionColor.r, myDirectionColor.g, myDirectionColor.b, myDirectionColor.a);
		_myDirectionMaterial.transparent = true;
		/* create an empty dummy bitmap */

		_myDirection.position().x = myPainter.position.x + (myDirectionTrans.x * myPainter.spacing * myPainter.myPlug.parameter.getXDirection());
		_myDirection.position().y = myPainter.position.y + (myDirectionTrans.y * myPainter.spacing * myPainter.myPlug.parameter.getYDirection());
		_myDirection.position().z = myPainter.position.z + (myDirectionTrans.z * myPainter.spacing);
		_myDirection.scale().x = Math.abs(myDirectionSize.x * myPainter.spacing * myPainter.myPlug.parameter.getXDirection() + myDirectionSize.y * myPainter.spacing * myPainter.myPlug.parameter.getXDirection());
		_myDirection.scale().y = Math.abs(myDirectionSize.y * myPainter.spacing * myPainter.myPlug.parameter.getYDirection() + myDirectionSize.x * myPainter.spacing * myPainter.myPlug.parameter.getYDirection());
		_myDirection.scale().z = myDirectionSize.z * myPainter.spacing;
		_myDirection.setMaterialRef(_myButtonsMaterial);
		//_myButton.material().addPlugin(myTexture);

		for(int i = 0; i < 9; i++){
			_myChannelPlaneMaterials[i].color.set(myButtonColor.r, myButtonColor.g, myButtonColor.b, myButtonColor.a);
			_myChannelPlaneMaterials[i].transparent = true;
			_myChannelPlaneMaterials[i].wireframe = true;
			
			_myChannelPlanes[i].position().x = myPainter.position.x + getChannelShift(i, myPainter)[0];
			_myChannelPlanes[i].position().y = myPainter.position.y + getChannelShift(i, myPainter)[1];
			_myChannelPlanes[i].scale().x = myPainter.spacing * 0.23f;
			_myChannelPlanes[i].scale().y = myPainter.spacing * 0.23f;
			_myChannelPlanes[i].setMaterialRef(_myChannelPlaneMaterials[i]);
		}

	}
	
	private void reset(){
		_myButtonsMaterial = myPainter.getCanvas().getPlugin().drawablefactory().material();
		_mySwitchMaterial = myPainter.getCanvas().getPlugin().drawablefactory().material();
		_mySwitch = myPainter.getCanvas().getPlugin().drawablefactory().cube();
		_myDirectionMaterial = myPainter.getCanvas().getPlugin().drawablefactory().material();
		_myDirection = myPainter.getCanvas().getPlugin().drawablefactory().cube();

		_myChannelPlanes = new Plane[9];
		_myChannelPlaneMaterials = new Material[9];
		for(int i = 0; i < 9; i++){
			_myChannelPlanes[i] = myPainter.getCanvas().getPlugin().drawablefactory().plane();
			_myChannelPlaneMaterials[i] = myPainter.getCanvas().getPlugin().drawablefactory().material();
		}
		
		setup();
		myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).add(_myDirection);
		myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).add(_mySwitch);
		for(int i = 0; i < 9; i++){
			myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).add(_myChannelPlanes[i]);
		}
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

		if(myPainter.myPlug.parameter.switchIsOn()){
			_mySwitchMaterial.color.a = 1.0f;
		}else{
			_mySwitchMaterial.color.a = mySwitchColor.a;
		}

		if(myPainter.myPlug.parameter.lampIsOn()){
			_myDirectionMaterial.color.a = 1.0f;
		}else{
			_myDirectionMaterial.color.a = myDirectionColor.a;
		}

	}

	public void comatize(){
		_myDirection.setActive(false);
		_mySwitch.setActive(false);
		for(int i = 0; i < 9; i++){
			_myChannelPlanes[i].setActive(false);
		}
	}
	
	public void reAnimate(){
		setup();
		_myDirection.setActive(true);
		_mySwitch.setActive(true);
		for(int i = 0; i < 9; i++){
			_myChannelPlanes[i].setActive(true);
		}
	}

	public void reSurrect(){
		reset();
	}

	public void destroy(){
		myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).remove(_myDirection);
		myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).remove(_mySwitch);
		for(int i = 0; i < 9; i++){
			myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).remove(_myChannelPlanes[i]);
		}
	}
	
	/*
	 * Helps to get the offset for the different channel
	 */
	public static float[] getChannelShift(int channel, Painter painter){
		float CHANNELSHIFT = painter.spacing / 4;
		return getChannelShift(channel, painter, CHANNELSHIFT);
	}

	/*
	 * Helps to get the offset for the different channel
	 */
	public static float[] getChannelShift(int channel, Painter painter, float shift){
		float CHANNELSHIFT = shift;
		switch(channel){
		case 8:
			float[] ret1 = {0, CHANNELSHIFT};
			return ret1;
		case 1:
			float[] ret2 = {-CHANNELSHIFT,CHANNELSHIFT};
			return ret2;
		case 2:
			float[] ret3 = {-CHANNELSHIFT, 0};
			return ret3;
		case 3:
			float[] ret4 = {-CHANNELSHIFT, -CHANNELSHIFT};
			return ret4;
		case 4:
			float[] ret5 = {0, -CHANNELSHIFT};
			return ret5;
		case 5:
			float[] ret6 = {CHANNELSHIFT, -CHANNELSHIFT};
			return ret6;
		case 6:
			float[] ret7 = {CHANNELSHIFT, 0};
			return ret7;
		case 7:
			float[] ret8 = {CHANNELSHIFT,CHANNELSHIFT};
			return ret8;			
		}
		float[] ret0 = {0,0};
		return ret0;
	}

	
}
