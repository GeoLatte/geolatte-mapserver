package org.geolatte.mapserver.spi;

import org.geolatte.mapserver.FeatureSourceFactoryRegistry;
import org.geolatte.mapserver.LayerRegistry;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.features.FeatureSourceFactory;

/**
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public interface LayerRegistryProvider {

    LayerRegistry layerRegistry(FeatureSourceFactoryRegistry fsFactoryRegistry, ServiceLocator locator);
}
