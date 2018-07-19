package org.geolatte.mapserver.config;

import org.geolatte.mapserver.Layer;
import org.geolatte.mapserver.LayerRegistry;

import java.io.Closeable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
class LayerRegistryImpl implements LayerRegistry {
    private final Map<String, Layer> registry = new ConcurrentHashMap<>();

    void add(Layer layer) {
        if (layer != null)  {
            registry.put(layer.getName(), layer);
        }
    }

    public Optional<Layer> getLayer(String name) {
        return Optional.ofNullable(registry.get(name));
    }

    @Override
    public void close() throws Exception {
        for (Layer l : registry.values()) {
            if( l instanceof Closeable) {
                ((Closeable)l).close();
            }
        }
    }
}