package ch.maybites.gestalt.examples.excellent;
/*
 * Gestalt
 *
 * Copyright (C) 2009 Patrick Kochlik + Dennis Paul
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


import ch.maybites.gestalt.extension.quad.*;
import gestalt.render.AnimatorRenderer;
import gestalt.util.FPSCounter;

import werkzeug.interpolation.InterpolateExponential;
import werkzeug.interpolation.Interpolator;

import mathematik.Vector3f;

import ch.maybites.gestalt.extension.*;

/**
 * this demo shows how to use bezier curved quad lines.
 * note that these curves can be textured as they are made up of series of quads.
 * also note the interpolators that can be applied to color and width.
 */
public class UsingBezierCurves
    extends AnimatorRenderer {

    private NewQuadBezierCurve[] _myBezierLine;

    private float _myCounter;

    private FPSCounter _myFPSCounter;

    public void setup() {

        /* g1 */
        camera().culling = CAMERA_CULLING_BACKFACE;
        framerate(UNDEFINED);

        camera().setMode(CAMERA_MODE_LOOK_AT);

        QuadProducer.VERBOSE = true;
        

        /* create bezier line */
        _myBezierLine = new NewQuadBezierCurve[50];
        for (int i = 0; i < _myBezierLine.length; ++i) {
            //_myBezierLine[i] = drawablefactory().extensions().quadbeziercurve();
            _myBezierLine[i] = new NewQuadBezierCurve();
            QuadProducer producer = _myBezierLine[i].getProducer();
            producer.setUpVectorRef(new Vector3f(1, 0, 0));
            _myBezierLine[i].linewidth = 10;
            _myBezierLine[i].setResolution(50);
            _myBezierLine[i].material().transparent = true;
            _myBezierLine[i].material().depthtest = false;
            _myBezierLine[i].material().blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
            _myBezierLine[i].material().wireframe = false;
            _myBezierLine[i].begincolor.a = 0.03f;
            _myBezierLine[i].endcolor.a = 0.03f;
            _myBezierLine[i].setColorRedInterpolator(new Interpolator(0, 0.5f, new InterpolateExponential(1.0f)));
            _myBezierLine[i].setColorGreenInterpolator(new Interpolator(0, 0.5f, new InterpolateExponential(0.5f)));
            _myBezierLine[i].setColorBlueInterpolator(new Interpolator(0, 1f, new InterpolateExponential(0.5f)));
            _myBezierLine[i].setColorAlphaInterpolator(new Interpolator(0.08f, 0.08f, new InterpolateExponential(0.5f)));
            _myBezierLine[i].begin.set( -displaycapabilities().width / 2 + 150, 0, 0);
            _myBezierLine[i].end.set(displaycapabilities().width / 2 - 150, 0, 0);
            _myBezierLine[i].setLineWidthInterpolator(new Interpolator(10, 50, new InterpolateSinus(10, 10, 4.0f, 4.0f)));
            bin(BIN_3D).add(_myBezierLine[i]);
        }

        /* fps counter */
        _myFPSCounter = new FPSCounter();
        _myFPSCounter.setInterval(60);
        _myFPSCounter.display().position.set(displaycapabilities().width / -2 + 20, displaycapabilities().height / 2 - 20);
        _myFPSCounter.display().color.set(1);
        bin(BIN_2D_FOREGROUND).add(_myFPSCounter.display());
    }


    public void loop(float theDeltaTime) {
        _myCounter += 0.125f * theDeltaTime;
        for (int i = 0; i < _myBezierLine.length; ++i) {
            float myRadius = 10;
            double myCounter = _myCounter + PI * 2 * (float) i / (float) _myBezierLine.length;
            float myX = (float) Math.sin(myCounter) * myRadius + event().mouseX;
            float myY = (float) Math.cos(myCounter) * myRadius + event().mouseY;
            float myZ = (float) Math.cos(myCounter) * myRadius * 5;

            _myBezierLine[i].begincontrol.set( -displaycapabilities().width / 2 + 150, myY, myZ);
            _myBezierLine[i].endcontrol.set( displaycapabilities().width / 2 - 150, myY, myZ);

            _myBezierLine[i].update();
        }

        moveCamera(theDeltaTime);

        /* fps counter */
        _myFPSCounter.loop();
    }

    private void moveCamera(float theDeltaTime) {
        float mySpeed = 300f * theDeltaTime;
        /* move camera */
        if (event().keyCode == KEYCODE_A) {
            camera().forward(mySpeed);
        }
        if (event().keyCode == KEYCODE_Q) {
            camera().forward( -mySpeed);
        }
        if (event().keyCode == KEYCODE_LEFT) {
            camera().side( -mySpeed);
        }
        if (event().keyCode == KEYCODE_RIGHT) {
            camera().side(mySpeed);
        }
        if (event().keyCode == KEYCODE_DOWN) {
            camera().up( -mySpeed);
        }
        if (event().keyCode == KEYCODE_UP) {
            camera().up(mySpeed);
        }
        if (event().keyCode == KEYCODE_W) {
            camera().fovy += mySpeed;
        }
        if (event().keyCode == KEYCODE_S) {
            camera().fovy -= mySpeed;
        }
    }


    public static void main(String[] args) {
        new UsingBezierCurves().init();
    }
}
