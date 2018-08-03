package org.geolatte.mapserver;

import java.util.Optional;

/**
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public interface LayerRegistry extends AutoCloseable {

    public Optional<Layer> getLayer(String name);

}
