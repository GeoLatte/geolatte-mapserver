package org.geolatte.mapserver.boot;

import org.geolatte.mapserver.FeatureSourceFactoryRegistry;
import org.geolatte.mapserver.features.FeatureSourceFactory;
import org.geolatte.mapserver.spi.FeatureSourceFactoryProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * Created by Karel Maesen, Geovise BVBA on 20/07/2018.
 */
public class FeatureSourceFactoryImpl implements FeatureSourceFactoryRegistry {

    final private Map<String, org.geolatte.mapserver.features.FeatureSourceFactory> featureSourceFactories;

    public FeatureSourceFactoryImpl() {
        featureSourceFactories = loadAllFeatureSourceFactoryProviders();
    }

    //TODO -- this duplication to BootServiceLocate#loaddAll -- refactor
    private Map<String, org.geolatte.mapserver.features.FeatureSourceFactory> loadAllFeatureSourceFactoryProviders() {
        Map<String, org.geolatte.mapserver.features.FeatureSourceFactory> result = new HashMap<>();
        ServiceLoader<FeatureSourceFactoryProvider> loader = ServiceLoader.load(FeatureSourceFactoryProvider.class);
        for (FeatureSourceFactoryProvider provider : loader) {
            org.geolatte.mapserver.features.FeatureSourceFactory factory = provider.featureSourceFactory();
            result.put(factory.resultClass().getCanonicalName(), factory);
        }
        return result;
    }

    @Override
    public Optional<org.geolatte.mapserver.features.FeatureSourceFactory> featureSourceFactoryForType(String canonicalName) {
        return Optional.ofNullable(featureSourceFactories.get(canonicalName));
    }

}
