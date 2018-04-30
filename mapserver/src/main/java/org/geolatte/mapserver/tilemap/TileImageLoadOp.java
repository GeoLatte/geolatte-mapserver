package org.geolatte.mapserver.tilemap;

import org.geolatte.mapserver.spi.Imaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Karel Maesen, Geovise BVBA
 * creation-date: 7/1/11
 */
public class TileImageLoadOp implements TileMapOperation<java.util.List<TileImage>> {

    private final static Logger LOGGER = LoggerFactory.getLogger(TileImageLoadOp.class);

    private final static TileReadExecutor READ_EXECUTOR = new TileReadExecutor();

    private final List<Tile> tiles;
    private final Imaging imaging;
    private boolean forceArgb;

    TileImageLoadOp(List<Tile> tiles, Imaging imaging, boolean forceArgb) {
        this.tiles = tiles;
        this.imaging = imaging;
        this.forceArgb = forceArgb;
    }

    @Override
    public java.util.List<TileImage> execute() {
        LOGGER.debug("Start loading " + tiles.size() + " tiles.");
        List<Future<TileImage>> futures = new ArrayList<Future<TileImage>>();
        java.util.List<TileImage> results = new ArrayList<>();

        for (Tile tile : tiles) {
            TileImageReaderTask readerTask = new TileImageReaderTask(tile, imaging, forceArgb);
            Future<TileImage> future = READ_EXECUTOR.submit(readerTask);
            futures.add(future);
        }

        for (Future<TileImage> future : futures) {
            try {
                results.add(future.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getCause());
            } catch (ExecutionException e) {
                throw new RuntimeException(e.getCause());
            }
        }
        return results;
    }
}
