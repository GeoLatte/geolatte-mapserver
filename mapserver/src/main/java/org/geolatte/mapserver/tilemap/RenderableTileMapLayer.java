package org.geolatte.mapserver.tilemap;

import org.geolatte.mapserver.Layer;
import org.geolatte.mapserver.RenderableLayer;
import org.geolatte.mapserver.AsyncOperation;
import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.ows.GetMapRequest;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by Karel Maesen, Geovise BVBA on 25/07/2018.
 */
public class RenderableTileMapLayer implements Layer, RenderableLayer {

    final private TileMap tileMap;
    final private String name;
    final private FeatureSource featureSource;

    public RenderableTileMapLayer(String name, TileMap tileMap, FeatureSource featureSource) {
        this.name = name;
        this.tileMap = tileMap;
        this.featureSource = featureSource;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public Image createMapImage(GetMapRequest request) {
        throw new NotImplementedException();
    }

    @Override
    public AsyncOperation renderLayer() {
        throw new NotImplementedException();
    }
}
