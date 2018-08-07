package org.geolatte.mapserver.rxhttp;

import org.geolatte.mapserver.features.FeatureDeserializer;

/**
 *  A factory for deserializing FeatureDeserializer
 *
 */
public interface FeatureDeserializerFactory {

    FeatureDeserializer featureDeserializer();

}
