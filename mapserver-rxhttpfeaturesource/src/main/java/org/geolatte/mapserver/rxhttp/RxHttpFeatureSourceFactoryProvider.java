package org.geolatte.mapserver.rxhttp;

import org.geolatte.mapserver.features.FeatureSourceFactory;
import org.geolatte.mapserver.spi.FeatureSourceFactoryProvider;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public class RxHttpFeatureSourceFactoryProvider implements FeatureSourceFactoryProvider {

    @Override
    public FeatureSourceFactory featureSourceFactory() {
        return new RxHttpFeatureSourceFactory();
    }

}
