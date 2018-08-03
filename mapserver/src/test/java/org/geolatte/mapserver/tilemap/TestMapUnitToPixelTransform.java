/*
 * Copyright 2009-2010  Geovise BVBA, QMINO BVBA
 *
 * This file is part of GeoLatte Mapserver.
 *
 * GeoLatte Mapserver is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GeoLatte Mapserver is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GeoLatte Mapserver.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.geolatte.mapserver.tilemap;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Point;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.mapserver.util.Pixel;
import org.geolatte.mapserver.util.PixelRange;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.PROJECTED_2D_METER;
import static org.junit.Assert.assertTrue;

/**
 * @author Karel Maesen, Geovise BVBA
 * creation-date: Jul 8, 2010
 */
public class TestMapUnitToPixelTransform {


    private CoordinateReferenceSystem<C2D> crs = PROJECTED_2D_METER;

    private Envelope<C2D> bbox = new Envelope<>(10, 20, 110, 100, crs);
    private PixelRange pixelRange = new PixelRange(100, 50, 100, 200);
    private double mapUnitsPerPixel = 10.0;

    private MapUnitToPixelTransform pixRangeBasedTransform = new MapUnitToPixelTransform(bbox, pixelRange);
    private MapUnitToPixelTransform uppBasedTransform = new MapUnitToPixelTransform(bbox, 100, 200, 0.1);

    @Test
    public void test_pixel_outside_range_is_allowed() {
        Point<C2D> received = pixRangeBasedTransform.toPoint(Pixel.valueOf(0, 0));
        Point<C2D> expected = new Point<>(new C2D(-90, 120), crs);
        assertEquals(expected, received);

    }

    @Test
    public void test_pixel_to_point_on_pixrange_based_transform() {

        Point<C2D> pnt = pixRangeBasedTransform.toPoint(Pixel.valueOf(100, 50));
        assertTrue(equalsWithinTolerance(point(crs, c(10, 100)), pnt, Math.ulp(100d)));

        pnt = pixRangeBasedTransform.toPoint(Pixel.valueOf(100, 249));
        assertTrue(equalsWithinTolerance(point(crs, c(10, 20.4)), pnt, Math.ulp(250d)));

        pnt = pixRangeBasedTransform.toPoint(Pixel.valueOf(199, 249));
        assertTrue(equalsWithinTolerance(point(crs,c(109, 20.4)), pnt, Math.ulp(250d)));

        pnt = pixRangeBasedTransform.toPoint(Pixel.valueOf(199, 50));
        assertTrue(equalsWithinTolerance(point(crs, c(109, 100)), pnt, Math.ulp(200d)));

        pnt = pixRangeBasedTransform.toPoint(Pixel.valueOf(150, 150));
        assertTrue(equalsWithinTolerance(point(crs,c(60, 60)), pnt, Math.ulp(150d)));

    }

    @Test
    public void test_pixel_to_point_on_upp_based_transform() {

        Point<C2D> pnt = uppBasedTransform.toPoint(Pixel.valueOf(100, 200));
        assertTrue(equalsWithinTolerance(point(crs,c(10, 100)), pnt, Math.ulp(100d)));

        pnt = uppBasedTransform.toPoint(Pixel.valueOf(100, 999));
        assertTrue(equalsWithinTolerance(point(crs, c(10, 20.1)), pnt, Math.ulp(20d)));

        pnt = uppBasedTransform.toPoint(Pixel.valueOf(1099, 999));
        assertTrue(equalsWithinTolerance(point(crs, c(109.9, 20.1)), pnt, Math.ulp(100d)));

        pnt = uppBasedTransform.toPoint(Pixel.valueOf(1099, 200));
        assertTrue(equalsWithinTolerance(point(crs, c(109.9, 100)), pnt, Math.ulp(100d)));

        pnt = uppBasedTransform.toPoint(Pixel.valueOf(600, 600));
        assertTrue(equalsWithinTolerance(point(crs, c(60, 60)), pnt, Math.ulp(60d)));

    }

    @Test
    public void test_point_outside_extent_is_allowed() {

        Pixel received = pixRangeBasedTransform.toPixel(new C2D(0, 2));
        Pixel expected = Pixel.valueOf(90, 295);
        assertEquals(expected, received);
    }

    @Test
    public void test_point_to_pixel_on_pixrange_based_transform() {
        Pixel pixel = pixRangeBasedTransform.toPixel( new C2D(10, 20));
        assertEquals(Pixel.valueOf(100, 249), pixel);

        pixel = pixRangeBasedTransform.toPixel(new C2D(10, 100));
        assertEquals(Pixel.valueOf(100, 50), pixel);

        pixel = pixRangeBasedTransform.toPixel(new C2D(110, 100));
        assertEquals(Pixel.valueOf(199, 50), pixel);

        pixel = pixRangeBasedTransform.toPixel(new C2D(110, 20));
        assertEquals(Pixel.valueOf(199, 249), pixel);

        pixel = pixRangeBasedTransform.toPixel(new C2D(60, 60));
        assertEquals(Pixel.valueOf(150, 150), pixel);

    }

    @Test
    public void test_point_pixel_on_upp_based_transform() {
        Pixel pixel = uppBasedTransform.toPixel(new C2D(10, 100));
        assertEquals(Pixel.valueOf(100, 200), pixel);

        pixel = uppBasedTransform.toPixel(new C2D(10, 20));
        assertEquals(Pixel.valueOf(100, 999), pixel);

        pixel = uppBasedTransform.toPixel(new C2D(110, 20));
        assertEquals(Pixel.valueOf(1099, 999), pixel);

        pixel = uppBasedTransform.toPixel(new C2D(110, 100));
        assertEquals(Pixel.valueOf(1099, 200), pixel);

        pixel = uppBasedTransform.toPixel(new C2D(60, 60));
        assertEquals(Pixel.valueOf(600, 600), pixel);

        //the next few cases test whether coordinates map properly to the correct pixel
        pixel = uppBasedTransform.toPixel(new C2D(60.1, 60));
        assertEquals(Pixel.valueOf(601, 600), pixel);

        pixel = uppBasedTransform.toPixel(new C2D(60.05, 60));
        assertEquals(Pixel.valueOf(600, 600), pixel);

        pixel = uppBasedTransform.toPixel(new C2D(60, 60.1));
        assertEquals(Pixel.valueOf(600, 599), pixel);

        pixel = uppBasedTransform.toPixel(new C2D(60, 60.05));
        assertEquals(Pixel.valueOf(600, 599), pixel);


    }

    @Test
    public void test_point_pixel_on_upp_based_transform_rounding_errors() {
        double originXinMapUnits = -20037508.34;
        double originYinMapUnits = -20037508.34;
        double unitsPerPixel = 4.777314266967774;
        Envelope<C2D> boundingBoxInMapUnits = new Envelope<>(originXinMapUnits, originYinMapUnits, -originXinMapUnits, -originYinMapUnits, crs);
        MapUnitToPixelTransform uppBasedTransform = new MapUnitToPixelTransform(boundingBoxInMapUnits, unitsPerPixel);
        int tileNumberX = 16811;
        int tileNumberY = 21781;
        int tileDimensionInPixels = 256;
        double tileWidthInMapUnits = tileDimensionInPixels * unitsPerPixel;
        double tileHeightInMapUnits = tileWidthInMapUnits;
        double tileMinXinMapUnits = tileNumberX * tileWidthInMapUnits + originXinMapUnits;
        double tileMinYinMapUnits = tileNumberY * tileHeightInMapUnits + originYinMapUnits;
        Envelope<C2D> tileBoundingBoxInMapUnits = new Envelope<>(tileMinXinMapUnits, tileMinYinMapUnits, tileMinXinMapUnits + tileWidthInMapUnits, tileMinYinMapUnits + tileHeightInMapUnits, crs);
        PixelRange tilePixelRange = uppBasedTransform.toPixelRange(tileBoundingBoxInMapUnits);
        assertEquals(tileDimensionInPixels, tilePixelRange.getWidth());
        assertEquals(tileDimensionInPixels, tilePixelRange.getHeight());
    }

    @Test
    public void test_bbox_to_pixrange_on_pixrange_based_transform() {
        Envelope<C2D> bbox = new Envelope<>(-180, -90, 180, 90, crs);
        PixelRange pixRange = new PixelRange(0, 0, 512, 256);
        MapUnitToPixelTransform mutpTransform = new MapUnitToPixelTransform(bbox, pixRange);
        Envelope<C2D> testBBox = new Envelope<>(-170, -80, 170, 80, crs);
        PixelRange result = mutpTransform.toPixelRange(testBBox);
        PixelRange expected = new PixelRange(14, 14, 512 - 28, 256 - 28);
        assertEquals(expected, result);

        testBBox = new Envelope<>(-180, -90, 180, 90, crs);
        result = mutpTransform.toPixelRange(testBBox);
        expected = new PixelRange(0, 0, 512, 256);
        assertEquals(expected, result);

        mutpTransform = new MapUnitToPixelTransform(new Envelope<>(0, 0, 90, 90,crs), new PixelRange(0, 0, 512, 256));
        testBBox = new Envelope<>(0, 0, 90, 90, crs);
        result = mutpTransform.toPixelRange(testBBox);
        expected = new PixelRange(0, 0, 512, 256);
        assertEquals(expected, result);
    }

    //TODO -- this should be added to geometry interface
    public boolean equalsWithinTolerance(Point<C2D> p1, Point<C2D> p2, double tolerance) {
        if (tolerance < 0) tolerance = -tolerance;
        C2D c1 = p1.getPosition();
        C2D c2 = p2.getPosition();
        return Math.abs(c1.getX() - c2.getX()) < tolerance &&
                Math.abs(c2.getY() - c2.getY()) < tolerance;
    }

}
