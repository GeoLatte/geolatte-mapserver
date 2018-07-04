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

import org.geolatte.mapserver.TMSTestSupport;

public class TestTileSetChooser {

    static TileMap tileMap = TMSTestSupport.makeOrthoTileMap();

    //TODO these tests need to be redone

//    @Test
//    public void test_determine_request_UnitsPerPixel_squarebox() {
//
//        TileSetChooser tsc;
//        double upp;
//        Dimension dim = new Dimension(256, 256);
//        //order 0
//        Envelope bbox = new Envelope(Points.create2D(-180, -90, CrsId.UNDEFINED), Points.create2D(0, 90, CrsId.UNDEFINED));
//        tsc = new TileSetChooser(tileMap, bbox, dim);
//        tsc.determineRequestUnitsPerPixel();
//        upp = (Double) TMSTestSupport.accessField(tsc, "requestUnitsPerPixel");
//        assertEquals(0.70312500000000000000, upp, 0.00000005);
//
//        //order 1
//        bbox = new Envelope(Points.create2D(-45, 0, CrsId.UNDEFINED), Points.create2D(45, 90, CrsId.UNDEFINED));
//        tsc = new TileSetChooser(tileMap, bbox, dim);
//        tsc.determineRequestUnitsPerPixel();
//        upp = (Double) TMSTestSupport.accessField(tsc, "requestUnitsPerPixel");
//        assertEquals(0.35156250000000000000, upp, 0.00000005);
//    }
//
//
//    @Test
//    public void test_determine_request_UnitsPerPixel_aspectratio_difference() {
//        TileSetChooser tsc;
//        double upp;
//
//        //order 0
//        Envelope bbox = new Envelope(Points.create2D(-180, -90, CrsId.UNDEFINED), Points.create2D(0, 0, CrsId.UNDEFINED));
//        Dimension dim = new Dimension(256, 256);
//        tsc = new TileSetChooser(tileMap, bbox, dim);
//        tsc.determineRequestUnitsPerPixel();
//        upp = (Double) TMSTestSupport.accessField(tsc, "requestUnitsPerPixel");
//        assertEquals(0.70312500000000000000, upp, 0.00000005);
//    }
//
//    @Test
//    public void test_determine_tile_set() {
//        TileSetChooser tsc;
//        Dimension dim = new Dimension(256, 256);
//
//        //order 0
//        Envelope bbox = new Envelope(Points.create2D(-180, -90, CrsId.UNDEFINED), Points.create2D(0, 90, CrsId.UNDEFINED));
//        tsc = new TileSetChooser(tileMap, bbox, dim);
//        TileSet ts = tsc.chooseTileSet();
//        assertEquals(0, ts.getOrder());
//
//        //order 1
//        bbox = new Envelope(Points.create2D(-45, 0, CrsId.UNDEFINED), Points.create2D(45, 90, CrsId.UNDEFINED));
//        tsc = new TileSetChooser(tileMap, bbox, dim);
//        ts = tsc.chooseTileSet();
//        assertEquals(1, ts.getOrder());
//
//    }
}
