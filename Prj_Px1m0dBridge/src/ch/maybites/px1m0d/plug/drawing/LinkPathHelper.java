package ch.maybites.px1m0d.plug.drawing;

import ch.maybites.px1m0d.plug.drawing.effects.icons.IconBasic;
import mathematik.*;

public class LinkPathHelper {

	int startX, startY, endX, endY;
	int startU, startV, endU, endV;
	
	int cornerU, cornerV;
	boolean hasCorner = false;

	/**       X  0     2     4     6     8     10    12    14
	 *  Y V   U     1     3     5     7     9     11    13
	 *        
	 *  0       [ ]   [ ]   [ ]   [ ]   [ ]   [ ]   [ ]   [ ]
	 *
	 * 	  1         o     o     o     o     o     o     o
	 *
	 *  2       [ ]   [ ]   [ ]   [ ]   [ ]   [ ]   [ ]   [ ]
	 *
	 * 	  3         o     o     o     o     o     o     o
	 *
	 *  4       [ ]   [ ]   [ ]   [ ]   [ ]   [ ]   [ ]   [ ]
	 *
	 * 	  5         o     o     o     o     o     o     o
	 *
	 *  6       [ ]   [ ]   [ ]   [ ]   [e]   [ ]   [ ]   [ ]
	 *                                 /
	 * 	  7         x-----o-----o-----o     o     o     o
	 *              |
	 *  8       [ ] | [ ]   [ ]   [ ]   [ ]   [ ]   [ ]   [ ]
	 *              |
	 * 	  9         o     o     o     o     o     o     o
	 *              |
	 *  10      [ ] | [ ]   [ ]   [ ]   [ ]   [ ]   [ ]   [ ]
	 *              |
	 * 	  11        o     o     o     o     o     o     o
	 *              |
	 *  12      [ ] | [ ]   [ ]   [ ]   [ ]   [ ]   [ ]   [ ]
	 *              |
	 * 	  13        o     o     o     o     o     o     o
	 *             /
	 *  14      [s]   [ ]   [ ]   [ ]   [ ]   [ ]   [ ]   [ ]
	 * 
	 */

	public LinkPathHelper(int startSocketID, int endSocketID){
		startX = getCol(startSocketID);
		startY = getRow(startSocketID);
		endX = getCol(endSocketID);
		endY = getRow(endSocketID);
		setUVCoordinates();
		checkForCorner();
	}
	
	public Vector3f[] getPoints(Painter painter){		
		Vector3f pointSp = new Vector3f(
				(7 - startY) * painter.spacing / 2 ,
				(7 - startX) * painter.spacing / 2 ,
				0);

		Vector3f pointSs = new Vector3f(
				(7 - startV) * painter.spacing / 2 ,
				(7 - startU) * painter.spacing / 2 ,
				0);
		
		// Startpoint Correction to its corner:
		pointSp = new Vector3f(
				pointSs.x - (pointSs.x - pointSp.x) * (1 - IconBasic.buttonSize.x) ,
				pointSs.y - (pointSs.y - pointSp.y) * (1 - IconBasic.buttonSize.y) ,
				0);
		

		Vector3f pointC = new Vector3f();
		if(hasCorner){
			pointC = new Vector3f(
					(7 - cornerV) * painter.spacing / 2 ,
					(7 - cornerU) * painter.spacing / 2 ,
					0);
		}

		Vector3f pointEs = new Vector3f(
				(7 - endV) * painter.spacing / 2 ,
				(7 - endU) * painter.spacing / 2 ,
				0);

		Vector3f pointEp = new Vector3f(
				(7 - endY) * painter.spacing / 2 ,
				(7 - endX) * painter.spacing / 2 ,
				0);

		// Endpoint Correction to its corner:

		pointEp = new Vector3f(
				pointEs.x - (pointEs.x - pointEp.x) * (1 - IconBasic.switchSize.x) ,
				pointEs.y - (pointEs.y - pointEp.y) * (1 - IconBasic.switchSize.y) ,
				0);

		Vector3f[] thePoints;
		
		if(hasCorner){
			thePoints = new Vector3f[5];
			thePoints[0] = pointSp;
			thePoints[1] = pointSs;
			thePoints[2] = pointC;
			thePoints[3] = pointEs;
			thePoints[4] = pointEp;
		} else {
			thePoints = new Vector3f[4];
			thePoints[0] = pointSp;
			thePoints[1] = pointSs;
			thePoints[2] = pointEs;
			thePoints[3] = pointEp;
		}

		return thePoints;
	}
	
	public Vector3f getTargetCubePosition(Painter painter){
		return new Vector3f(
				(7 - startY) * painter.spacing / 2 ,
				(7 - startX) * painter.spacing / 2 ,
				0);
	}

	public Vector3f getTargetCubeScale(Painter painter){
		return new Vector3f(
				IconBasic.buttonSize.x * painter.spacing ,
				IconBasic.buttonSize.y * painter.spacing ,
				0.3f * painter.spacing );
	}

	private void checkForCorner(){
		if(startU != endU || startV != endV){
			hasCorner = true;
			cornerU = startU;
			cornerV = endV;
		}
	}

	private void setUVCoordinates(){
		int diffX = endX - startX;
		int diffY = endY - startY;
		if(diffX != 0){
			startU = startX + getSign(diffX);
			endU = endX - getSign(diffX);
		} else {
			startU = startX + getSign(startX - 7);
			endU = endX + getSign(startX - 7);
		}

		if(diffY != 0){
			startV = startY + getSign(diffY);
			endV = endY - getSign(diffY);
		} else {
			startV = startY + getSign(startY - 7);
			endV = endY + getSign(startY - 7);
		}
	}
	
	private int getSign(int value){
		return (value != 0)? value / Math.abs(value) : 0;
	}

	private int getRow(int socketID){
		return (socketID % 8) * 2;
	}

	private int getCol(int socketID){
		return (socketID / 8) * 2;
	}

}
