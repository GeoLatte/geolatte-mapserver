package org.geolatte.mapserver.spi;

import org.geolatte.mapserver.transform.CoordinateTransforms;

/**
 * Created by Karel Maesen, Geovise BVBA on 2019-03-24.
 */
public interface CoordinateTransformsProvider {

	CoordinateTransforms coordinateTranforms();

}
