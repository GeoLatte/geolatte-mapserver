package org.geolatte.mapserver.tilemap;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.mapserver.ServiceRegistry;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.image.Imaging;
import org.geolatte.mapserver.Layer;
import org.geolatte.mapserver.LayerType;
import org.geolatte.mapserver.request.GetMapRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;

/**
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public class TileMapLayer implements Layer {

    final private static Logger logger = LoggerFactory.getLogger(TileMapLayer.class);
    final private String name;

    final private TileMap tileMap;

    final private BoundingBoxOpFactory bboxOpFactory = new DefaultBoundingBoxOpFactory();

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
    public LayerType getType() {
        return LayerType.TILE_MAP;
    }

    @Override
    public Image createMapImage(GetMapRequest request) {
        Imaging imaging = ServiceRegistry.getInstance().imaging();
        return bboxOpFactory.create(request, tileMap, imaging).execute();
    }

    public List<Tile> getTiles(Envelope<C2D> envelope, Dimension dimension) {
        TileSetChooser tsc = new TileSetChooser(tileMap, envelope, dimension);
        int tileSet = tsc.chooseTileSet();
        logger.debug("TileSet chosen has order = " + tileSet);
        return tileMap.getTilesFor(tileSet, envelope);
    }
}
