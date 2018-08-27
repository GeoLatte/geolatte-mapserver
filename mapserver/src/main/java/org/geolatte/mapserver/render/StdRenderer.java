package org.geolatte.mapserver.render;

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
import rx.Observable;
import rx.Subscriber;
import rx.observers.Subscribers;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

import static org.geolatte.mapserver.render.BboxFactors.upp;
import static org.geolatte.mapserver.util.EnvelopUtils.bufferRounded;

/**
 * Created by Karel Maesen, Geovise BVBA on 02/08/2018.
 */
public class StdRenderer implements Renderer {

    final private FeatureSource featureSource;
    final private PainterFactory painterFactory;
    final private String painterRef;
    final private Imaging imaging;
    private BboxFactors dynamicFactors;

    public StdRenderer(FeatureSource featureSource, String painterRef, BboxFactors dynamicFactors, ServiceLocator serviceLocator) {
        this.featureSource = featureSource;
        this.painterRef = painterRef;
        this.imaging = serviceLocator.imaging();
        this.painterFactory = serviceLocator.painterFactory();
        this.dynamicFactors = dynamicFactors;
    }

    public StdRenderer(RenderContext renderContext, ServiceLocator locator) {
        this(renderContext.getFeatureSource(), renderContext.getPainterRef(), renderContext.getBboxFactors(), locator);
    }


    public StdRenderer(RenderContext renderContext) {
        this(renderContext.getFeatureSource(), renderContext.getPainterRef(), renderContext.getBboxFactors(), ServiceLocator.defaultInstance());
    }

    @Override
    public CompletableFuture<Image> render(Dimension dimension, Envelope<C2D> tileBoundingBox) {
        MapGraphics graphics = new AWTMapGraphics(dimension, tileBoundingBox);
        Painter painter = painterFactory.mkPainter(painterRef, graphics);

        CompletableFuture<Image> promise = new CompletableFuture<>();

        if (!painter.willPaint()) {
            promise.complete(imaging.fromRenderedImage(graphics.renderImage()));
            return promise;
        }

        Observable<PlanarFeature> features = featureSource.query(queryBoundingBox(tileBoundingBox, graphics.getMapUnitsPerPixel()));

        Subscriber<PlanarFeature> featureRenderer = Subscribers.create(
                painter::paint,
                promise::completeExceptionally,
                () -> promise.complete(imaging.fromRenderedImage(graphics.renderImage()))
        );

        features.subscribe(featureRenderer);
        return promise;
    }

    private Envelope<C2D> queryBoundingBox(Envelope<C2D> tileBoundingBox, double resolution) {
        return bufferRounded(
                tileBoundingBox,
                this.dynamicFactors.getFactor(upp(resolution)));

    }
}
