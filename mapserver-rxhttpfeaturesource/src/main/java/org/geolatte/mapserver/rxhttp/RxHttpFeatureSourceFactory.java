package org.geolatte.mapserver.rxhttp;

import com.typesafe.config.Config;
import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.features.FeatureSourceConfig;
import org.geolatte.mapserver.features.FeatureSourceFactory;

import static java.lang.String.format;

/**
 * Created by Karel Maesen, Geovise BVBA on 20/07/2018.
 */
public class RxHttpFeatureSourceFactory implements FeatureSourceFactory {

    @Override
    public Class<? extends FeatureSource> resultClass() {
        return RxHttpFeatureSource.class;
    }

    @Override
    public FeatureSource mkFeatureSource(FeatureSourceConfig config) {
        if(config instanceof RxHttpFeatureSource) {
            throw new IllegalStateException(format("Unexpected type of config: %s ", config.getClass().getCanonicalName()));
        }
        return new RxHttpFeatureSource((RxHttpFeatureSourceConfig) config);
    }

    @Override
    public Class<? extends FeatureSourceConfig> configClass() {
        return RxHttpFeatureSourceConfig.class;
    }


}
