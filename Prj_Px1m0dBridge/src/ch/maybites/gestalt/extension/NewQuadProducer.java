package ch.maybites.gestalt.extension;

/*
 * Gestalt Extension
 *
 * Copyright (C) 2009 Patrick Kochlik + Dennis Paul
 * 
 * written 2009 by Martin Fršhlich, based on Gestalts QuadProducer
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * {@link http://www.gnu.org/licenses/lgpl.html}
 *
 */

import mathematik.Vector3f;
import ch.maybites.gestalt.extension.quad.*;
import gestalt.shape.Color;
import java.io.Serializable;

import gestalt.shape.Color;

import mathematik.Vector3f;

import werkzeug.interpolation.Interpolator;

public class NewQuadProducer  extends QuadProducer implements Serializable {

	    boolean adaptiveTexCoords = true;

	    private int _myTexCoordsMode = QUAD_TEXCORDS_NORMALIZED;

	    private float _myTexCoordsFixedLength = 1;

	    private Vector3f _myProposedUpVector;

	    private final Vector3f _myForwardVector;

	    private Interpolator _myInterpolator;

	    /* temp helpers */
	    private Vector3f _mySideVector;

	    private Vector3f _myUpVector;

	    public NewQuadProducer() {
	        _myProposedUpVector = new Vector3f(0, 0, 1);
	        _myForwardVector = new Vector3f();

	        _mySideVector = new Vector3f();
	        _myUpVector = new Vector3f();

	        _myInterpolator = null;
	    }


	    public Vector3f upvector() {
	        return _myProposedUpVector;
	    }


	    public void setUpVectorRef(Vector3f theUpVectorRef) {
	        _myProposedUpVector = theUpVectorRef;
	    }


	    public void setLineWidthInterpolator(Interpolator theInterpolator) {
	        _myInterpolator = theInterpolator;
	    }


	    public void setTexCoordsMode(int theMode) {
	        _myTexCoordsMode = theMode;
	    }


	    public void setTexCoordsFixedLength(float theTexCoordsFixedLength) {
	        _myTexCoordsFixedLength = theTexCoordsFixedLength;
	    }


	    public void getQuadStrip(final Vector3f[] theLines,
	                             final Color[] theColors,
	                             final float[] theStrokeSizes,
	                             final float theStrokeSize,
	                             final QuadFragment[] theQuadFragments) {
	        if (theLines != null &&
	            theQuadFragments != null &&
	            theQuadFragments.length == theLines.length &&
	            (theStrokeSizes == null || theStrokeSizes.length == theQuadFragments.length) &&
	            theLines.length >= 2) {

	            if (theColors != null && theLines.length != theColors.length) {
	                if (VERBOSE) {
	                    System.err.println(
	                        "### WARNING QuadProducer.getQuadStrip / malformed data / lines.length != colors.length");
	                }
	                return;
	            }

	            final Vector3f myPreviousUpVector = new Vector3f(_myProposedUpVector);

	            float myTotalLineLength = 0;
	            if (_myTexCoordsMode == QUAD_TEXCORDS_ADAPTIVE) {
	                for (int i = 0; i < theLines.length - 1; i++) {
	                    Vector3f myDistance = new Vector3f(theLines[i]);
	                    myDistance.sub(theLines[i + 1]);
	                    myTotalLineLength += myDistance.length();
	                }
	            }

	            float myOldRatio = 0;
	            for (int i = 0; i < theLines.length; ++i) {
	                if (theQuadFragments[i] == null) {
	                    theQuadFragments[i] = new QuadFragment();
	                }

	                /* linewidth */
	                final float myPercentage = (float) i / (float) (theLines.length - 1);
	                final float myLineWidthInterpolation;
	                if (_myInterpolator == null) {
	                    if (theStrokeSizes != null) {
	                        myLineWidthInterpolation = theStrokeSizes[i];
	                    } else {
	                        myLineWidthInterpolation = theStrokeSize;
	                    }
	                } else {
	                    myLineWidthInterpolation = _myInterpolator.get(myPercentage);
	                }

	                if (i < theLines.length - 1) {
	                    _myForwardVector.sub(theLines[i + 1], theLines[i]);
	                }

	                getSideAndUpVector(myPreviousUpVector);

	                /* store upvector for next fragment */
	                if (UPVECTOR_PROPAGATION) {
	                    myPreviousUpVector.set(_myUpVector);
	                    /** @todo why in hell do we need to do this */
	                    myPreviousUpVector.scale( -1);
	                }

	                _mySideVector.scale(myLineWidthInterpolation / 2.0f);
	                theQuadFragments[i].pointA.sub(theLines[i], _mySideVector);
	                theQuadFragments[i].pointB.add(theLines[i], _mySideVector);

	                /* color */
	                if (theColors != null) {
	                    theQuadFragments[i].colorA = theColors[i];
	                    theQuadFragments[i].colorB = theColors[i];
	                }

	                /* texture coordinates */
	                float myRatio = 0;
	                switch (_myTexCoordsMode) {
	                    case (QUAD_TEXCORDS_NORMALIZED):
	                        myRatio = myPercentage;
	                        break;
	                    case (QUAD_TEXCORDS_FIXEDLENGTH):
	                        if (i > 0 && i < theLines.length - 1) {
	                            Vector3f myDistance = new Vector3f(theLines[i - 1]);
	                            myDistance.sub(theLines[i]);
	                            myRatio = myDistance.length() / _myTexCoordsFixedLength + myOldRatio;
	                        } else if (i == 0) {
	                            myRatio = 0;
	                        } else if (i == theLines.length - 1) {
	                            myRatio = 1;
	                        }
	                        myOldRatio = myRatio;
	                        break;
	                    case (QUAD_TEXCORDS_ADAPTIVE):
	                        if (i > 0 && i < theLines.length - 1) {
	                            Vector3f myDistance = new Vector3f(theLines[i - 1]);
	                            myDistance.sub(theLines[i]);
	                            myRatio = myDistance.length() / myTotalLineLength + myOldRatio;
	                        } else if (i == 0) {
	                            myRatio = 0;
	                        } else if (i == theLines.length - 1) {
	                            myRatio = 1;
	                        }
	                        myOldRatio = myRatio;
	                        break;
	                }
	                theQuadFragments[i].texcoordA.set(myRatio, 1);
	                theQuadFragments[i].texcoordB.set(myRatio, 0);

	                /* normal */
	                theQuadFragments[i].normal.set(_myUpVector);
	            }
	        } else {
	            if (VERBOSE) {
	                System.err.println("### WARNING QuadProducer.getQuadStrip / malformed data");
	            }
	            return;
	        }
	    }


	    private void getSideAndUpVector(final Vector3f theProposedUpVector) {
	        /* get sideVector */
	        _mySideVector.cross(theProposedUpVector, _myForwardVector);
	        _mySideVector.normalize();
	        /* get 'real' upVector */
	        _myUpVector.cross(_mySideVector, _myForwardVector);
	        _myUpVector.normalize();
	    }
	}
