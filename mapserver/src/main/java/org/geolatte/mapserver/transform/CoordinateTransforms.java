package org.geolatte.mapserver.transform;

import org.geolatte.geom.crs.CrsId;

/**
 * Created by Karel Maesen, Geovise BVBA on 12/04/2018.
 */
public interface CoordinateTransforms {

    public TransformOperation getTransformOp(CrsId source, CrsId target);

}
