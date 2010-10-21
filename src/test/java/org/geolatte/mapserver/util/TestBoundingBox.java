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

import static org.junit.Assert.*;

public class TestBoundingBox {


    @Test
    public void test_invalid_creation() {
        try {
            BoundingBox test = new BoundingBox(50, 50, 25, 75);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            BoundingBox test = new BoundingBox(50, 50, 75, 25);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            BoundingBox test = new BoundingBox(50, 50, 25, 25);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void test_union() {
        BoundingBox b1 = new BoundingBox(0, 0, 45, 45);
        BoundingBox b2 = new BoundingBox(45, 90, 90, 135);
        BoundingBox expected = new BoundingBox(0, 0, 90, 135);
        BoundingBox received = BoundingBox.union(b1, b2);
        assertEquals(expected, received);
    }

    @Test
    public void test_isWithin() {
        BoundingBox b0 = new BoundingBox(0, 0, 500, 100);
        assertTrue(b0.isWithin(b0));

        BoundingBox b1 = new BoundingBox(10, 20, 30, 40);
        assertTrue(b1.isWithin(b0));
        assertFalse(b0.isWithin(b1));

        b1 = new BoundingBox(-10, 20, 30, 40);
        assertFalse(b1.isWithin(b0));
        assertFalse(b0.isWithin(b1));

        b1 = new BoundingBox(-10, -10, -5, -5);
        assertFalse(b1.isWithin(b0));

        b1 = new BoundingBox(-10, -10, 600, 120);
        assertFalse(b1.isWithin(b0));

        b1 = new BoundingBox(600, 50, 700, 80);
        assertFalse(b1.isWithin(b0));


    }

    @Test
    public void test_intersect() {
        //clip boundingbox
        BoundingBox b0 = new BoundingBox(0, 0, 100, 100);

        BoundingBox b1 = new BoundingBox(10, 20, 130, 110);
        BoundingBox expected1 = new BoundingBox(10, 20, 100, 100);
        BoundingBox received1 = b0.intersect(b1);
        assertEquals(expected1, received1);

        BoundingBox b2 = new BoundingBox(-10, -8, 80, 90);
        BoundingBox expected2 = new BoundingBox(0, 0, 80, 90);
        BoundingBox received2 = b0.intersect(b2);
        assertEquals(expected2, received2);

        BoundingBox b3 = new BoundingBox(-10, -20, 130, 110);
        BoundingBox expected3 = new BoundingBox(0, 0, 100, 100);
        BoundingBox received3 = b0.intersect(b3);
        assertEquals(expected3, received3);

        BoundingBox b4 = new BoundingBox(-10, -10, -1, -1);
        BoundingBox expected4 = new BoundingBox(0, 0, 0, 0);
        BoundingBox received4 = b0.intersect(b4);
        assertEquals(expected4, received4);

        BoundingBox b5 = new BoundingBox(110, 105, 120, 130);
        BoundingBox expected5 = new BoundingBox(0, 0, 0, 0);
        BoundingBox received5 = b0.intersect(b5);
        assertEquals(expected5, received5);

        BoundingBox b6 = new BoundingBox(-10, 10, 50, 110);
        BoundingBox expected6 = new BoundingBox(0, 10, 50, 100);
        BoundingBox received6 = b0.intersect(b6);
        assertEquals(expected6, received6);

        BoundingBox b7 = new BoundingBox(10, 110, 90, 115);
        BoundingBox expected7 = new BoundingBox(0, 0, 0, 0);
        BoundingBox received7 = b0.intersect(b7);
        assertEquals(expected7, received7);

        BoundingBox b8 = new BoundingBox(10, 20, 50, 60);
        BoundingBox received8 = b0.intersect(b8);
        assertEquals(b8, received8);

        BoundingBox b9 = new BoundingBox(110, 20, 115, 60);
        BoundingBox expected9 = new BoundingBox(0, 0, 0, 0);
        BoundingBox received9 = b0.intersect(b9);
        assertEquals(expected9, received9);
    }

    @Test
    public void test_contains() {
        BoundingBox box = new BoundingBox(0, 0, 100, 100);
        Point p = Point.valueOf(0, 0);
        assertTrue(box.contains(p));

        p = Point.valueOf(100, 100);
        assertTrue(box.contains(p));

        p = Point.valueOf(10, 20);
        assertTrue(box.contains(p));

        p = Point.valueOf(-10, 0);
        assertFalse(box.contains(p));

        p = Point.valueOf(0, -10);
        assertFalse(box.contains(p));

        p = Point.valueOf(110, 0);
        assertFalse(box.contains(p));

        p = Point.valueOf(0, 110);
        assertFalse(box.contains(p));

        p = Point.valueOf(110, 110);
        assertFalse(box.contains(p));


    }

}
