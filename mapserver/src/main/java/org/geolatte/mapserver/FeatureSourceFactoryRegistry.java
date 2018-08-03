package org.geolatte.mapserver;

import org.geolatte.mapserver.features.FeatureSourceFactory;

import java.util.Optional;

/**
 * Created by Karel Maesen, Geovise BVBA on 20/07/2018.
 */
public interface FeatureSourceFactoryRegistry {
    Optional<FeatureSourceFactory> featureSourceFactoryForType(String canonicalName);
}
