package org.geolatte.mapserver.test;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryCollection;
import org.geolatte.geom.Point;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.geolatte.maprenderer.shape.ShapeAdapter;
import org.geolatte.mapserver.PainterFactory;
import org.geolatte.mapserver.spi.PainterFactoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;

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

    final private static Logger logger = LoggerFactory.getLogger(TestPainter.class);

    MapGraphics mapGraphics;

    public TestPainter(MapGraphics mapGraphics) {
        this.mapGraphics = mapGraphics;
    }

    @Override
    public void paint(PlanarFeature planarFeature) {
        Geometry<C2D> geometry = planarFeature.getGeometry();
        if (geometry instanceof Point) {
            drawPoint((Point<C2D>) geometry);
        } else if (geometry instanceof GeometryCollection) {
            ((GeometryCollection<C2D, Geometry<C2D>>)geometry).forEach(geom -> {
                if (geom instanceof Point) {
                    drawPoint((Point<C2D>)geom);
                } else {
                    drawGeom(geom);
                }
            });
        } else {
            drawGeom(geometry);
        }
    }

    private void drawGeom(Geometry<C2D> geom) {
        ShapeAdapter sa = new ShapeAdapter(mapGraphics.getTransform());
        Shape[] shapes = sa.toShape(geom);
        asList(shapes).forEach(mapGraphics::draw);
    }

    private void drawPoint(Point<C2D> geometry) {
        C2D co = geometry.getPosition();
        logger.info(format("Painting %d, %d", (int)co.getX(), (int)co.getY()));
        mapGraphics.setColor(Color.BLACK);
        int size = (int)(mapGraphics.getMapUnitsPerPixel() * 5);
        mapGraphics.drawRect( (int)co.getX(), (int)co.getY(), size, size);
    }

}