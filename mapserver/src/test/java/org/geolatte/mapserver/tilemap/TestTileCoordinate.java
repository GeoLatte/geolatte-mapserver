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

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestTileCoordinate {


    @Test
    public void test_range() {

        TileCoordinate start = TileCoordinate.valueOf(1, 1);
        TileCoordinate stop = TileCoordinate.valueOf(5, 5);
        List<TileCoordinate> range = TileCoordinate.range(start, stop);
        assertEquals(start, range.get(0));
        assertEquals(25, range.size());
        assertEquals(stop, range.get(range.size() - 1));

    }

    @Test
    public void test_range_revert_input() {
        TileCoordinate stop = TileCoordinate.valueOf(5, 5);
        TileCoordinate start = TileCoordinate.valueOf(1, 1);
        List<TileCoordinate> range = TileCoordinate.range(stop, start);
        assertEquals(start, range.get(0));
        assertEquals(25, range.size());
        assertEquals(stop, range.get(range.size() - 1));

        stop = TileCoordinate.valueOf(5, 1);
        start = TileCoordinate.valueOf(1, 5);
        range = TileCoordinate.range(stop, start);
        assertEquals(TileCoordinate.valueOf(1, 1), range.get(0));
        assertEquals(25, range.size());
        assertEquals(TileCoordinate.valueOf(5, 5), range.get(range.size() - 1));
    }


}
