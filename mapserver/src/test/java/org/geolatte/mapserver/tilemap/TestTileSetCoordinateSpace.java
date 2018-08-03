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
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.geolatte.mapserver.util.Pixel;
import org.geolatte.mapserver.util.PixelRange;
import org.junit.Test;

import java.awt.*;

import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.point;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Karel Maesen, Geovise BVBA
 * creation-date: Jun 16, 2010
 */
public class TestTileSetCoordinateSpace {

    private CoordinateReferenceSystem<C2D> crs = CoordinateReferenceSystems.PROJECTED_2D_METER;
    private TileSetCoordinateSpace space1 = new TileSetCoordinateSpace(point(crs, c(500, 500)),
            new Dimension(10, 10), new Envelope<>(50, 50, 1050, 1050, crs), 5);
    private TileSetCoordinateSpace space2 = new TileSetCoordinateSpace(point(crs, c(-180, -90)),
            new Dimension(256, 256), new Envelope<>(-180, -90, 180, 90, crs), 0.17578125);

    @Test
    public void test_get_bounds_for_tile_coordinate() {

        Envelope<C2D> received = space1.boundingBox(TileCoordinate.valueOf(0, 0));
        Envelope<C2D> expected = new Envelope<>(500, 500, 550, 550, crs);
        assertEquals(expected, received);

        received = space1.boundingBox(TileCoordinate.valueOf(-1, -1));
        expected = new Envelope<>(450, 450, 500, 500, crs);
        assertEquals(expected, received);

        received = space1.boundingBox(TileCoordinate.valueOf(2, 3));
        expected = new Envelope<>(600, 650, 650, 700, crs);
        assertEquals(expected, received);


        received = space1.boundingBox(TileCoordinate.valueOf(100, 100));
        expected = new Envelope<>(5500, 5500, 5550, 5550, crs);
        assertEquals("Also tilecoordinates beyond extent of tilemap are allowed.", expected, received);

    }

    @Test
    public void test_get_pixelrange_for_tile_coordinate() {
        //TODO -- verify test cases
        PixelRange received = space1.tilePixelRange(TileCoordinate.valueOf(0, 0));
        PixelRange expected = new PixelRange(90, 100, new Dimension(10, 10));
        assertEquals(expected, received);

        received = space1.tilePixelRange(TileCoordinate.valueOf(2, 3));
        expected = new PixelRange(110, 70, new Dimension(10, 10));
        assertEquals(expected, received);

        received = space1.tilePixelRange(TileCoordinate.valueOf(-9, 10));
        expected = new PixelRange(0, 0, new Dimension(10, 10));
        assertEquals(expected, received);

        received = space2.tilePixelRange(TileCoordinate.valueOf(0, 3));
        expected = new PixelRange(0, 0, new Dimension(256, 256));
        assertEquals(expected, received);


    }

    @Test
    public void test_tileset_pixeldimension() {
        Dimension received = space1.tileSetPixelDimension();
        Dimension expected = new Dimension(200, 200);
        assertEquals(expected, received);

        TileSetCoordinateSpace tcs = new TileSetCoordinateSpace(point(crs, c(500, 500)), new Dimension(20, 40),
                new Envelope<>(0, 0, 1000, 5000, crs), 10);
        expected = new Dimension(100, 500);
        received = tcs.tileSetPixelDimension();
        assertEquals(expected, received);
    }


    @Test
    public void test_tileWidth_In_MapUnits() {
        TileSetCoordinateSpace tcs = new TileSetCoordinateSpace(point(crs,c(500, 500)),
                new Dimension(20, 40), new Envelope<>(0, 0, 1000, 5000,crs), 10);
        double received = tcs.tileWidthInMapUnits();
        assertEquals(200.0, received, 0.00001);
    }

    @Test
    public void test_tileHeight_In_MapUnits() {
        TileSetCoordinateSpace tcs = new TileSetCoordinateSpace(point(crs, c(500, 500)),
                new Dimension(20, 40), new Envelope<>(0, 0, 1000, 5000,crs), 10);
        double received = tcs.tileHeightInMapUnits();
        assertEquals(400.0, received, 0.00001);
    }

    @Test
    public void test_point_to_tile_coordinate() {

        TileCoordinate received = space1.tileCoordinateContaining(point(crs, c(500, 500)), true);
        assertEquals(TileCoordinate.valueOf(0, 0), received);

        received = space1.tileCoordinateContaining(point(crs, c(50, 50)), true);
        assertEquals(TileCoordinate.valueOf(-9, -9), received);

        received = space1.tileCoordinateContaining(point(crs, c(610, 575)), true);
        assertEquals(TileCoordinate.valueOf(2, 1), received);

        received = space1.tileCoordinateContaining(point(crs, c(550, 550)), false);
        assertEquals(TileCoordinate.valueOf(0, 0), received);

        received = space1.tileCoordinateContaining(point(crs, c(550, 550)), true);
        assertEquals(TileCoordinate.valueOf(1, 1), received);

        try {
            space2.tileCoordinateContaining(point(crs, c(220, 220)), true);
            fail();
        } catch (IllegalArgumentException e) {
            //OK
        }


    }

    @Test
    public void test_to_pixel() {
        Pixel expected = space1.toPixel(point(crs, c(50, 50)));
        Pixel received = Pixel.valueOf(0, 199);
        assertEquals(expected, received);

        received = space1.toPixel(point(crs, c(1050, 1050)));
        expected = Pixel.valueOf(199, 0);
        assertEquals(expected, received);

        received = space1.toPixel(point(crs, c(1050, 50)));
        expected = Pixel.valueOf(199, 199);
        assertEquals(expected, received);

        received = space1.toPixel(point(crs, c(50, 1050)));
        expected = Pixel.valueOf(0, 0);
        assertEquals(expected, received);

        received = space1.toPixel(point(crs, c(100, 250)));
        expected = Pixel.valueOf(10, 160);
        assertEquals(expected, received);

        received = space1.toPixel(point(crs, c(287, 1023)));
        expected = Pixel.valueOf(47, 5);
        assertEquals(expected, received);


        received = space1.toPixel(point(crs, c(1200, 2000)));
        expected = Pixel.valueOf(230, -190);
        assertEquals("Points outside TILE_MAP extent are allowed.", expected, received);

        received = space1.toPixel(point(crs, c(0, 0)));
        expected = Pixel.valueOf(-10, 210);
        assertEquals("Points outside TILE_MAP extent are allowed.", received, expected);

    }


    @Test
    public void test_pixelrange_bbox() {
        PixelRange received = space1.pixelRange(new Envelope<>(50, 50, 1050, 1050, crs));
        PixelRange expected = new PixelRange(0, 0, 200, 200);
        assertEquals(expected, received);

        received = space1.pixelRange(new Envelope<>(50, 950, 150, 1050, crs));
        expected = new PixelRange(0, 0, 20, 20);
        assertEquals(expected, received);

        received = space1.pixelRange(new Envelope<>(0, 950, 1100, 1100, crs));
        expected = new PixelRange(-10, -10, 220, 30);
        assertEquals(expected, received);

    }

    @Test
    public void test_to_point() {
        Point received = space1.toPoint(Pixel.valueOf(0, 199));
        Point expected = point(crs, c(50, 55));
        assertEquals(expected, received);

        received = space1.toPoint(Pixel.valueOf(199, 0));
        expected = point(crs, c(1045, 1050));
        assertEquals(expected, received);

        received = space1.toPoint(Pixel.valueOf(199, 199));
        expected = point(crs, c(1045, 55));
        assertEquals(expected, received);

        received = space1.toPoint(Pixel.valueOf(0, 0));
        expected = point(crs, c(50, 1050));
        assertEquals(expected, received);

        received = space1.toPoint(Pixel.valueOf(10, 160));
        expected = point(crs, c(100, 250));
        assertEquals(expected, received);

        received = space1.toPoint(Pixel.valueOf(201, 201));
        expected = point(crs, c(1055, 45));
        assertEquals(expected, received);
    }
}
