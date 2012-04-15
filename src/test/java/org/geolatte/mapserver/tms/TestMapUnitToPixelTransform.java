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

package org.geolatte.mapserver.tms;

import org.geolatte.geom.Envelope;
import org.geolatte.geom.Point;
import org.geolatte.geom.Points;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.mapserver.util.Pixel;
import org.geolatte.mapserver.util.PixelRange;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: Jul 8, 2010
 */
public class TestMapUnitToPixelTransform {

    Envelope bbox = new Envelope(10, 20, 110, 100);
    PixelRange pixelRange = new PixelRange(100, 50, 100, 200);
    double mapUnitsPerPixel = 10.0;

    MapUnitToPixelTransform pixRangeBasedTransform = new MapUnitToPixelTransform(bbox, pixelRange);
    MapUnitToPixelTransform uppBasedTransform = new MapUnitToPixelTransform(bbox, 100, 200, 0.1);

    @Test
    public void test_pixel_outside_range_is_allowed() {
        Point received = pixRangeBasedTransform.toPoint(Pixel.valueOf(0, 0));
        Point expected = Points.create(-90, 120, CrsId.UNDEFINED);
        assertEquals(expected, received);

    }

    @Test
    public void test_pixel_to_point_on_pixrange_based_transform() {

        Point pnt = pixRangeBasedTransform.toPoint(Pixel.valueOf(100, 50));
        assertTrue(equalsWithinTolerance(Points.create(10, 100, CrsId.UNDEFINED), pnt, Math.ulp(100d)));

        pnt = pixRangeBasedTransform.toPoint(Pixel.valueOf(100, 249));
        assertTrue(equalsWithinTolerance(Points.create(10, 20.4, CrsId.UNDEFINED), pnt, Math.ulp(250d)));

        pnt = pixRangeBasedTransform.toPoint(Pixel.valueOf(199, 249));
        assertTrue(equalsWithinTolerance(Points.create(109, 20.4, CrsId.UNDEFINED), pnt, Math.ulp(250d)));

        pnt = pixRangeBasedTransform.toPoint(Pixel.valueOf(199, 50));
        assertTrue(equalsWithinTolerance(Points.create(109, 100, CrsId.UNDEFINED), pnt, Math.ulp(200d)));

        pnt = pixRangeBasedTransform.toPoint(Pixel.valueOf(150, 150));
        assertTrue(equalsWithinTolerance(Points.create(60, 60, CrsId.UNDEFINED), pnt, Math.ulp(150d)));

    }

    @Test
    public void test_pixel_to_point_on_upp_based_transform() {

        Point pnt = uppBasedTransform.toPoint(Pixel.valueOf(100, 200));
        assertTrue(equalsWithinTolerance(Points.create(10, 100, CrsId.UNDEFINED), pnt, Math.ulp(100d)));

        pnt = uppBasedTransform.toPoint(Pixel.valueOf(100, 999));
        assertTrue(equalsWithinTolerance(Points.create(10, 20.1, CrsId.UNDEFINED), pnt, Math.ulp(20d)));

        pnt = uppBasedTransform.toPoint(Pixel.valueOf(1099, 999));
        assertTrue(equalsWithinTolerance(Points.create(109.9, 20.1, CrsId.UNDEFINED), pnt, Math.ulp(100d)));

        pnt = uppBasedTransform.toPoint(Pixel.valueOf(1099, 200));
        assertTrue(equalsWithinTolerance(Points.create(109.9, 100, CrsId.UNDEFINED), pnt, Math.ulp(100d)));

        pnt = uppBasedTransform.toPoint(Pixel.valueOf(600, 600));
        assertTrue(equalsWithinTolerance(Points.create(60, 60, CrsId.UNDEFINED), pnt, Math.ulp(60d)));

    }

    @Test
    public void test_point_outside_extent_is_allowed() {

        Pixel received = pixRangeBasedTransform.toPixel(Points.create(0, 2, CrsId.UNDEFINED));
        Pixel expected = Pixel.valueOf(90, 295);
        assertEquals(expected, received);
    }

    @Test
    public void test_point_to_pixel_on_pixrange_based_transform() {
        Pixel pixel = pixRangeBasedTransform.toPixel(Points.create(10, 20, CrsId.UNDEFINED));
        assertEquals(Pixel.valueOf(100, 249), pixel);

        pixel = pixRangeBasedTransform.toPixel(Points.create(10, 100, CrsId.UNDEFINED));
        assertEquals(Pixel.valueOf(100, 50), pixel);

        pixel = pixRangeBasedTransform.toPixel(Points.create(110, 100, CrsId.UNDEFINED));
        assertEquals(Pixel.valueOf(199, 50), pixel);

        pixel = pixRangeBasedTransform.toPixel(Points.create(110, 20, CrsId.UNDEFINED));
        assertEquals(Pixel.valueOf(199, 249), pixel);

        pixel = pixRangeBasedTransform.toPixel(Points.create(60, 60, CrsId.UNDEFINED));
        assertEquals(Pixel.valueOf(150, 150), pixel);

    }

    @Test
    public void test_point_pixel_on_upp_based_transform() {
        Pixel pixel = uppBasedTransform.toPixel(Points.create(10, 100, CrsId.UNDEFINED));
        assertEquals(Pixel.valueOf(100, 200), pixel);

        pixel = uppBasedTransform.toPixel(Points.create(10, 20, CrsId.UNDEFINED));
        assertEquals(Pixel.valueOf(100, 999), pixel);

        pixel = uppBasedTransform.toPixel(Points.create(110, 20, CrsId.UNDEFINED));
        assertEquals(Pixel.valueOf(1099, 999), pixel);

        pixel = uppBasedTransform.toPixel(Points.create(110, 100, CrsId.UNDEFINED));
        assertEquals(Pixel.valueOf(1099, 200), pixel);

        pixel = uppBasedTransform.toPixel(Points.create(60, 60, CrsId.UNDEFINED));
        assertEquals(Pixel.valueOf(600, 600), pixel);

        //the next few cases test whether coordinates map properly to the correct pixel
        pixel = uppBasedTransform.toPixel(Points.create(60.1, 60, CrsId.UNDEFINED));
        assertEquals(Pixel.valueOf(601, 600), pixel);

        pixel = uppBasedTransform.toPixel(Points.create(60.05, 60, CrsId.UNDEFINED));
        assertEquals(Pixel.valueOf(600, 600), pixel);

        pixel = uppBasedTransform.toPixel(Points.create(60, 60.1, CrsId.UNDEFINED));
        assertEquals(Pixel.valueOf(600, 599), pixel);

        pixel = uppBasedTransform.toPixel(Points.create(60, 60.05, CrsId.UNDEFINED));
        assertEquals(Pixel.valueOf(600, 599), pixel);


    }

    @Test
    public void test_point_pixel_on_upp_based_transform_rounding_errors() {
        double originXinMapUnits = -20037508.34;
        double originYinMapUnits = -20037508.34;
        double unitsPerPixel = 4.777314266967774;
        Envelope boundingBoxInMapUnits = new Envelope(originXinMapUnits, originYinMapUnits,-originXinMapUnits,-originYinMapUnits);
        MapUnitToPixelTransform uppBasedTransform = new MapUnitToPixelTransform(boundingBoxInMapUnits, unitsPerPixel);
        int tileNumberX = 16811;
        int tileNumberY = 21781;
        int tileDimensionInPixels = 256;
        double tileWidthInMapUnits = tileDimensionInPixels * unitsPerPixel;
        double tileHeightInMapUnits = tileWidthInMapUnits;
        double tileMinXinMapUnits = tileNumberX * tileWidthInMapUnits + originXinMapUnits;
        double tileMinYinMapUnits = tileNumberY * tileHeightInMapUnits + originYinMapUnits;
        Envelope tileBoundingBoxInMapUnits = new Envelope(tileMinXinMapUnits, tileMinYinMapUnits, tileMinXinMapUnits + tileWidthInMapUnits, tileMinYinMapUnits + tileHeightInMapUnits);
        PixelRange tilePixelRange = uppBasedTransform.toPixelRange(tileBoundingBoxInMapUnits);
        assertEquals(tileDimensionInPixels, tilePixelRange.getWidth());
        assertEquals(tileDimensionInPixels, tilePixelRange.getHeight());
    }

    @Test
    public void test_bbox_to_pixrange_on_pixrange_based_transform() {
        Envelope bbox = new Envelope(-180, -90, 180, 90);
        PixelRange pixRange = new PixelRange(0, 0, 512, 256);
        MapUnitToPixelTransform mutpTransform = new MapUnitToPixelTransform(bbox, pixRange);
        Envelope testBBox = new Envelope(-170, -80, 170, 80);
        PixelRange result = mutpTransform.toPixelRange(testBBox);
        PixelRange expected = new PixelRange(14, 14, 512 - 28, 256 - 28);
        assertEquals(expected, result);

        testBBox = new Envelope(-180, -90, 180, 90);
        result = mutpTransform.toPixelRange(testBBox);
        expected = new PixelRange(0, 0, 512, 256);
        assertEquals(expected, result);

        mutpTransform = new MapUnitToPixelTransform(new Envelope(0, 0, 90, 90), new PixelRange(0, 0, 512, 256));
        testBBox = new Envelope(0, 0, 90, 90);
        result = mutpTransform.toPixelRange(testBBox);
        expected = new PixelRange(0, 0, 512, 256);
        assertEquals(expected, result);
    }

    //TODO -- this should be added to geometry interface
    public boolean equalsWithinTolerance(Point p1, Point p2, double tolerance){
        if (tolerance < 0) tolerance = -tolerance;
        return Math.abs(p1.getX() - p2.getX()) < tolerance &&
                Math.abs(p2.getY() - p2.getY()) < tolerance;
    }

}
