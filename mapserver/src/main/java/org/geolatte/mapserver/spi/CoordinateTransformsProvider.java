package org.geolatte.mapserver.spi;

import org.geolatte.mapserver.transform.TransformFactory;

/**
 * Created by Karel Maesen, Geovise BVBA on 2019-03-24.
 */
public interface CoordinateTransformsProvider {

	TransformFactory coordinateTranforms();

}
