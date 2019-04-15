package org.geolatte.mapserver.transform;

import org.geolatte.geom.Position;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.geom.crs.CrsRegistry;

/**
 * Created by Karel Maesen, Geovise BVBA on 12/04/2018.
 */
public interface TransformFactory {

    <P extends Position, Q extends Position> Transform<P,Q> getTransform(
            CoordinateReferenceSystem<P> source,
            CoordinateReferenceSystem<Q> target
    );

    default Transform<?,?> getTransform(CrsId source, CrsId target) {
        CoordinateReferenceSystem<?> sourceCrs = CrsRegistry.getCoordinateReferenceSystem(
                source,
                null
        );
        CoordinateReferenceSystem<?> targetCrs = CrsRegistry.getCoordinateReferenceSystem(
                target,
                null
        );
        return getTransform( sourceCrs, targetCrs );
    }



}
