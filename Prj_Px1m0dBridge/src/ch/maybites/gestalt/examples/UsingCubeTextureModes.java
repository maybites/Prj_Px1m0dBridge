package ch.maybites.gestalt.examples;

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


import gestalt.render.AnimatorRenderer;
import gestalt.shape.Cube;
import gestalt.shape.material.TexturePlugin;
import gestalt.texture.Bitmaps;

import data.Resource;


/**
 * this demo shows how to use the different texture modes of a cube.
 * to find out how the texture is mapped onto the cube have a look
 * at the referenced image.
 */

public class UsingCubeTextureModes
    extends AnimatorRenderer {

    private Cube _myCube;

    public void setup() {
        /* gestalt */
        displaycapabilities().backgroundcolor.set(0.2f);

        /* create cube */
        _myCube = drawablefactory().cube();
        _myCube.setTextureMode(SHAPE_CUBE_TEXTURE_WRAP_AROUND);
        _myCube.scale().set(100, 100, 100);
        bin(BIN_3D).add(_myCube);

        /* create texture */
        TexturePlugin myTexture = drawablefactory().texture();
        myTexture.load(Bitmaps.getBitmap(Resource.getStream("demo/common/cube.png")));
        _myCube.material().addPlugin(myTexture);
    }


    public void loop(float theDeltaTime) {
        /* rotate cube */
        _myCube.rotation().x = 2 * PI * (float) event().mouseX / displaycapabilities().width;
        _myCube.rotation().y = 2 * PI * (float) event().mouseY / displaycapabilities().height;

        /**
         * switch texture modes. there are two modes to choose from.
         *
         *    SHAPE_CUBE_TEXTURE_SAME_FOR_EACH_SIDE
         *    SHAPE_CUBE_TEXTURE_WRAP_AROUND
         *
         * 'SAME_FOR_EACH_SIDE' uses the same image on all six sides while
         * 'WRAP_AROUND' takes parts from the texture and thus creates
         * different textures for each side.
         * to find out how the texture is mapped onto the cube have a look
         * at the referenced image.
         */
        if (event().keyPressed) {
            if (event().keyCode == KEYCODE_1) {
                _myCube.setTextureMode(SHAPE_CUBE_TEXTURE_SAME_FOR_EACH_SIDE);
            }
            if (event().keyCode == KEYCODE_2) {
                _myCube.setTextureMode(SHAPE_CUBE_TEXTURE_WRAP_AROUND);
            }
        }
    }


    public static void main(String[] args) {
        new UsingCubeTextureModes().init();
        new UsingCubeTextureModes().init();
    }
}
