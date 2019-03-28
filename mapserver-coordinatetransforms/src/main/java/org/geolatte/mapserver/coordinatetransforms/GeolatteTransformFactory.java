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
import org.geolatte.geom.crs.trans.EnvelopeTransform;
import org.geolatte.geom.crs.trans.TransformOperation;
import org.geolatte.geom.crs.trans.TransformOperations;
import org.geolatte.geom.crs.trans.TransformVisitor;
import org.geolatte.mapserver.transform.TransformFactory;
import org.geolatte.mapserver.transform.Transform;

/**
 * Transforms bounding boxes to Lat/Lon
 *
 * @author Karel Maesen, Geovise BVBA
 */
public class GeolatteTransformFactory implements TransformFactory {

	public <P extends Position, Q extends Position> Transform<P,Q> getTransform(
			CoordinateReferenceSystem<P> source,
			CoordinateReferenceSystem<Q> target) {
		return new GLTransformOp<P,Q>(source, target);
	}


}

class GLTransformOp<P extends Position, Q extends Position> implements Transform<P,Q> {


    final private TransformOperation<P,Q> operation;
    final private TransformVisitor<P, Q> visitor;
	final private EnvelopeTransform<P,Q> envTransform;

    GLTransformOp(CoordinateReferenceSystem<P> sourceCrs, CoordinateReferenceSystem<Q> targetCrs) {
		this.operation = TransformOperations.from( sourceCrs, targetCrs );
		visitor = new TransformVisitor<>( operation );
		envTransform = new EnvelopeTransform<>(operation);
    }


	@Override
	public Geometry<Q> forward(Geometry<P> src) {
    	visitor.reset();
		src.accept( visitor );
		return visitor.getTransformed();
	}

	@Override
	public Envelope<P> reverse(Envelope<Q> src) {
		return envTransform.reverse( src );
	}
}


