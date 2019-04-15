package org.geolatte.mapserver.transform;

import java.util.Map;

import org.geolatte.geom.Envelope;
import org.geolatte.geom.Feature;
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

	default <ID> Feature<Q, ID> forwardFeature(Feature<P, ID> feature) {
		return new TransformFeature<Q, ID>( forward( feature.getGeometry() ), feature );
	}
}

class TransformFeature<Q extends Position, ID> implements Feature<Q, ID> {

	final private Geometry<Q> geometry;
	final private Map<String, ?> properties;
	final private ID id;

	public TransformFeature(Geometry<Q> geometry, Feature<?, ID> original) {
		this.geometry = geometry;
		properties = original.getProperties();
		id = original.getId();
	}

	@Override
	public Geometry<Q> getGeometry() {
		return this.geometry;
	}

	@Override
	public ID getId() {
		return this.getId();
	}

	@Override
	public Map<String, Object> getProperties() {
		return this.getProperties();
	}
}
