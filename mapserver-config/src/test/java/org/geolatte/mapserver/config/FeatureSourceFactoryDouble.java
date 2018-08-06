package org.geolatte.mapserver.config;

import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.features.FeatureSourceConfig;
import org.geolatte.mapserver.features.FeatureSourceFactory;
import org.geolatte.mapserver.spi.FeatureSourceFactoryProvider;

/**
 * Created by Karel Maesen, Geovise BVBA on 02/08/2018.
 */
public class FeatureSourceFactoryDouble implements FeatureSourceFactory, FeatureSourceFactoryProvider {

    @Override
    public Class<? extends FeatureSource> resultClass() {
        return FeatureSourceDouble.class;
    }

    @Override
    public FeatureSource mkFeatureSource(FeatureSourceConfig config) {
        return new FeatureSourceDouble();
    }

    @Override
    public Class<? extends FeatureSourceConfig> configClass() {
        return FeatureSourceConfigDouble.class;
    }

    @Override
    public FeatureSourceFactory featureSourceFactory() {
        return new FeatureSourceFactoryDouble();
    }
}
