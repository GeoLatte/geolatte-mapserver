package org.geolatte.mapserver.tilemap;

import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.image.Imaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.geolatte.mapserver.util.CompletableFutureUtil.sequence;

/**
 * @author Karel Maesen, Geovise BVBA
 * creation-date: 7/1/11
 */
public class TileImageLoadOp implements TileMapOperation<List<Image>> {

    private final static Logger LOGGER = LoggerFactory.getLogger(TileImageLoadOp.class);

    private final List<Tile> tiles;
    private final Imaging imaging;
    private boolean forceArgb;

    TileImageLoadOp(List<Tile> tiles, Imaging imaging, boolean forceArgb) {
        this.tiles = tiles;
        this.imaging = imaging;
        this.forceArgb = forceArgb;
    }

    @Override
    public CompletableFuture<List<Image>> execute() {
        LOGGER.debug("Start loading " + tiles.size() + " tiles.");
        List<CompletableFuture<Image>> futures = new ArrayList<>();
        java.util.List<Image> results = new ArrayList<>();
        for (Tile tile : tiles) {

            CompletableFuture<Image> future =
                    CompletableFuture
                            .supplyAsync(() -> tile.getImage(imaging, forceArgb))
                            .thenApply(Optional::get); //TODO -- this Optional::get() is temporary and will need to disappear
            futures.add(future);
        }

        return sequence(futures);
    }

}
