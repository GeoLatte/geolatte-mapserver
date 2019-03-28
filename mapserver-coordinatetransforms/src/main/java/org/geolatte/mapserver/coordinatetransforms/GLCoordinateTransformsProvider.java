package org.geolatte.mapserver.coordinatetransforms;

import org.geolatte.mapserver.spi.CoordinateTransformsProvider;
import org.geolatte.mapserver.transform.TransformFactory;

/**
 * Created by Karel Maesen, Geovise BVBA on 2019-03-24.
 */
public class GLCoordinateTransformsProvider implements CoordinateTransformsProvider {

	@Override
	public TransformFactory coordinateTranforms() {
		return new GeolatteTransformFactory();
	}
}
