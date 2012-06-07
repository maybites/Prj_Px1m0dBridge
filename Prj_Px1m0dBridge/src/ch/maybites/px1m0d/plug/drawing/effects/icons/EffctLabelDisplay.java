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
 * Displays the lables of a plug
 */
public class EffctLabelDisplay extends Effect{
	private static final long serialVersionUID = 1L;

	private Painter myPainter;

	public final static Vector3f switchSize = new Vector3f(0.8f, 0.8f, 0.0f);
	public final static Vector3f switchTrans = new Vector3f(0.0f, 0.0f, 0f);
	private Color mySwitchColor;

	public final static Vector3f buttonSize = new Vector3f(1f, 1f, 0.05f);
	private Color myBckgrndColor;

	public final static Vector3f directionSize = new Vector3f(0.1f, 0.1f, 0.0f);
	public final static Vector3f directionTrans = new Vector3f(0.45f, 0.45f, 0f);
	private Color myDirectionColor;

	final float buttonPressedFactor = 1.2f;

	transient FontProducer _myFontProducer;

	transient Plane _myLabelPlane;
	transient Plane _myLabelBckgrndPlane;
	transient Plane _myLabelBorderPlane;
	transient Plane _myLabelValuePlane;
	transient TexturePlugin _myLabelTexture;
	
	float _labelValueScale;

	transient Plane[] _myPipeLabelPlane;
	transient Plane[] _myPipeLabelBckgrndPlane;
	transient Plane[] _myPipeLabelBorderPlane;
	transient Plane[] _myPipeLabelValuePlane;
	transient TexturePlugin[] _myPipeLabelTexture;
	float[] _labelPipeValueScale;


	private boolean isInitializied;

	public EffctLabelDisplay(Color c){
		isInitializied = false;

		mySwitchColor = new Color();
		mySwitchColor.set(c);

		myBckgrndColor = new Color();
		myBckgrndColor.set(1, 1, 1, 0.3f);

		myDirectionColor = new Color();
		myDirectionColor.set(c);

		/*
		 * get a fontproducer from the Bitmaps.
		 * the fontproducer provides certain methods to control
		 * the quality and size of the font texture. further it provides
		 * the actual bitmap that can be used as data for your texture
		 *
		 * you can use true type fonts to create the fontproducer
		 */
		_myFontProducer = Bitmaps.getFontProducer(GlobalPreferences.getInstance().getAbsolutePath("fonts/courier/CourierNew.ttf"));

	}

	private void setup(){
		if(isInitializied){
			Vector3f _myPosition = new Vector3f(
					myPainter.position.x + Math.signum(myPainter.myPlug.parameter.getXPos() - 3.5) * myPainter.spacing,
					myPainter.position.y + Math.signum(myPainter.myPlug.parameter.getYPos() - 3.5) * myPainter.spacing,
					myPainter.position.z -20);

			Color c = this.myPainter.myPlug.parameter.getIconColor();
			myBckgrndColor.set(c.r / 4, c.g / 4, c.b / 4, 1f);
			float lableYSpacing = myPainter.spacing / 1.2f;
			float xSpace = myPainter.spacing * 1.5f;
			float ySpace = myPainter.spacing / 2.5f;

			/* set size and quality of the font texture */
			_myFontProducer.setSize(14);
			_myFontProducer.setSmoothFactor(8);
			_myFontProducer.setLineWidth(1);
			//_myFontProducer.setFontColor(new Color(0f,0f,0f,1f));
			_myFontProducer.setQuality(gestalt.Gestalt.FONT_QUALITY_LOW);
			_myFontProducer.setAlignment(gestalt.Gestalt.FONT_ALIGN_LEFT);
			/* the font is a little extreme so we create an invisible border around. */
			//_myFontProducer.setImageBorder(new Vector2i(20, 20));

			_myLabelTexture.load(_myFontProducer.getBitmap(" > " + this.myPainter.myPlug.parameter.getIconLabel() + " < "));
			_myLabelPlane.material().addPlugin(_myLabelTexture);
			_myLabelPlane.setPlaneSizeToTextureSize();
			_myLabelPlane.material().color.a = 1f;
			_myLabelPlane.position().x = _myPosition.x;
			_myLabelPlane.position().y = _myPosition.y + lableYSpacing - ySpace / 10;
			_myLabelPlane.position().z = _myPosition.z - 1;
			_myLabelPlane.rotation(0, 0, 3.14159f);

			_myLabelBckgrndPlane.material().addPlugin(_myLabelTexture);
			_myLabelBckgrndPlane.setPlaneSizeToTextureSize();
			_myLabelBckgrndPlane.material().removePlugin(_myLabelTexture);
			_myLabelBckgrndPlane.material().color = myBckgrndColor;
			_myLabelBckgrndPlane.material().wireframe = false;
			_myLabelBckgrndPlane.position().x = _myPosition.x;
			_myLabelBckgrndPlane.position().y = _myPosition.y + lableYSpacing;
			_myLabelBckgrndPlane.position().z = _myPosition.z;

			_myLabelBorderPlane.material().addPlugin(_myLabelTexture);
			_myLabelBorderPlane.setPlaneSizeToTextureSize();
			_myLabelBorderPlane.material().removePlugin(_myLabelTexture);
			_myLabelBorderPlane.material().color = c;
			_myLabelBorderPlane.material().wireframe = true;
			_myLabelBorderPlane.position().x = _myPosition.x;
			_myLabelBorderPlane.position().y = _myPosition.y + lableYSpacing;
			_myLabelBorderPlane.position().z = _myPosition.z;

			_myLabelValuePlane.material().addPlugin(_myLabelTexture);
			_myLabelValuePlane.setPlaneSizeToTextureSize();
			_myLabelValuePlane.material().removePlugin(_myLabelTexture);
			_myLabelValuePlane.material().color = c;
			_myLabelValuePlane.material().wireframe = false;
			_myLabelValuePlane.position().x = _myPosition.x;
			_myLabelValuePlane.position().y = _myPosition.y + lableYSpacing - ySpace / 2.4f;
			_myLabelValuePlane.position().z = _myPosition.z;
			_myLabelValuePlane.scale().y = ySpace / 6;
			_labelValueScale = _myLabelValuePlane.scale().x;

			for(int i = 0; i < 9; i++){
				float[] shift = EffctDevicePlug.getChannelShift(i, myPainter, 1.0f);
				float xShift = shift[0] * xSpace;
				float yShift = shift[1] * ySpace;
				Vector3f pipeLabelPosition = new Vector3f(
						_myPosition.x + xShift, 
						_myPosition.y + yShift, 
						_myPosition.z + 5);

				_myPipeLabelTexture[i].load(_myFontProducer.getBitmap(" "+ i + ":" + this.myPainter.myPlug.parameter.getIconLabelPipe(i) + " "));

				/* set the texture in the material of your shape */
				_myPipeLabelPlane[i].material().addPlugin(_myPipeLabelTexture[i]);
				_myPipeLabelPlane[i].setPlaneSizeToTextureSize();
				_myPipeLabelPlane[i].material().color.a = 1f;
				_myPipeLabelPlane[i].position(pipeLabelPosition.x, pipeLabelPosition.y - ySpace / 10, pipeLabelPosition.z - 1);
				_myPipeLabelPlane[i].rotation(0, 0, 3.14159f);     

				_myPipeLabelBckgrndPlane[i].material().addPlugin(_myPipeLabelTexture[i]);
				_myPipeLabelBckgrndPlane[i].setPlaneSizeToTextureSize();
				_myPipeLabelBckgrndPlane[i].material().removePlugin(_myPipeLabelTexture[i]);
				_myPipeLabelBckgrndPlane[i].material().wireframe = false;
				_myPipeLabelBckgrndPlane[i].material().color = myBckgrndColor;
				_myPipeLabelBckgrndPlane[i].setPositionRef(pipeLabelPosition);
				_myPipeLabelBckgrndPlane[i].rotation(0, 0, 3.14159f);    

				_myPipeLabelBorderPlane[i].material().addPlugin(_myPipeLabelTexture[i]);
				_myPipeLabelBorderPlane[i].setPlaneSizeToTextureSize();
				_myPipeLabelBorderPlane[i].material().removePlugin(_myPipeLabelTexture[i]);
				_myPipeLabelBorderPlane[i].material().wireframe = true;
				_myPipeLabelBorderPlane[i].material().color = c;
				_myPipeLabelBorderPlane[i].setPositionRef(pipeLabelPosition);
				_myPipeLabelBorderPlane[i].rotation(0, 0, 3.14159f);  

				_myPipeLabelValuePlane[i].material().addPlugin(_myPipeLabelTexture[i]);
				_myPipeLabelValuePlane[i].setPlaneSizeToTextureSize();
				_myPipeLabelValuePlane[i].material().removePlugin(_myPipeLabelTexture[i]);
				_myPipeLabelValuePlane[i].material().wireframe = false;
				_myPipeLabelValuePlane[i].material().color = c;
				_myPipeLabelValuePlane[i].position(pipeLabelPosition.x, pipeLabelPosition.y - ySpace / 2.4f, pipeLabelPosition.z);
				_myPipeLabelValuePlane[i].rotation(0, 0, 3.14159f);  
				_myPipeLabelValuePlane[i].scale().y = ySpace / 6;
				_labelPipeValueScale[i] = _myPipeLabelValuePlane[i].scale().x;

			}
		}

	}

	private void reset(){
		isInitializied = true;

		/* create a plane */
		_myLabelPlane = myPainter.getCanvas().getPlugin().drawablefactory().plane();
		_myLabelBckgrndPlane = myPainter.getCanvas().getPlugin().drawablefactory().plane();
		_myLabelBorderPlane = myPainter.getCanvas().getPlugin().drawablefactory().plane();
		_myLabelValuePlane = myPainter.getCanvas().getPlugin().drawablefactory().plane();
		_labelValueScale = 0f;

		/* create a texture */
		_myLabelTexture = myPainter.getCanvas().getPlugin().drawablefactory().texture();

		_myPipeLabelPlane = new Plane[9];
		_myPipeLabelTexture = new TexturePlugin[9];
		_myPipeLabelBckgrndPlane = new Plane[9];
		_myPipeLabelBorderPlane = new Plane[9];
		_myPipeLabelValuePlane = new Plane[9];
		_labelPipeValueScale = new float[9];
		for(int i = 0; i < 9; i++){
			_myPipeLabelPlane[i] = myPainter.getCanvas().getPlugin().drawablefactory().plane();
			_myPipeLabelBckgrndPlane[i] = myPainter.getCanvas().getPlugin().drawablefactory().plane();
			_myPipeLabelBorderPlane[i] = myPainter.getCanvas().getPlugin().drawablefactory().plane();
			_myPipeLabelValuePlane[i] = myPainter.getCanvas().getPlugin().drawablefactory().plane();
			_myPipeLabelTexture[i] = myPainter.getCanvas().getPlugin().drawablefactory().texture();
			_labelPipeValueScale[i] = 0f;
		}

		setup();
		
		myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).add(_myLabelPlane);
		myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).add(_myLabelBckgrndPlane);
		myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).add(_myLabelBorderPlane);
		myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).add(_myLabelValuePlane);
		for(int i = 0; i < 9; i++){
			myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).add(_myPipeLabelPlane[i]);
			myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).add(_myPipeLabelBckgrndPlane[i]);
			myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).add(_myPipeLabelBorderPlane[i]);
			myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).add(_myPipeLabelValuePlane[i]);
		}	
	}

	public void registerToPainter(Painter p){
		myPainter = p;
		isDead = false;
		reset();
		Debugger.getInstance().debugMessage(this.getClass(), "successfully registered to painter"); 
	}

	public void update(){
		;
	}

	public void draw(){

		_myLabelValuePlane.scale().x = _labelValueScale * myPainter.myPlug.parameter.getNatebuValue() / 255;
		for(int i = 0; i < 9; i++){
			_myPipeLabelValuePlane[i].scale().x = _labelPipeValueScale[i] * myPainter.myPlug.parameter.getPipeValue(i) / 255;
		}
		
	}

	public void comatize(){
		_myLabelPlane.setActive(false);
		_myLabelBckgrndPlane.setActive(false);
		_myLabelBorderPlane.setActive(false);
		_myLabelValuePlane.setActive(false);
		for(int i = 0; i < 9; i++){
			_myPipeLabelPlane[i].setActive(false);
			_myPipeLabelBckgrndPlane[i].setActive(false);
			_myPipeLabelBorderPlane[i].setActive(false);
			_myPipeLabelValuePlane[i].setActive(false);
		}	
	}

	public void reAnimate(){
		setup();
		_myLabelBckgrndPlane.setActive(true);
		_myLabelPlane.setActive(true);
		_myLabelBorderPlane.setActive(true);
		_myLabelValuePlane.setActive(true);
		for(int i = 0; i < 9; i++){
			_myPipeLabelPlane[i].setActive(true);
			_myPipeLabelBckgrndPlane[i].setActive(true);
			_myPipeLabelBorderPlane[i].setActive(true);
			_myPipeLabelValuePlane[i].setActive(true);
		}	
	}

	public void reSurrect(){
		reset();
	}

	public void destroy(){
		if(isInitializied){
			myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).remove(_myLabelPlane);
			myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).remove(_myLabelBckgrndPlane);
			myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).remove(_myLabelBorderPlane);
			myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).remove(_myLabelValuePlane);
			for(int i = 0; i < 9; i++){
				myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).remove(_myPipeLabelPlane[i]);
				myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).remove(_myPipeLabelBckgrndPlane[i]);
				myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).remove(_myPipeLabelBorderPlane[i]);
				myPainter.getCanvas().getPlugin().bin(Gestalt.BIN_3D).remove(_myPipeLabelValuePlane[i]);
			}	
		}
	}

	public void kill(){
		isDead = true;
		destroy();
	}

}
