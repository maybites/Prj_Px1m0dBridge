package ch.maybites.gestalt.extension;

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
 *
 * This class is a variation on the JoglQuadBezierCurve
 */


import javax.media.opengl.GL;

import gestalt.context.GLContext;
import gestalt.impl.jogl.context.JoglGLContext;
import gestalt.impl.jogl.shape.JoglMaterial;
import ch.maybites.gestalt.extension.quad.*;

public class NewQuadBezierCurve extends QuadBezierCurve {
	private static final long serialVersionUID = 1L;

	private transient JoglMaterial newMaterial;
	
    public NewQuadBezierCurve() {
        material = new JoglMaterial();
        _myProducer = new NewQuadProducer();
    }
	
	public void draw(final GLContext theRenderContext) {

		if (newMaterial == null){
			newMaterial = new JoglMaterial();
		}
		if (_myAutoUpdate) {
			update();
		}

		final GL gl = ( (JoglGLContext) theRenderContext).gl;

		/* begin material */
		newMaterial.begin(theRenderContext);

		/* draw quads */
		if (_myQuadFragments != null) {
			gl.glBegin(GL.GL_QUAD_STRIP);
			for (int i = 0; i < _myQuadFragments.length; ++i) {
				gl.glNormal3f(_myQuadFragments[i].normal.x,
						_myQuadFragments[i].normal.y,
						_myQuadFragments[i].normal.z);
				gl.glColor4f(_myQuadFragments[i].colorB.r,
						_myQuadFragments[i].colorB.g,
						_myQuadFragments[i].colorB.b,
						_myQuadFragments[i].colorB.a);
				if (!material.disableTextureCoordinates) {
					gl.glTexCoord2f(_myQuadFragments[i].texcoordB.x,
							_myQuadFragments[i].texcoordB.y);
				}
				gl.glVertex3f(_myQuadFragments[i].pointB.x,
						_myQuadFragments[i].pointB.y,
						_myQuadFragments[i].pointB.z);
				gl.glColor4f(_myQuadFragments[i].colorA.r,
						_myQuadFragments[i].colorA.g,
						_myQuadFragments[i].colorA.b,
						_myQuadFragments[i].colorA.a);
				if (!material.disableTextureCoordinates) {
					gl.glTexCoord2f(_myQuadFragments[i].texcoordA.x,
							_myQuadFragments[i].texcoordA.y);
				}
				gl.glVertex3f(_myQuadFragments[i].pointA.x,
						_myQuadFragments[i].pointA.y,
						_myQuadFragments[i].pointA.z);

			}
			for (int i = (_myQuadFragments.length - 1); i >= 0; --i) {
				gl.glNormal3f(_myQuadFragments[i].normal.x * -1,
						_myQuadFragments[i].normal.y * -1,
						_myQuadFragments[i].normal.z * -1);
				gl.glColor4f(_myQuadFragments[i].colorB.r,
						_myQuadFragments[i].colorB.g,
						_myQuadFragments[i].colorB.b,
						_myQuadFragments[i].colorB.a);
				if (!material.disableTextureCoordinates) {
					gl.glTexCoord2f(_myQuadFragments[i].texcoordB.x,
							_myQuadFragments[i].texcoordB.y);
				}
				gl.glVertex3f(_myQuadFragments[i].pointB.x,
						_myQuadFragments[i].pointB.y,
						_myQuadFragments[i].pointB.z);
				gl.glColor4f(_myQuadFragments[i].colorA.r,
						_myQuadFragments[i].colorA.g,
						_myQuadFragments[i].colorA.b,
						_myQuadFragments[i].colorA.a);
				if (!material.disableTextureCoordinates) {
					gl.glTexCoord2f(_myQuadFragments[i].texcoordA.x,
							_myQuadFragments[i].texcoordA.y);
				}
				gl.glVertex3f(_myQuadFragments[i].pointA.x,
						_myQuadFragments[i].pointA.y,
						_myQuadFragments[i].pointA.z);

			}
			gl.glEnd();
		}

		/* end material */
		newMaterial.end(theRenderContext);

		/* draw children */
		drawChildren(theRenderContext);
	}
}
