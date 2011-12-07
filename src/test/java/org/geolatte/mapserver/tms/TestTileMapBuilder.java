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

import junit.framework.Assert;
import org.dom4j.Document;
import org.dom4j.util.NodeComparator;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Point;
import org.geolatte.geom.crs.CrsId;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestTileMapBuilder {

    private TileMapBuilder builder;
    private static Document xmlDoc;

    static {
        xmlDoc = TMSTestSupport.makeTileCacheResource();
    }

    @Before
    public void setUp() {
        builder = new TileMapBuilder(xmlDoc);
    }

    @Test
    public void test_read_tilemap_resource() {
        Document xml = builder.getMetadataAsXML();
        Document expected = TMSTestSupport.makeTileCacheResource();
        NodeComparator comparator = new NodeComparator();
        Assert.assertEquals(0, comparator.compare(expected, xml));
    }

    @Test
    public void test_get_service_url() {
        String expected = "http://localhost/cgi-bin/tilecache.cgi/1.0.0/";
        String received = builder.getMapServiceUrl();
        assertEquals(expected, received);
    }

    @Test
    public void test_get_title() {
        assertEquals("basic", builder.getTitle());

    }

    @Test
    public void test_get_srs() {
        CrsId srs = builder.getSRS();
        assertEquals(CrsId.parse("EPSG: 4326"), srs);
    }

    @Test
    public void test_get_bbox() {
        Point ll = Point.create(-180.0000, -90.0000, CrsId.valueOf(4326));
        Point ur = Point.create(180.0000, 90.0000, CrsId.valueOf(4326));
        Envelope bbox = builder.getBoundingBox();

        assertEquals(ll, bbox.lowerLeft());
        assertEquals(ur, bbox.upperRight());
    }

    @Test
    public void test_get_origin() {
        Point origin = Point.create(-180.0000, -90.000, CrsId.UNDEFINED);
        assertEquals(origin, builder.getOrigin());
    }

    @Test
    public void test_get_image_dimension() {
        Dimension dimension = new Dimension(256, 256);
        assertEquals(dimension, builder.getTileDimension());
    }

    @Test
    public void test_get_tile_format() {
        TileFormat expected = new TileFormat(
                new Dimension(256, 256),
                "image/png", "png"
        );
        TileFormat format = builder.getTileFormat();
        assertEquals(expected, format);
    }

    @Test
    public void test_get_tilesets() {
        List<TileSet> tileSets = builder.getTileSets();
        assertEquals(20, tileSets.size());
        int expectedOrder = 0;
        for (Iterator<TileSet> it = tileSets.iterator(); it.hasNext(); expectedOrder++) {
            TileSet tileSet = it.next();
            Integer order = (Integer) TMSTestSupport.accessField(tileSet, "order");
            assertEquals(expectedOrder, order.intValue());
            String url = (String) TMSTestSupport.accessField(tileSet, "href");
            assertEquals("http://localhost/cgi-bin/tilecache.cgi/1.0.0/basic/" + expectedOrder, url);
        }
        Double unitsPerPixel = (Double) TMSTestSupport.runMethod(tileSets.get(0), "unitsPerPixel");
        assertEquals(0.70312500000000000000, unitsPerPixel, 0.0000005);
    }
}
