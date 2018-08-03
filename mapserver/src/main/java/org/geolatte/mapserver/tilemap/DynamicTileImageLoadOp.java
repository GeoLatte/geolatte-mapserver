package org.geolatte.mapserver.tilemap;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.maprenderer.java2D.AWTMapGraphics;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.geolatte.mapserver.PainterFactory;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.image.Imaging;
import org.geolatte.mapserver.layers.RenderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;
import rx.observers.Subscribers;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.geolatte.mapserver.util.CompletableFutureUtil.sequence;
import static org.geolatte.mapserver.util.EnvelopUtils.bufferRounded;

/**
 * Created by Karel Maesen, Geovise BVBA on 27/07/2018.
 */
public class DynamicTileImageLoadOp implements TileMapOperation<List<Image>> {

    final private static Logger logger = LoggerFactory.getLogger(DynamicTileImageLoadOp.class);

    final private List<Tile> tiles;
    final private FeatureSource featureSource;
    final private boolean forceArgb;
    final private Imaging imaging;
    final private ExecutorService executorService;
    final private PainterFactory painterFactory;
    final private String painterRef;

    DynamicTileImageLoadOp(List<Tile> tiles, boolean forceArgb, RenderContext renderContext, ServiceLocator locator) {
        this.tiles = tiles;
        this.forceArgb = forceArgb;
        this.featureSource = renderContext.getFeatureSource();
        this.imaging = locator.imaging();
        this.executorService =  locator.executorService();
        this.painterFactory =  locator.painterFactory();
        this.painterRef =  renderContext.getPainterRef();
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

    private CompletableFuture<Image> renderTile(Tile tile) {

        MapGraphics graphics = new AWTMapGraphics(tile.getDimension(), tile.getBoundingBox());
        Painter painter = painterFactory.mkPainter(painterRef, graphics);

        CompletableFuture<Image> promise = new CompletableFuture<>();

        if (!painter.willPaint()) {
            return writeAndComplete(tile, graphics, promise);
        }

        Observable<PlanarFeature> features = featureSource.query(mkQueryBoundingBox(tile));

        Subscriber<PlanarFeature> featureRenderer = Subscribers.create(
                painter::paint,
                promise::completeExceptionally,
                () -> writeAndComplete(tile, graphics, promise)
        );
        features.subscribe(featureRenderer);
        return promise;

    }

    private CompletableFuture<Image> writeAndComplete(Tile tile, MapGraphics graphics, CompletableFuture<Image> promise) {
        Image img = imaging.fromRenderedImage(graphics.renderImage());
        writeImageToTile(tile, img);
        completePromise(img, promise);
        return promise;
    }

    private void writeImageToTile(Tile tile, Image img) {
        logger.debug(format("Writing image %s", tile.toString()));
        try {
            tile.writeImage(imaging, img);
        }catch(Throwable t){
            logger.error(format("Failure to write tile %s", tile.toString()), t);
        }
    }

    private void completePromise(Image image, CompletableFuture<Image> promise) {
        promise.complete(image);
    }

    private Envelope<C2D> mkQueryBoundingBox(Tile tile) {
        //todo make this configurable
        return bufferRounded(tile.getBoundingBox(), 3);
    }

}
