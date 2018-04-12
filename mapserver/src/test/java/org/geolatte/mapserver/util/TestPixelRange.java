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

import java.awt.*;

import static org.junit.Assert.assertEquals;

public class TestPixelRange {

    @Test
    public void test_max_min() {
        PixelRange b = new PixelRange(0, 0, 512, 256);
        assertEquals(512, b.getWidth());
        assertEquals(256, b.getHeight());
        assertEquals(511, b.getMaxX());
        assertEquals(255, b.getMaxY());

        b = new PixelRange(10, 10, 512, 256);
        assertEquals(512, b.getWidth());
        assertEquals(256, b.getHeight());
        assertEquals(521, b.getMaxX());
        assertEquals(265, b.getMaxY());


    }

    @Test
    public void test_union_image_bounds() {

        PixelRange b1 = new PixelRange(768, 0, new Dimension(256, 256));
        PixelRange b2 = new PixelRange(512, 256, new Dimension(256, 512));

        PixelRange expected = new PixelRange(512, 0, new Dimension(512, 768));
        PixelRange received = PixelRange.union(b1, b2);
        assertEquals(expected, received);
    }

    @Test
    public void test_union_with_null() {
        PixelRange expected = new PixelRange(768, 0, new Dimension(256, 256));
        PixelRange received = PixelRange.union(expected, null);
        assertEquals(expected, received);
        received = PixelRange.union(null, expected);
        assertEquals(expected, received);
    }
}
