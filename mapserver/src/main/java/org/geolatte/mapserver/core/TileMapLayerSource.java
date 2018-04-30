package org.geolatte.mapserver.core;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.mapserver.tilemap.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;

/**
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public class TileMapLayerSource implements LayerSource {

    final private static Logger logger = LoggerFactory.getLogger(TileMapLayerSource.class);
    final private String name;

    final private TileImageSourceFactory tileImageSourceFactory;

    final private TileMap tileMap;

    public TileMapLayerSource(String name, TileImageSourceFactory tileImageSourceFactory, TileMap tileMap) {
        this.name = name;
        this.tileImageSourceFactory = tileImageSourceFactory;
        this.tileMap = tileMap;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LayerSourceType getType() {
        return LayerSourceType.TILE_MAP;
    }


    public List<Tile> getTiles(Envelope<C2D> envelope, Dimension dimension) {
        TileSetChooser tsc = new TileSetChooser(tileMap, envelope, dimension);
        TileSet tileSet = tsc.chooseTileSet();
        logger.debug("TileSet chosen has order = " + tileSet.getOrder());
        return tileMap.getTilesFor(tileSet, envelope);
    }
}
