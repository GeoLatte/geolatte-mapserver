package org.geolatte.mapserver.tilemap;

import org.geolatte.mapserver.RenderableLayer;
import org.geolatte.mapserver.AsyncOperation;
import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.ows.GetMapRequest;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by Karel Maesen, Geovise BVBA on 25/07/2018.
 */
public class RenderableTileMapLayer implements  RenderableLayer {

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
    public Image createMapImage(GetMapRequest request) {
        return makeBoundingBoxOp(request).execute();
    }

    @Override
    public AsyncOperation renderLayer() {
        throw new NotImplementedException();
    }

    private BoundingBoxOp makeBoundingBoxOp(GetMapRequest request) {
        if (tileMap.getCoordinateReferenceSystem().getCrsId().equals(request.getCrs())) {
            return new DynamicBoundingBoxOp(tileMap, request.getBbox(), request.getDimension(), renderContext);
        } else {
            throw new UnsupportedOperationException("Reprojecting images is not supported ");
        }
    }

}
