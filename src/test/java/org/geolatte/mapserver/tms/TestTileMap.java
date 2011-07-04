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
import org.geolatte.mapserver.util.PixelRange;
import org.geolatte.mapserver.util.Point;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

public class TestTileMap {


    TileMap tileMap;


    @Before
    public void setUp() throws TileMapCreationException {
        TileMapBuilder builder = TileMapBuilder.fromURL(TMSTestSupport.URL);
        tileMap = builder.buildTileMap();
    }


    @Test
    public void test_tile_width_and_height_in_map_units() {
        TileSet tileSet = tileMap.getTileSets().get(3);
        TileSetCoordinateSpace cs = tileSet.getTileCoordinateSpace();
        double w = cs.tileWidthInMapUnits();
        assertEquals(22.5, w, 0.000005);
        double h = cs.tileHeightInMapUnits();
        assertEquals(22.5, h, 0.000005);
    }

    @Test
    public void test_tile_image_bounds() {
        TileSet tileSet = tileMap.getTileSets().get(2);

        Tile tile = tileMap.makeTile(tileSet, TileCoordinate.valueOf(0, 3));
        PixelRange expected = new PixelRange(0, 0, new Dimension(256, 256));
        PixelRange received = tile.getPixelBounds();
        assertEquals(expected, received);

        tile = tileMap.makeTile(tileSet, TileCoordinate.valueOf(7, 3));
        expected = new PixelRange(7 * 256, 0, new Dimension(256, 256));
        received = tile.getPixelBounds();
        assertEquals(expected, received);

        tile = tileMap.makeTile(tileSet, TileCoordinate.valueOf(0, 0));
        expected = new PixelRange(0, 3 * 256, new Dimension(256, 256));
        received = tile.getPixelBounds();
        assertEquals(expected, received);

        tile = tileMap.makeTile(tileSet, TileCoordinate.valueOf(2, 2));
        expected = new PixelRange(512, 256, new Dimension(256, 256));
        received = tile.getPixelBounds();
        assertEquals(expected, received);

    }

    @Test
    public void test_tile_bbox() {
        TileSet tileSet = tileMap.getTileSets().get(2);

        Tile tile = tileMap.makeTile(tileSet, TileCoordinate.valueOf(0, 3));
        BoundingBox expected = new BoundingBox(-180, 45, -135, 90);
        BoundingBox received = tile.getBoundingBox();
        assertEquals(expected, received);

        tile = tileMap.makeTile(tileSet, TileCoordinate.valueOf(7, 3));
        expected = new BoundingBox(180 - 45, 45, 180, 90);
        received = tile.getBoundingBox();
        assertEquals(expected, received);

        tile = tileMap.makeTile(tileSet, TileCoordinate.valueOf(0, 0));
        expected = new BoundingBox(-180, -90, -180 + 45, -45);
        received = tile.getBoundingBox();
        assertEquals(expected, received);

        tile = tileMap.makeTile(tileSet, TileCoordinate.valueOf(2, 2));
        expected = new BoundingBox(-180 + 2 * 45, 0, -180 + 3 * 45, 45);
        received = tile.getBoundingBox();
        assertEquals(expected, received);

    }

    @Test
    public void test_get_tiles_for_order_zero() {
        Set<Tile> expected = new HashSet<Tile>();
        TileSet tileSet = tileMap.getTileSets().get(0);
        expected.add(tileMap.makeTile(tileSet, TileCoordinate.valueOf(0, 0)));
        BoundingBox bbox = new BoundingBox(new Point(-180, -90), new Point(-1, 89));
        Dimension dim = new Dimension(256, 256);
        Set<Tile> result = tileMap.getTilesFor(tileSet, bbox);
        Assert.assertEquals(expected, result);

        expected.add(tileMap.makeTile(tileSet, TileCoordinate.valueOf(1, 0)));
        bbox = new BoundingBox(new Point(-180, -90), new Point(180, 90));
        result = tileMap.getTilesFor(tileSet, bbox);
        Assert.assertEquals(expected, result);

        bbox = new BoundingBox(new Point(-170, -80), new Point(160, 88));
        result = tileMap.getTilesFor(tileSet, bbox);
        Assert.assertEquals(expected, result);
    }


}
