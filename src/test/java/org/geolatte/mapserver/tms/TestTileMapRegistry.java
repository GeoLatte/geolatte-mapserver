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

import org.geolatte.geom.crs.CrsId;
import org.geolatte.mapserver.config.Configuration;
import org.geolatte.mapserver.config.ConfigurationException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestTileMapRegistry {

    TileMapRegistry tileMaps;

    @Before
    public void setUp() throws ConfigurationException {
        Configuration config = Configuration.load("test-config.xml");
        tileMaps = TileMapRegistry.configure(config);
    }

    @Test
    public void check_tilemaps_present() {
        List<String> expected = new ArrayList<String>();
        expected.add("basic");
        expected.add("osm");
        expected.add("tms-vlaanderen");
        List<String> received = tileMaps.getTileMapNames();
        assertEquals(expected, received);
    }

    @Test
    public void check_supported_CrsId() {
        assertTrue(tileMaps.getSupportedSRS("basic").isEmpty());
        List<CrsId> expected = new ArrayList<CrsId>();
        expected.add(CrsId.parse("EPSG:25831"));
        expected.add(CrsId.parse("EPSG:900913"));
        List<CrsId> received = tileMaps.getSupportedSRS("tms-vlaanderen");
        assertEquals(expected, received);
    }

    @Test
    public void check_is_CrsId_supported() {
        assertTrue(tileMaps.supportsSRS("tms-vlaanderen", CrsId.parse("EPSG:31370")));
        assertTrue(tileMaps.supportsSRS("tms-vlaanderen", CrsId.parse("EPSG:900913")));
        assertTrue(tileMaps.supportsSRS("tms-vlaanderen", CrsId.parse("EPSG:25831")));
        assertFalse(tileMaps.supportsSRS("tms-vlaanderen", CrsId.parse("EPSG:4326")));
    }

    @Test
    public void verify_tilemap() {
        TileMap tileMap = tileMaps.getTileMap("basic");
        assertNotNull(tileMap);
    }


}
