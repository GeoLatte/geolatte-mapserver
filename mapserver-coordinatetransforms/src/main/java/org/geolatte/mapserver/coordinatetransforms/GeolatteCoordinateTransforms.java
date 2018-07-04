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
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.mapserver.transform.CoordinateTransforms;

/**
 * Transforms bounding boxes to Lat/Lon
 *
 * @author Karel Maesen, Geovise BVBA
 */
public class GeolatteCoordinateTransforms implements CoordinateTransforms {


    @Override
    public <Q extends Position, P extends Position> Geometry<P> transform(Geometry<Q> src, CoordinateReferenceSystem<P> targetCrs) {
        return null;
    }

    @Override
    public <Q extends Position, P extends Position> Envelope<P> transform(Envelope<Q> src, CoordinateReferenceSystem<P> targetCrs) {
        return null;
    }

    @Override
    public void transform(double[] src, double[] trgt, CoordinateReferenceSystem<?> srcCrs, CoordinateReferenceSystem<?> trgtCrs) {

    }
}
