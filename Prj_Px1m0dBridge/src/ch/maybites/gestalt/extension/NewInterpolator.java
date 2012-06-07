package ch.maybites.gestalt.extension;
/*
 * Werkzeug
 *
 * Copyright (C) 2005 Patrick Kochlik + Dennis Paul
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


import java.io.Serializable;
import werkzeug.interpolation.*;


public class NewInterpolator extends Interpolator {

    private final float _myStart;

    private InterpolatorKernel _myKernel;

    public NewInterpolator(float theStart, InterpolatorKernel theKernel) {
    	super(0,0,null);
        _myStart = theStart;
        _myKernel = theKernel;
    }


    public void setKernel(InterpolatorKernel theKernel) {
        _myKernel = theKernel;
    }


    public float get(final float theDelta) {
        return _myStart + _myKernel.get(theDelta);
    }
}
