package org.geolatte.mapserver.layers;

import org.geolatte.mapserver.Layer;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.ows.GetMapRequest;
import org.geolatte.mapserver.tilemap.BoundingBoxOp;
import org.geolatte.mapserver.tilemap.DynamicBoundingBoxOp;
import org.geolatte.mapserver.tilemap.TileMap;

import java.util.concurrent.CompletableFuture;


/**
 * Created by Karel Maesen, Geovise BVBA on 25/07/2018.
 */
public class RenderableTileMapLayer implements Layer {

    final private TileMap tileMap;
    final private String name;
    final private RenderContext renderContext;

    public RenderableTileMapLayer(String name, TileMap tileMap, RenderContext renderContext) {
        this.name = name;
        this.tileMap = tileMap;
        this.renderContext = renderContext;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CompletableFuture<Image> createMapImage(GetMapRequest request) {
        return makeBoundingBoxOp(request).execute();
    }

    private BoundingBoxOp makeBoundingBoxOp(GetMapRequest request) {
        if (tileMap.getCoordinateReferenceSystem().getCrsId().equals(request.getCrs())) {
            return new DynamicBoundingBoxOp(tileMap, request.getBbox(), request.getDimension(), renderContext);
        } else {
            throw new UnsupportedOperationException("Reprojecting images is not supported ");
        }
    }

}
