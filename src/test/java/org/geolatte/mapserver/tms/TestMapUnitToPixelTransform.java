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

import org.geolatte.mapserver.util.BoundingBox;
import org.geolatte.mapserver.util.Pixel;
import org.geolatte.mapserver.util.PixelRange;
import org.geolatte.mapserver.util.Point;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: Jul 8, 2010
 */
public class TestMapUnitToPixelTransform {

    BoundingBox bbox = new BoundingBox(10, 20, 110, 100);
    PixelRange pixelRange = new PixelRange(100, 50, 100, 200);
    double mapUnitsPerPixel = 10.0;

    MapUnitToPixelTransform pixRangeBasedTransform = new MapUnitToPixelTransform(bbox, pixelRange);
    MapUnitToPixelTransform uppBasedTransform = new MapUnitToPixelTransform(bbox, 100, 200, 0.1);

    @Test
    public void test_pixel_outside_range_is_allowed() {
        Point received = pixRangeBasedTransform.toPoint(Pixel.valueOf(0, 0));
        Point expected = Point.valueOf(-90, 120);
        assertEquals(expected, received);

    }

    @Test
    public void test_pixel_to_point_on_pixrange_based_transform() {

        Point pnt = pixRangeBasedTransform.toPoint(Pixel.valueOf(100, 50));
        assertEquals(Point.valueOf(10, 100), pnt);

        pnt = pixRangeBasedTransform.toPoint(Pixel.valueOf(100, 249));
        assertEquals(Point.valueOf(10, 20.4), pnt);

        pnt = pixRangeBasedTransform.toPoint(Pixel.valueOf(199, 249));
        assertEquals(Point.valueOf(109, 20.4), pnt);

        pnt = pixRangeBasedTransform.toPoint(Pixel.valueOf(199, 50));
        assertEquals(Point.valueOf(109, 100), pnt);

        pnt = pixRangeBasedTransform.toPoint(Pixel.valueOf(150, 150));
        assertEquals(Point.valueOf(60, 60), pnt);

    }

    @Test
    public void test_pixel_to_point_on_upp_based_transform() {

        Point pnt = uppBasedTransform.toPoint(Pixel.valueOf(100, 200));
        assertEquals(Point.valueOf(10, 100), pnt);

        pnt = uppBasedTransform.toPoint(Pixel.valueOf(100, 999));
        assertEquals(Point.valueOf(10, 20.1), pnt);

        pnt = uppBasedTransform.toPoint(Pixel.valueOf(1099, 999));
        assertEquals(Point.valueOf(109.9, 20.1), pnt);

        pnt = uppBasedTransform.toPoint(Pixel.valueOf(1099, 200));
        assertEquals(Point.valueOf(109.9, 100), pnt);

        pnt = uppBasedTransform.toPoint(Pixel.valueOf(600, 600));
        assertEquals(Point.valueOf(60, 60), pnt);

    }

    @Test
    public void test_point_outside_extent_is_allowed() {

        Pixel received = pixRangeBasedTransform.toPixel(Point.valueOf(0, 2));
        Pixel expected = Pixel.valueOf(90, 295);
        assertEquals(expected, received);
    }

    @Test
    public void test_point_to_pixel_on_pixrange_based_transform() {
        Pixel pixel = pixRangeBasedTransform.toPixel(Point.valueOf(10, 20));
        assertEquals(Pixel.valueOf(100, 249), pixel);

        pixel = pixRangeBasedTransform.toPixel(Point.valueOf(10, 100));
        assertEquals(Pixel.valueOf(100, 50), pixel);

        pixel = pixRangeBasedTransform.toPixel(Point.valueOf(110, 100));
        assertEquals(Pixel.valueOf(199, 50), pixel);

        pixel = pixRangeBasedTransform.toPixel(Point.valueOf(110, 20));
        assertEquals(Pixel.valueOf(199, 249), pixel);

        pixel = pixRangeBasedTransform.toPixel(Point.valueOf(60, 60));
        assertEquals(Pixel.valueOf(150, 150), pixel);

    }

    @Test
    public void test_point_pixel_on_upp_based_transform() {
        Pixel pixel = uppBasedTransform.toPixel(Point.valueOf(10, 100));
        assertEquals(Pixel.valueOf(100, 200), pixel);

        pixel = uppBasedTransform.toPixel(Point.valueOf(10, 20));
        assertEquals(Pixel.valueOf(100, 999), pixel);

        pixel = uppBasedTransform.toPixel(Point.valueOf(110, 20));
        assertEquals(Pixel.valueOf(1099, 999), pixel);

        pixel = uppBasedTransform.toPixel(Point.valueOf(110, 100));
        assertEquals(Pixel.valueOf(1099, 200), pixel);

        pixel = uppBasedTransform.toPixel(Point.valueOf(60, 60));
        assertEquals(Pixel.valueOf(600, 600), pixel);

        //the next few cases test whether coordinates map properly to the correct pixel
        pixel = uppBasedTransform.toPixel(Point.valueOf(60.1, 60));
        assertEquals(Pixel.valueOf(601, 600), pixel);

        pixel = uppBasedTransform.toPixel(Point.valueOf(60.05, 60));
        assertEquals(Pixel.valueOf(600, 600), pixel);

        pixel = uppBasedTransform.toPixel(Point.valueOf(60, 60.1));
        assertEquals(Pixel.valueOf(600, 599), pixel);

        pixel = uppBasedTransform.toPixel(Point.valueOf(60, 60.05));
        assertEquals(Pixel.valueOf(600, 599), pixel);


    }

    @Test
    public void test_bbox_to_pixrange_on_pixrange_based_transform() {
        BoundingBox bbox = new BoundingBox(-180, -90, 180, 90);
        PixelRange pixRange = new PixelRange(0, 0, 512, 256);
        MapUnitToPixelTransform mutpTransform = new MapUnitToPixelTransform(bbox, pixRange);
        BoundingBox testBBox = new BoundingBox(-170, -80, 170, 80);
        PixelRange result = mutpTransform.toPixelRange(testBBox);
        PixelRange expected = new PixelRange(14, 14, 512 - 28, 256 - 28);
        assertEquals(expected, result);

        testBBox = new BoundingBox(-180, -90, 180, 90);
        result = mutpTransform.toPixelRange(testBBox);
        expected = new PixelRange(0, 0, 512, 256);
        assertEquals(expected, result);

        mutpTransform = new MapUnitToPixelTransform(new BoundingBox(0, 0, 90, 90), new PixelRange(0, 0, 512, 256));
        testBBox = new BoundingBox(0, 0, 90, 90);
        result = mutpTransform.toPixelRange(testBBox);
        expected = new PixelRange(0, 0, 512, 256);
        assertEquals(expected, result);
    }


}
