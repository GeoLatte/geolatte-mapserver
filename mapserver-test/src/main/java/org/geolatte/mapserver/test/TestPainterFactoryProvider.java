package org.geolatte.mapserver.test;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.geolatte.mapserver.PainterFactory;
import org.geolatte.mapserver.spi.PainterFactoryProvider;

/**
 * Created by Karel Maesen, Geovise BVBA on 27/07/2018.
 */
public class TestPainterFactoryProvider implements PainterFactoryProvider {

    @Override
    public PainterFactory painterFactory() {

        return new PainterFactory() {
            @Override
            public boolean canCreate(String ref) {
                return "testPainter".equals(ref);
            }

            @Override
            public Painter mkPainter(String ref, MapGraphics mapGraphics) {
                return new TestPainter(mapGraphics) ;
            }
        };
    }
}

class TestPainter implements Painter {

    MapGraphics mapGraphics;

    public TestPainter(MapGraphics mapGraphics) {
        this.mapGraphics = mapGraphics;
    }


    @Override
    public void paint(PlanarFeature planarFeature) {

    }

    @Override
    public void paint(Iterable<PlanarFeature> features) {

    }

    @Override
    public Envelope<C2D> envelope(PlanarFeature f) {
        return null;
    }
}