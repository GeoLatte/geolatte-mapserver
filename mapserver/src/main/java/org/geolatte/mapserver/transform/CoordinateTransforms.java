package org.geolatte.mapserver.transform;

import org.geolatte.geom.Envelope;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Position;
import org.geolatte.geom.crs.CoordinateReferenceSystem;

/**
 * Created by Karel Maesen, Geovise BVBA on 12/04/2018.
 */
public interface CoordinateTransforms {


    public <Q extends Position, P extends Position> Geometry<P> transform(Geometry<Q> src, CoordinateReferenceSystem<P> targetCrs);

    public <Q extends Position, P extends Position> Envelope<P> transform(Envelope<Q> src, CoordinateReferenceSystem<P> targetCrs);

    public void transform(double[] src, double[] trgt, CoordinateReferenceSystem<?> srcCrs, CoordinateReferenceSystem<?> trgtCrs);

}
