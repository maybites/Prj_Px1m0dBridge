package ch.maybites.px1m0d.plug.drawing.effects;

import mathematik.Vector3f;
import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.drawing.*;
import ch.maybites.px1m0d.plug.drawing.effects.icons.EffctDevicePlug;
import gestalt.*;
import gestalt.render.bin.*;
import gestalt.shape.*;

import java.io.*;

public class PipeChannelReceiverPad implements Serializable{
	private static final long serialVersionUID = 1L;

	private Painter _myPainter;
	transient private AbstractBin _myRenderer;
	private int _myAnimationSwitch = 1;

	public final static Vector3f myTargetCubeSize = new Vector3f(0.15f, 0.15f, 0.05f);

	private transient Cube _myChannelTargCube;
	private transient Material _myChannelTargetCubeMaterial;

	private float _myCounter;
	
	private Vector3f _myEnd;
	
	private int _myChannel;
		
	public PipeChannelReceiverPad(Painter painter, int socketID, int channel, Color c){
		_myPainter = painter;
		_myRenderer = Canvas.getInstance().getPlugin().bin(Gestalt.BIN_3D);
		_myChannel = channel;
				
		_myEnd = new Vector3f();
		
		setup();
	}
	
	private void setup(){

		_myChannelTargetCubeMaterial = Canvas.getInstance().getPlugin().drawablefactory().material();
		_myChannelTargetCubeMaterial.transparent = false;
		_myChannelTargetCubeMaterial.wireframe = false;
		
		_myChannelTargCube = Canvas.getInstance().getPlugin().drawablefactory().cube();
		_myChannelTargCube.setMaterialRef(_myChannelTargetCubeMaterial);
		
		_myRenderer.add(_myChannelTargCube);	
	}
	
	public boolean isThisPipeChannel(int channel){
		return (channel == _myChannel)? true : false;
	}
	
	public void refreshGeometry(){		
		_myEnd.set(
				_myPainter.position.x + EffctDevicePlug.getChannelShift(_myChannel, _myPainter)[0], 
				_myPainter.position.y + EffctDevicePlug.getChannelShift(_myChannel, _myPainter)[1], 
				_myPainter.position.z);
		_myChannelTargCube.position().x = _myEnd.x;
		_myChannelTargCube.position().y = _myEnd.y;
		_myChannelTargCube.scale().x = _myPainter.spacing * myTargetCubeSize.x;
		_myChannelTargCube.scale().y = _myPainter.spacing * myTargetCubeSize.y;
		_myChannelTargCube.scale().z = _myPainter.spacing * myTargetCubeSize.z;
	}
	
	public void animate(boolean condition){
		if(condition){
			_myAnimationSwitch = 1;
		} else {
			_myAnimationSwitch = 0;
		}
	}
	
	public void update(float theDeltaTime) {
		_myCounter -= 0.005f * theDeltaTime * _myAnimationSwitch;
		//Debugger.getInstance().debugMessage(this.getClass(), "channel: " + _myChannel + " value: " + this._myPainter.myPlug.parameter.getPipeValue(this._myChannel).floatValue());
		float color  = ch.maybites.tools.Calc.map(this._myPainter.myPlug.parameter.getPipeValue(this._myChannel).floatValue(), 0f, 255f, 0.0f, 1.0f);
		
		_myChannelTargetCubeMaterial.color.r = color;
		_myChannelTargetCubeMaterial.color.g = color;
		_myChannelTargetCubeMaterial.color.b = color;
		
		float distortion = ch.maybites.tools.Calc.map(
				this._myPainter.myPlug.parameter.getPipeValue(this._myChannel).floatValue(), 
				0f, 255f, 0.5f, 1.5f);
		_myChannelTargCube.scale().x = _myPainter.spacing * myTargetCubeSize.x * distortion;
		_myChannelTargCube.scale().y = _myPainter.spacing * myTargetCubeSize.y * distortion;
		//_myChannelTargCube.position().z = _myEnd.z - zDistortion / 2;
	}
	
	public void comatize(){
		_myChannelTargCube.setActive(false);
	}

	public void reAnimate(){
		_myChannelTargCube.setActive(true);
	}
	
	public void reSurrect(){
		_myRenderer = Canvas.getInstance().getPlugin().bin(Gestalt.BIN_3D);
		setup();
		refreshGeometry();
	}

	public void destroy(){
		_myRenderer.remove(_myChannelTargCube);
	}
	
}
