package org.geolatte.mapserver.rxhttp;

import org.geolatte.mapserver.features.FeatureDeserializer;
import org.geolatte.mapserver.spi.FeatureDeserializerProvider;

public class GeoJsonFeatureDeserializerProvider implements FeatureDeserializerProvider {

    @Override
    public FeatureDeserializer featureDeserializer() {
        return new GeoJsonFeatureDeserializer();
    }
}
