package org.geolatte.mapserver.spi;

import org.geolatte.mapserver.features.FeatureDeserializer;

/**
 *  SPI contract for a FeatureDeserializer
 *
 */
public interface FeatureDeserializerProvider {

    FeatureDeserializer featureDeserializer();

}
