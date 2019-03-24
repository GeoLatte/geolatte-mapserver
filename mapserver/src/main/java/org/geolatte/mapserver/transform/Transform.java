package org.geolatte.mapserver.transform;

import org.geolatte.geom.Envelope;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Position;
import org.geolatte.geom.crs.CoordinateReferenceSystem;

/**
 * Created by Karel Maesen, Geovise BVBA on 2019-03-24.
 */
public interface Transform {

	public <P extends Position> Geometry<?> forward(Geometry<P> src);

	public <P extends Position> Envelope<?> reverse(Envelope<P> src);

}
