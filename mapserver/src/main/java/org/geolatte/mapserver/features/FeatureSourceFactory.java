package org.geolatte.mapserver.features;

/**
 * Created by Karel Maesen, Geovise BVBA on 20/07/2018.
 */
public interface FeatureSourceFactory {

    Class<? extends FeatureSource> resultClass();

    FeatureSource mkFeatureSource(FeatureSourceConfig config);

    Class<? extends FeatureSourceConfig> configClass();

}

