package ch.maybites.px1m0d.plug.drawing.effects;

import mathematik.Vector3f;
import ch.maybites.gestalt.extension.NewInterpolator;
import ch.maybites.gestalt.extension.NewQuadBezierCurve;
import ch.maybites.px1m0d.plug.drawing.*;
import gestalt.*;
import ch.maybites.gestalt.extension.InterpolateSinus;
import gestalt.render.bin.*;
import gestalt.shape.*;

import java.io.*;

public class PipeChannelSenderTube implements Serializable{
	private static final long serialVersionUID = 1L;

	private Painter _myPainter;
	transient private AbstractBin _myRenderer;
	private Color _myColor;
	private int _myAnimationSwitch = 1;

	private transient NewQuadBezierCurve _myBezierLine;

	private float _myCounter;
	
	private Vector3f _myBegin, _myEnd;

	private InterpolateSinus _mySinusWidthInterpolator;
	private InterpolateSinus _mySinusColorInterpolator;

	public Float ANIMA_BEGIN_X = new Float(0f);
	public Float ANIMA_BEGIN_Y = new Float(0f);
	public Float ANIMA_BEGIN_Z = new Float(0f);

	public Float ANIMA_END_X = new Float(0f);
	public Float ANIMA_END_Y = new Float(0f);
	public Float ANIMA_END_Z = new Float(0f);
	
	private int _mySocketID;
	private int _myChannel;
	private float _myValue;
		
	public PipeChannelSenderTube(Painter painter, int socketID, int channel, Color c){
		_myPainter = painter;
		_myRenderer = Canvas.getInstance().getPlugin().bin(Gestalt.BIN_3D);
		_mySocketID = socketID;
		_myChannel = channel;
		_myValue = 0;
		_myColor = new Color(c.r, c.g, c.b, c.a);
		
		_myBegin = new Vector3f();
		_myEnd = new Vector3f();
		
		_mySinusWidthInterpolator = new InterpolateSinus(0, 1, 10.0f, 3.0f);
		_mySinusColorInterpolator = new InterpolateSinus(0f, 0.2f, 10.0f, 0.1f);

		setup();
	}
	
	private void setup(){
		/* create bezier line */
		_myBezierLine = new NewQuadBezierCurve();
		
		_myBezierLine.linewidth = 5;
		_myBezierLine.setResolution(20);
		_myBezierLine.material().transparent = true;
		_myBezierLine.material().depthtest = false;
		_myBezierLine.material().blendmode = Gestalt.MATERIAL_NORMAL_NORMALIZE;
		_myBezierLine.material().wireframe = false;
		_myBezierLine.begincolor.a = 0.03f;
		_myBezierLine.endcolor.a = 0.03f;
		_myBezierLine.setColorRedInterpolator(new NewInterpolator(_myColor.r, _mySinusColorInterpolator));
		_myBezierLine.setColorGreenInterpolator(new NewInterpolator(_myColor.g, _mySinusColorInterpolator));
		_myBezierLine.setColorBlueInterpolator(new NewInterpolator(_myColor.b, _mySinusColorInterpolator));
		_myBezierLine.setColorAlphaInterpolator(new NewInterpolator(_myColor.a, _mySinusColorInterpolator));
		_myBezierLine.setLineWidthInterpolator(new NewInterpolator(1f, _mySinusWidthInterpolator));
		
		_myRenderer.add(_myBezierLine);	
//		_myRenderer.add(_myChannelTargetPlanes);	
	}
	
	public boolean isThisPipeChannel(int socketid, int channel){
		return (socketid == _mySocketID && channel == _myChannel)? true : false;
	}
	
	public void refreshGeometry(){
		_myBezierLine.begin = _myBegin;
		_myBezierLine.begincontrol.set( _myBegin.x, _myBegin.y, _myBegin.z - 50);
		_myBezierLine.end = _myEnd;
		_myBezierLine.endcontrol.set( _myEnd.x, _myEnd.y, _myEnd.z - 50);
		Vector3f up = new Vector3f();
		up.sub(_myBegin, _myEnd);
		_myBezierLine.getProducer().setUpVectorRef(up);
		_myBezierLine.getProducer().UPVECTOR_PROPAGATION = true;
		
	}
	
	public void setBegin(float x, float y, float z){
		_myBegin.set(x, y, z);
	}
	
	public void setEnd(float x, float y, float z){
		_myEnd.set(x, y, z);
	}

	public void animate(boolean condition){
		if(condition){
			_myAnimationSwitch = 1;
		} else {
			_myAnimationSwitch = 0;
		}
	}
	
	public void valueChange(float value){
		_myValue = value;
		_myAnimationSwitch = 2;
	}
	
	public void update(float theDeltaTime) {
		_myCounter -=  0.02f * theDeltaTime * _myAnimationSwitch;
		float amplitudeWidth = ch.maybites.tools.Calc.map(_myValue, 0f, 255f, 2.0f, 8.0f);
		float amplitudeColor = ch.maybites.tools.Calc.map(_myValue, 0f, 255f, 0.01f, 0.3f);
		_mySinusWidthInterpolator.setAmplitude(amplitudeWidth);	
		_mySinusColorInterpolator.setAmplitude(amplitudeColor);
		_mySinusWidthInterpolator.setXOffset(_myCounter);
		_mySinusColorInterpolator.setXOffset(_myCounter);

		_myBezierLine.update();
		_myAnimationSwitch = 1; // moves the pipe if messages are sent through.
//		_myChannelTargetPlaneMaterial.color.r = this._myPainter.myPlug.parameter.getPipeValue(this._myChannel).floatValue();
	}
	
	public void comatize(){
		_myBezierLine.setActive(false);
	}

	public void reAnimate(){
		_myBezierLine.setActive(true);
	}
	
	public void reSurrect(){
		_myRenderer = Canvas.getInstance().getPlugin().bin(Gestalt.BIN_3D);
		setup();
		refreshGeometry();
	}

	public void destroy(){
		_myRenderer.remove(_myBezierLine);
	}
	
}
