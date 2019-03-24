package org.geolatte.mapserver.boot;

import org.geolatte.mapserver.FeatureSourceFactoryRegistry;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.features.FeatureSourceFactory;
import org.geolatte.mapserver.spi.FeatureSourceFactoryProvider;

import java.util.*;

/**
 * Created by Karel Maesen, Geovise BVBA on 20/07/2018.
 */
public class StdFeatureSourceFactory implements FeatureSourceFactoryRegistry {

    final private Map<String, FeatureSourceFactory> featureSourceFactories = new HashMap<>();

    public StdFeatureSourceFactory(List<FeatureSourceFactory> factories, ServiceLocator serviceLocator) {
        factories.forEach( f -> {
            f.setServiceLocator(serviceLocator);
            featureSourceFactories.put(f.resultClass().getCanonicalName(), f);
        });
    }

    @Override
    public Optional<org.geolatte.mapserver.features.FeatureSourceFactory> featureSourceFactoryForType(String canonicalName) {
        return Optional.ofNullable(featureSourceFactories.get(canonicalName));
    }

}
