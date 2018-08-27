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
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import static org.geolatte.mapserver.util.EnvelopUtils.bufferRounded;

/**
 * Created by Karel Maesen, Geovise BVBA on 02/08/2018.
 */
public class StdRenderer implements Renderer {

    private final static double STANDARD_FACTOR = 3;

    final private FeatureSource featureSource;
    final private PainterFactory painterFactory;
    final private String painterRef;
    final private Imaging imaging;
    final private double factor;
    private TreeMap<Double, Double> dynamicFactors;

    public StdRenderer(FeatureSource featureSource, String painterRef, Double factor, TreeMap<Double, Double> dynamicFactors, ServiceLocator serviceLocator) {
        this.featureSource = featureSource;
        this.painterRef = painterRef;
        this.imaging = serviceLocator.imaging();
        this.painterFactory = serviceLocator.painterFactory();
        this.factor = factor != null ? factor : STANDARD_FACTOR;
        this.dynamicFactors = dynamicFactors;
    }

    public StdRenderer(RenderContext renderContext, ServiceLocator locator, Double factor, TreeMap<Double, Double> dynamicFactors) {
        this(renderContext.getFeatureSource(), renderContext.getPainterRef(), factor, dynamicFactors, locator);
    }

    public StdRenderer(RenderContext renderContext, ServiceLocator locator) {
        this(renderContext.getFeatureSource(), renderContext.getPainterRef(), STANDARD_FACTOR, null, locator);
    }

    public StdRenderer(RenderContext renderContext){
        this(renderContext.getFeatureSource(), renderContext.getPainterRef(), STANDARD_FACTOR, null, ServiceLocator.defaultInstance());
    }

    @Override
    public CompletableFuture<Image> render(Dimension dimension, Envelope<C2D> tileBoundingBox) {
        MapGraphics graphics = new AWTMapGraphics(dimension, tileBoundingBox);
        Painter painter = painterFactory.mkPainter(painterRef, graphics);

        CompletableFuture<Image> promise = new CompletableFuture<>();

        if (!painter.willPaint()) {
            promise.complete( imaging.fromRenderedImage(graphics.renderImage()));
            return promise;
        }

        Observable<PlanarFeature> features = featureSource.query(queryBoundingBox(tileBoundingBox, graphics.getMapUnitsPerPixel()));

        Subscriber<PlanarFeature> featureRenderer = Subscribers.create(
                painter::paint,
                promise::completeExceptionally,
                () -> promise.complete( imaging.fromRenderedImage(graphics.renderImage()))
        );

        features.subscribe(featureRenderer);
        return promise;
    }

    private Envelope<C2D> queryBoundingBox(Envelope<C2D> tileBoundingBox, double resolution) {
        if (this.dynamicFactors != null) {
            return bufferRounded(
                    tileBoundingBox,
                    this.dynamicFactors.ceilingKey(resolution) != null ? this.dynamicFactors.ceilingEntry(resolution).getValue() : this.factor
            );
        } else {
            return bufferRounded(tileBoundingBox, this.factor);
        }
    }
}
