package org.geolatte.mapserver.transform;

import org.geolatte.geom.Envelope;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Position;

/**
 * Created by Karel Maesen, Geovise BVBA on 2019-03-24.
 *
 * <P> the type of the positions in the feature source
 * <Q> the type of positions in the map projection
 */
public interface Transform<P extends Position, Q extends Position> {

	public Geometry<Q> forward(Geometry<P> src);

	public Envelope<P> reverse(Envelope<Q> src);

}
