package org.geolatte.mapserver.tilemap;

import org.geolatte.mapserver.Layer;
import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.request.GetMapRequest;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by Karel Maesen, Geovise BVBA on 25/07/2018.
 */
public class DynamicTileMapLayer implements Layer {

    final private TileMap tileMap;
    final private String name;
    final private FeatureSource featureSource;

    public DynamicTileMapLayer(String name, TileMap tileMap, FeatureSource featureSource) {
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


}
