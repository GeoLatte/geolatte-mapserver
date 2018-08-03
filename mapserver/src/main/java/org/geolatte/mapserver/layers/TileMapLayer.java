package org.geolatte.mapserver.layers;

import org.geolatte.mapserver.Layer;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.ows.GetMapRequest;
import org.geolatte.mapserver.tilemap.BoundingBoxOp;
import org.geolatte.mapserver.tilemap.TileMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public class TileMapLayer implements Layer {

    final private String name;

    final private TileMap tileMap;

    public TileMapLayer(String name, TileMap tileMap) {
        this.name = name;
        this.tileMap = tileMap;
    }

    @Override
    public String getName() {
        return name;
    }

    public TileMap getTileMap(){
        return tileMap;
    }

    @Override
    public CompletableFuture<Image> createMapImage(GetMapRequest request) {
        return makeBoundingBoxOp(request).execute();
    }

    private BoundingBoxOp makeBoundingBoxOp(GetMapRequest request) {
        if (tileMap.getCoordinateReferenceSystem().getCrsId().equals(request.getCrs())) {
            return new BoundingBoxOp(tileMap, request.getBbox(), request.getDimension());
        } else {
            throw new UnsupportedOperationException("Reprojecting images is not supported ");
        }
    }
}
