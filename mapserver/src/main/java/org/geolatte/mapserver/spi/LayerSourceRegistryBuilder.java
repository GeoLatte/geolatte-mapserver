package org.geolatte.mapserver.spi;

import org.geolatte.mapserver.core.LayerSourceRegistry;

/**
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public interface LayerSourceRegistryBuilder {

    LayerSourceRegistry build();
}
