package org.geolatte.mapserver.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public interface LayerSourceRegistry {

    public void addLayerSource(LayerSource layerSource);

    public Optional<LayerSource> getLayerSource(String name);

}
