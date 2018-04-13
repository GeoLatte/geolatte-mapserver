package org.geolatte.mapserver.config;

import org.geolatte.mapserver.core.LayerSource;
import org.geolatte.mapserver.core.LayerSourceRegistry;
import org.geolatte.mapserver.spi.LayerSourceRegistryBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public class JsonFileLayerSourceRegistryBuilder implements LayerSourceRegistryBuilder {


    @Override
    public LayerSourceRegistry build() {
        return null;
    }

    static class LayerSourceRegistryImpl implements LayerSourceRegistry {
        private final Map<String, LayerSource> registry = new ConcurrentHashMap<>();

        public void addLayerSource(LayerSource layerSource) {
            registry.put(layerSource.getName(), layerSource);
        }

        public Optional<LayerSource> getLayerSource(String name) {
            return Optional.ofNullable(registry.get(name));
        }

    }
}
