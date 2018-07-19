package org.geolatte.mapserver.features;

import org.geolatte.mapserver.Layer;
import org.geolatte.mapserver.LayerType;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.request.GetMapRequest;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Karel Maesen, Geovise BVBA on 20/07/2018.
 */
public class FeatureLayer implements Layer, Closeable {

    private final String name;
    private final FeatureSource source;

    public FeatureLayer(String name, FeatureSource source) {
        this.name = name;
        this.source = source;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public LayerType getType() {
        return LayerType.FEATURE;
    }

    @Override
    public Image createMapImage(GetMapRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        source.close();
    }
}
