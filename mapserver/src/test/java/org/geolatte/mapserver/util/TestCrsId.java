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
//package org.geolatte.mapserver.util;
//
//import org.geolatte.geom.crs.CrsId;
//import org.junit.Test;
//
//import static junit.framework.Assert.*;
//
////TODO -- move this to geolatte-geom
//public class TestCrsId {
//
//
//    @Test
//    public void test_parse() {
//        CrsId srs = CrsId.parse("EPSG:4326");
//        assertEquals("EPSG", srs.getAuthority());
//        assertEquals(4326, srs.getCode());
//
//        srs = CrsId.parse("EPSG: 4326");
//        assertEquals("EPSG", srs.getAuthority());
//        assertEquals(4326, srs.getCode());
//
//        srs = CrsId.parse("4326");
//        assertEquals("EPSG", srs.getAuthority());
//        assertEquals(4326, srs.getCode());
//
//    }
//
//    @Test
//    public void test_invalid_input() {
//        try {
//            CrsId srs = CrsId.parse("EPSG: xxx");
//            fail();
//        } catch (IllegalArgumentException e) {
//        }
//
//        try {
//            CrsId srs = CrsId.parse("blabla");
//            fail();
//        } catch (IllegalArgumentException e) {
//            //OK
//        }
//    }
//
//    @Test
//    public void test_null_input() {
//        try {
//            CrsId srs = CrsId.parse(null);
//            fail();
//        } catch (IllegalArgumentException e) {
//            // OK
//        }
//    }
//
//    @Test
//    public void test_equality() {
//        CrsId srs1 = CrsId.parse("EPSG: 4326");
//        CrsId srs2 = CrsId.parse("4326");
//        assertEquals(srs1, srs2);
//        srs2 = CrsId.parse("EPSG: 31370");
//        assertFalse(srs1.equals(srs2));
//
//    }
//
//
//}
