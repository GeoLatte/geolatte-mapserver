/*
 * Copyright 2009-2010  Geovise BVBA, QMINO BVBA
 *
 * This file is part of GeoLatte Mapserver.
 *
 * GeoLatte Mapserver is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GeoLatte Mapserver is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GeoLatte Mapserver.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.geolatte.mapserver.coordinatetransforms;


import org.geolatte.geom.Envelope;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Position;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.mapserver.transform.CoordinateTransforms;
import org.geolatte.mapserver.transform.Transform;

/**
 * Transforms bounding boxes to Lat/Lon
 *
 * @author Karel Maesen, Geovise BVBA
 */
public class GeolatteCoordinateTransforms implements CoordinateTransforms {
    @Override
    public Transform getTransformOp(CrsId source, CrsId target) {
        return new GLTransform();
    }
}

class GLTransform implements Transform{

    @Override
    public <P extends Position> Geometry<?> forward(Geometry<P> src) {
        return null;
    }

    @Override
    public <P extends Position> Envelope<?> reverse(Envelope<P> src) {
        return null;
    }
}