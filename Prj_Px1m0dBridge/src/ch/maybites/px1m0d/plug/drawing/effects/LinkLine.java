package ch.maybites.px1m0d.plug.drawing.effects;

import mathematik.Vector3f;
import ch.maybites.gestalt.extension.NewInterpolator;
import ch.maybites.gestalt.extension.NewQuadBezierCurve;
import ch.maybites.px1m0d.plug.drawing.*;
import gestalt.*;
import werkzeug.interpolation.InterpolateSinus;
import gestalt.render.bin.*;
import gestalt.shape.*;

import java.io.*;

public class LinkLine implements Serializable{
	private static final long serialVersionUID = 1L;

	transient private AbstractBin _myRenderer;

	private Line _mySimpleLine;
	private Cube _myTargetCube;
	private Vector3f[] _myLinePoints;

	public LinkLine(Painter painter, Color c, int socketID_FROM, int socketID_TO){
		_myRenderer = Canvas.getInstance().getPlugin().bin(Gestalt.BIN_3D);

		/* create line */
		_mySimpleLine = Canvas.getInstance().getPlugin().drawablefactory().line();

		/* create cube */
		_myTargetCube = Canvas.getInstance().getPlugin().drawablefactory().cube();

		_mySimpleLine.linewidth = 1;
		_mySimpleLine.material().color = c;
		_mySimpleLine.material().transparent = false;
		_mySimpleLine.material().depthtest = false;
		_mySimpleLine.material().blendmode = Gestalt.MATERIAL_NORMAL_NORMALIZE;
		_mySimpleLine.material().wireframe = false;

		_myTargetCube.material().color = c;
		_myTargetCube.material().transparent = false;
		_myTargetCube.material().wireframe = true;

		LinkPathHelper linker = new LinkPathHelper(socketID_FROM, socketID_TO);
		_myLinePoints = linker.getPoints(painter);

		_mySimpleLine.points = _myLinePoints;

		_myTargetCube.position(linker.getTargetCubePosition(painter));
		Vector3f scale = linker.getTargetCubeScale(painter);
		_myTargetCube.scale(scale.x, scale.y, scale.z);

		_myRenderer.add(_myTargetCube);
		_myRenderer.add(_mySimpleLine);	
	}

	public void destroy(){
		if(_myRenderer != null){
			_myRenderer.remove(_mySimpleLine);
			_myRenderer.remove(_myTargetCube);
		}
	}

}
