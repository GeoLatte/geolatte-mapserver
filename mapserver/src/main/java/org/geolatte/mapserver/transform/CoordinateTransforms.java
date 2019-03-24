package org.geolatte.mapserver.transform;

import org.geolatte.geom.Envelope;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Position;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.geom.crs.trans.CoordinateOperation;

/**
 * Created by Karel Maesen, Geovise BVBA on 12/04/2018.
 */
public interface CoordinateTransforms {

    public Transform getTransformOp(CrsId source, CrsId target);

}
