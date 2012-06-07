package ch.maybites.px1m0d.plug.drawing.effects;

import ch.maybites.px1m0d.plug.*;
import ch.maybites.px1m0d.plug.drawing.Effect;
import ch.maybites.px1m0d.plug.drawing.Painter;
import gestalt.*;
import gestalt.shape.*;
import java.util.*;
import mathematik.*;


/*
 * draws directon box and shows through color alpha transparency
 * if lamp is switched on
 */
public class EffctLinkLine extends Effect{
	private static final long serialVersionUID = 1L;
	private Plug _myPlug;
	private Painter myPainter;

	private LinkLine _myLinkLine;

	private Color _myColor;

	private int _myLinkSocketID;

	public EffctLinkLine(Plug plug){
		_myPlug = plug;
		_myColor = new Color(1f, 0.5f, 0.5f, 0.1f);
		link();
	}

	public void link(){
		unlink();
		if(_myPlug.parameter.plugIsLinked()){
			_myLinkLine = new LinkLine(myPainter, _myColor,  _myPlug.parameter.getSocketID(), _myPlug.parameter.getLink().parameter.getSocketID());
			_myLinkSocketID = _myPlug.parameter.getLink().parameter.getSocketID();
			_myColor = _myPlug.parameter.getLink().parameter.getIconColor();
		}
	}

	public void unlink(){
		if(_myLinkLine != null){
			_myLinkLine.destroy();
			_myLinkLine = null;
		}
	}

	public void registerToPainter(Painter p){
		myPainter = p;
	}

	public void update(){
		/*
		if(_myPlug.myLink != null){
			if(_myLinkSocketID != _myPlug.myLink.mySocketID){
				link();
			}
		}
		*/
	}

	public void draw(){;}

	public void comatize(){
		unlink();
	}

	public void reAnimate(){
		link();
	}

	public void reSurrect(){
		link();
	}

	public void destroy(){
		unlink();
	}
}
