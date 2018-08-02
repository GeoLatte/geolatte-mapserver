package org.geolatte.mapserver.tilemap;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.render.RenderContext;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Karel Maesen, Geovise BVBA on 27/07/2018.
 */
public class DynamicBoundingBoxOp extends BoundingBoxOp {

    final private DynamicTileImageLoadOp loadOp;

    public DynamicBoundingBoxOp(TileMap tileMap, Envelope<C2D> bbox,
                                Dimension dimension,
                                RenderContext renderContext
    ) {
        super(tileMap, bbox, dimension);
        loadOp = new DynamicTileImageLoadOp(getTiles(), getIsForceArgb(), renderContext);
    }

    @Override
    protected CompletableFuture<List<Image>> getTileImages() {
        return loadOp.execute();
    }

}
