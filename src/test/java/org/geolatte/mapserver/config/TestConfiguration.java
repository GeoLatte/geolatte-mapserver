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

package org.geolatte.mapserver.config;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestConfiguration {

    Configuration config;

    @Before
    public void setUp() throws ConfigurationException {
        config = Configuration.load("test-config.xml");
    }

    @Test
    public void test_config_file_not_on_classpath() {
        try {
            config = Configuration.load("non-existing.xml");
            fail();
        } catch (ConfigurationException e) {
            //this is OK
        } catch (Exception e) {
            fail("Wrong exception type.");
        }
    }

    @Test
    public void test_get_tilemaps() {
        List<String> tileMapNames = config.getTileMaps();
        List<String> expected = new ArrayList<String>();
        expected.add("basic");
        expected.add("osm");
        expected.add("orthos");
        expected.add("error");
        assertEquals(expected, tileMapNames);

    }

    @Test
    public void test_get_type_of_tilemap() throws ConfigurationException {
        Configuration.RESOURCE_TYPE expected = Configuration.RESOURCE_TYPE.URL;
        assertEquals(expected, config.getType("basic"));

        expected = Configuration.RESOURCE_TYPE.FILE;
        assertEquals(expected, config.getType("osm"));

        try {
            config.getType("error");
            fail();
        } catch (ConfigurationException e) {
            //this is OK
        } catch (Exception e) {
            fail("Wrong exception type. Expected IllegalStateException.");
        }
    }

    @Test
    public void test_get_path_for_tilemap() throws ConfigurationException {
        String expected;
        expected = "http://localhost/cgi-bin/tilecache.cgi/1.0.0/basic/";
        assertEquals(expected, config.getPath("basic"));


        expected = "/tmp/tiles/tilemapresource.xml";
        assertEquals(expected, config.getPath("osm"));

        try {
            config.getPath("error");
            fail();
        } catch (ConfigurationException e) {
            //this is OK
        } catch (Exception e) {
            fail("Expected ConfigurationException");
        }
    }

    @Test
    public void test_get_sourcefactory_for_tilemap() throws ConfigurationException {
        String expected;
        expected = "org.geolatte.mapserver.tms.URLTileImageSourceFactory";
        assertEquals(expected, config.getTileImageSourceFactoryClass("basic"));


        expected = "org.geolatte.mapserver.tms.FileTileImageSourceFactory";
        assertEquals(expected, config.getTileImageSourceFactoryClass("osm"));

        try {
            config.getTileImageSourceFactoryClass("error");
            fail();
        } catch (ConfigurationException e) {
            //this is OK
        } catch (Exception e) {
            fail("Expected ConfigurationException");
        }

    }

    @Test
    public void test_get_wms_service_elements() {
        assertEquals("incorrect title read", "test title", config.getWMSServiceTitle());
        assertEquals("incorrect title read", "test abstract", config.getWMSServiceAbstract());
        assertArrayEquals("incorrect keyword list", new String[]{"kw1", "kw2"}, config.getWMSServiceKeywords());
        assertEquals("incorrect URL retrieved", "http://www.geolatte.org", config.getWMSServiceOnlineResource());


    }

    @Test
    public void test_exception_when_path_not_found() {
        try {
            Configuration config = Configuration.load("doenstexisst.xml");
            fail();
        } catch (ConfigurationException e) {
            //ok
            return;
        }
    }


}
