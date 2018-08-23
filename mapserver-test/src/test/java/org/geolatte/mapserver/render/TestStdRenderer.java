package org.geolatte.mapserver.render;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.image.ImageFormat;
import org.geolatte.mapserver.test.FeatureSourceGenerate;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;

import static org.geolatte.geom.crs.CoordinateReferenceSystems.WEB_MERCATOR;
import static org.geolatte.mapserver.img.ImageOpsTestSupport.*;

/**
 * Created by Karel Maesen, Geovise BVBA on 03/08/2018.
 */
public class TestStdRenderer {

    private Renderer renderer;
    private Dimension dimension;
    private Envelope<C2D> bbox;

    @Before
    public void before() {
        FeatureSource fs = new FeatureSourceGenerate();
        renderer = new StdRenderer(fs, "testPainter",  1.0, ServiceLocator.defaultInstance());
        dimension = new Dimension(256, 256);
        bbox = new Envelope(0, 0, 1000, 1000, WEB_MERCATOR);
    }

    @Test
    public void testRendering() throws IOException {
        Image img = renderer.render(dimension, bbox).join();
        Image received = imageAfterIO(img, ImageFormat.PNG);
        Image expected = readTileImage("stdrenderer.png", true);
        assertImageEquals(received, expected);
    }

}

