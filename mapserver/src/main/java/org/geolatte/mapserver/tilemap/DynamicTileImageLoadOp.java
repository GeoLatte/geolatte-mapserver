package org.geolatte.mapserver.tilemap;

import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.image.Imaging;
import org.geolatte.mapserver.render.RenderContext;
import org.geolatte.mapserver.render.Renderer;
import org.geolatte.mapserver.render.StdRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.geolatte.mapserver.util.CompletableFutureUtil.sequence;

/**
 * Created by Karel Maesen, Geovise BVBA on 27/07/2018.
 */
public class DynamicTileImageLoadOp implements TileMapOperation<List<Image>> {

    final private static Logger logger = LoggerFactory.getLogger(DynamicTileImageLoadOp.class);

    final private List<Tile> tiles;

    final private boolean forceArgb;
    final private Imaging imaging;
    final private ExecutorService executorService;
    private final Renderer renderer;

    DynamicTileImageLoadOp(List<Tile> tiles, boolean forceArgb, RenderContext renderContext, ServiceLocator locator) {
        this.tiles = tiles;
        this.forceArgb = forceArgb;
        this.imaging = locator.imaging();
        this.executorService =  locator.executorService();
        this.renderer = new StdRenderer(renderContext, locator);
    }

    DynamicTileImageLoadOp(List<Tile> tiles, boolean forceArgb, RenderContext renderContext) {
        this(tiles, forceArgb, renderContext, ServiceLocator.defaultInstance());
    }

    @Override
    public CompletableFuture<List<Image>> execute() {

        List<CompletableFuture<Image>> futures = this.tiles.stream()
                .map(this::getImage)
                .collect(toList());
        return sequence(futures);
    }

    private CompletableFuture<Image> getImage(Tile tile) {

        return
                CompletableFuture.supplyAsync(
                        () -> tile.getImage(imaging, forceArgb), executorService
                ).thenCompose(opt -> getOrRender(opt, tile));
    }

    private CompletableFuture<Image> getOrRender(Optional<Image> opt, Tile tile) {
        return opt.map(CompletableFuture::completedFuture)
                .orElse(renderTile(tile));
    }

    private CompletableFuture<Image> renderTile(final Tile tile) {

        return renderer.render(tile.getDimension(), tile.getBoundingBox())
                .whenComplete( (img, exception ) -> {
                    if (exception == null) {
                        cache(tile, img);
                    } else {
                        logger.warn("Failure to render image", exception);
                    }
                });
    }


    private void cache(Tile tile, Image img) {
        logger.debug(format("Writing image %s", tile.toString()));
        try {
            tile.writeImage(imaging, img);
        }catch(Throwable t){
            logger.error(format("Failure to write tile %s", tile.toString()), t);
        }
    }

}
