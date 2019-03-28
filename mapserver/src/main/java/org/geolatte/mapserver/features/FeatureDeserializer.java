package org.geolatte.mapserver.features;

import org.geolatte.geom.Feature;

public interface FeatureDeserializer {

    Iterable<Feature> deserialize(String jsonString);

}
