package org.geolatte.mapserver.rxhttp;

import org.geolatte.mapserver.features.FeatureDeserializer;

public class GeoJsonFeatureDeserializerFactory implements FeatureDeserializerFactory {

    @Override
    public FeatureDeserializer featureDeserializer() {
        return new GeoJsonFeatureDeserializer();
    }
}
