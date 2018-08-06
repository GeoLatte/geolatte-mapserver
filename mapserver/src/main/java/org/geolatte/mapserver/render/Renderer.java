package org.geolatte.mapserver.render;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.mapserver.image.Image;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Karel Maesen, Geovise BVBA on 02/08/2018.
 */
public interface Renderer {
    CompletableFuture<Image> render(Dimension dimension, Envelope<C2D> tileBoundingBox);
}
