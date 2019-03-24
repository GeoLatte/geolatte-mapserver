package org.geolatte.mapserver.features;

import org.geolatte.mapserver.ServiceLocator;

/**
 * Created by Karel Maesen, Geovise BVBA on 20/07/2018.
 */
public interface FeatureSourceFactory {

    Class<? extends FeatureSource> resultClass();

    void setServiceLocator(ServiceLocator serviceLocator);

    FeatureSource mkFeatureSource(FeatureSourceConfig config);

    Class<? extends FeatureSourceConfig> configClass();

}

