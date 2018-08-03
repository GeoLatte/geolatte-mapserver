package org.geolatte.mapserver.spi;

import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.features.FeatureSourceFactory;

/**
 *  SPI contract for strategy to provide a FeatureSourceFactory
 *
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public interface FeatureSourceFactoryProvider {

    FeatureSourceFactory featureSourceFactory();



}
