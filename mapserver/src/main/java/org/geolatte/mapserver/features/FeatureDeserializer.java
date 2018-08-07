package org.geolatte.mapserver.features;

import org.geolatte.maprenderer.map.PlanarFeature;

public interface FeatureDeserializer {

    Iterable<PlanarFeature> deserialize(String jsonString);

}
