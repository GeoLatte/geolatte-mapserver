package org.geolatte.mapserver.spi;

import org.geolatte.mapserver.FeatureSourceFactoryRegistry;
import org.geolatte.mapserver.LayerRegistry;

/**
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public interface LayerRegistryProvider {

    LayerRegistry layerSourceRegistry(FeatureSourceFactoryRegistry featureSourceFactoryRegistry);
}
