///*
// * Copyright 2009-2010  Geovise BVBA, QMINO BVBA
// *
// * This file is part of GeoLatte Mapserver.
// *
// * GeoLatte Mapserver is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * GeoLatte Mapserver is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public License
// * along with GeoLatte Mapserver.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package org.geolatte.mapserver.tilemap;
//
//import org.geolatte.mapserver.TMSTestSupport;
//import org.geolatte.mapserver.img.Imaging;
//import org.geolatte.mapserver.img.JAIImaging;
//import org.junit.Test;
//
//import javax.media.jai.PlanarImage;
//import java.io.IOException;
//import java.util.Set;
//
//import static org.junit.Assert.assertTrue;
//
//public class TestTile {
//
//    private Imaging imaging = new JAIImaging();
//
//    @Test
//    public void test_loading_images() throws IOException {
//        Set<Tile> tiles = TMSTestSupport.getTestTiles();
//        for (Tile tile : tiles) {
//            TileImage img = tile.getImage(imaging, false);
//            assertTrue(img.getInternalRepresentation() instanceof PlanarImage);
//        }
//    }
//}
