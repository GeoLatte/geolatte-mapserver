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

package org.geolatte.mapserver.util;

import org.junit.Test;

import static junit.framework.Assert.*;

public class TestSRS {


    @Test
    public void test_parse() {
        SRS srs = SRS.parse("EPSG:4326");
        assertEquals("EPSG", srs.authority);
        assertEquals(4326, srs.code);

        srs = SRS.parse("EPSG: 4326");
        assertEquals("EPSG", srs.authority);
        assertEquals(4326, srs.code);

        srs = SRS.parse("4326");
        assertEquals("EPSG", srs.authority);
        assertEquals(4326, srs.code);

    }

    @Test
    public void test_invalid_input() {
        try {
            SRS srs = SRS.parse("EPSG: xxx");
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            SRS srs = SRS.parse("blabla");
            fail();
        } catch (IllegalArgumentException e) {
            //OK
        }
    }

    @Test
    public void test_null_input() {
        try {
            SRS srs = SRS.parse(null);
            fail();
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void test_equality() {
        SRS srs1 = SRS.parse("EPSG: 4326");
        SRS srs2 = SRS.parse("4326");
        assertEquals(srs1, srs2);
        srs2 = SRS.parse("EPSG: 31370");
        assertFalse(srs1.equals(srs2));

    }


}
