package org.geolatte.mapserver.layers;

import org.geolatte.maprenderer.map.Painter;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.geolatte.mapserver.Layer;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.features.FeatureSourceFactory;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.ows.GetMapRequest;
import org.geolatte.mapserver.render.RenderContext;
import org.geolatte.mapserver.render.Renderer;
import org.geolatte.mapserver.render.StdRenderer;
import rx.Observable;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

import static org.geolatte.mapserver.util.EnvelopUtils.bufferRounded;

/**
 * Dynamic Renderable layer
 *
 * Created by Karel Maesen, Geovise BVBA on 02/08/2018.
 */
public class DynamicLayer implements Layer {

    final private String name;
    final private Renderer renderer;


    public DynamicLayer(String name, RenderContext renderContext, ServiceLocator locator) {
        this.name = name;
        this.renderer = new StdRenderer(renderContext, locator);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public CompletableFuture<Image> createMapImage(GetMapRequest request) {
        return renderer.render(request.getDimension(), request.getBbox());
    }

    //for testing
    public Renderer getRenderer(){
        return this.renderer;
    }

}
