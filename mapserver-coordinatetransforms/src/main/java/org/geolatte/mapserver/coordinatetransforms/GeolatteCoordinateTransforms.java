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


import org.geolatte.geom.Complex;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Geometries;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.LLAPositionVisitor;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.Position;
import org.geolatte.geom.PositionSequence;
import org.geolatte.geom.PositionSequenceBuilder;
import org.geolatte.geom.PositionSequenceBuilders;
import org.geolatte.geom.Simple;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.geom.crs.CrsRegistry;
import org.geolatte.geom.crs.trans.CoordinateOperation;
import org.geolatte.geom.crs.trans.CoordinateOperations;
import org.geolatte.mapserver.transform.CoordinateTransforms;
import org.geolatte.mapserver.transform.TransformOperation;

/**
 * Transforms bounding boxes to Lat/Lon
 *
 * @author Karel Maesen, Geovise BVBA
 */
public class GeolatteCoordinateTransforms implements CoordinateTransforms {
    @Override
    public TransformOperation getTransformOp(CrsId source, CrsId target) {
        return new GLTransformOp(source, target);
    }
}

class GLTransformOp implements TransformOperation {


    final private CoordinateOperation operation;
    final private CoordinateReferenceSystem<?> sourceCrs;
    final private CoordinateReferenceSystem<?> targetCrs;


    GLTransformOp(CrsId source, CrsId target) {
        operation = CoordinateOperations.transform( source, target );
        sourceCrs = CrsRegistry.getCoordinateReferenceSystem(
                source,
                null
        );
        targetCrs = CrsRegistry.getCoordinateReferenceSystem(
                target,
                null
        );

    }

    @Override
    public <P extends Position> Geometry<?> forward(Geometry<P> geom) {
        //assume geom is in the Source SRC

        if (geom instanceof Simple ) {
            return forwardSimple(geom, targetCrs);
        } else {
            Complex complex = (Complex)geom;
            Geometry[] parts = new Geometry[complex.getNumGeometries()];
            for (int i = 0; i < complex.getNumGeometries(); i++) {
                parts[i] = forward(complex.components()[i]);
            }
            return Geometries.mkGeometry( geom.getClass(), parts);
        }
    }


    @SuppressWarnings( "unchecked" )
    private <P extends Position, Q extends Position> Geometry<?> forwardSimple(Geometry<P> geom, CoordinateReferenceSystem<Q> target) {
        PositionSequence<P> positions = geom.getPositions();
        ConvertingVisitor<Q> visitor = new ConvertingVisitor<Q>(
                positions.size(),
                geom.getCoordinateDimension(),
                this.operation,
                (CoordinateReferenceSystem<Q>)targetCrs
        );

        positions.accept( visitor );
        PositionSequence<Q> outPos = visitor.build();
        return Geometries.mkGeometry( geom.getClass(), outPos, target );

    }


    @Override
    public <P extends Position> Envelope<?> reverse(Envelope<P> src) {
        Position ll = src.lowerLeft();
        Position ur = src.upperRight();

        double[] inLL = ll.toArray( null );
        double[] inUR = ur.toArray( null );
        double[] outLL = new double[2];
        double[] outUR = new double[2];

        this.operation.reverse( inLL, outLL);
        this.operation.reverse( inUR, outUR );
        return new Envelope<>( outLL[0], outLL[1], outUR[0], outUR[1], targetCrs );
    }
}


class ConvertingVisitor<P extends Position> implements LLAPositionVisitor {

    final private PositionSequenceBuilder<P> builder;
    final private double[] coordinates ;
    final private CoordinateOperation operation;

    ConvertingVisitor(int size, int coordinateDimension, CoordinateOperation op, CoordinateReferenceSystem<P> targetCrs) {
        builder = PositionSequenceBuilders.fixedSized( size, targetCrs.getPositionClass() );
        coordinates = new double[coordinateDimension];
        this.operation = op;
    }

    @Override
    public void visit(double[] inCoordinates) {
        operation.forward( inCoordinates, coordinates );
        builder.add( coordinates );
    }

    PositionSequence<P> build() {
        return builder.toPositionSequence();
    }
}