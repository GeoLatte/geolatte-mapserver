package org.geolatte.mapserver.render;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.maprenderer.java2D.AWTMapGraphics;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.geolatte.mapserver.Instrumentation;
import org.geolatte.mapserver.PainterFactory;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.instrumentation.Timer;
import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.image.Imaging;
import org.geolatte.mapserver.instrumentation.StopOnceTimerWrapper;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Actions;
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
    final private Instrumentation instrumentation;
    private BboxFactors dynamicFactors;

    public StdRenderer(FeatureSource featureSource, String painterRef, BboxFactors dynamicFactors, ServiceLocator serviceLocator) {
        this.featureSource = featureSource;
        this.painterRef = painterRef;
        this.imaging = serviceLocator.imaging();
        this.painterFactory = serviceLocator.painterFactory();
        this.instrumentation = serviceLocator.instrumentation();
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

        instrumentCreateMapOp(graphics.getMapUnitsPerPixel(), promise);

        Observable<PlanarFeature> features = featureSource.query(queryBoundingBox(tileBoundingBox, graphics.getMapUnitsPerPixel()));
        Observable<PlanarFeature> share = features.share();

        share.subscribe(createRenderingSubscriber(graphics, painter, promise));
        share.take(1).subscribe(createInstrumentationSubscriber(graphics));

        return promise;
    }

    private Subscriber<PlanarFeature> createRenderingSubscriber(MapGraphics graphics, Painter painter, CompletableFuture<Image> promise) {
        return Subscribers.create(
                    painter::paint,
                    promise::completeExceptionally,
                    () -> promise.complete(imaging.fromRenderedImage(graphics.renderImage()))
            );
    }

    private Subscriber<PlanarFeature> createInstrumentationSubscriber(MapGraphics graphics) {
        StopOnceTimerWrapper featuresTimer = new StopOnceTimerWrapper(instrumentation.getLoadFeaturesTimer(graphics.getMapUnitsPerPixel()));
        return Subscribers.create(
                (feature) -> featuresTimer.stopOnce(), //stop timer when the first feature arrives
                Actions.empty(),
                featuresTimer::stopOnce //stop here as well because timer will not have been stopped in onNext when there were no features
        );
    }

    private void instrumentCreateMapOp(double upp, CompletableFuture<Image> promise) {
        Timer mapImageTimer = instrumentation.getCreateMapImageTimer(upp);
        promise.whenComplete((image, throwable) -> {
            if (image != null) {
                mapImageTimer.stop();
            }
        });
    }

    private Envelope<C2D> queryBoundingBox(Envelope<C2D> tileBoundingBox, double resolution) {
        return bufferRounded(
                tileBoundingBox,
                this.dynamicFactors.getFactor(upp(resolution)));

    }
}
