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

import org.geolatte.geom.Envelope;
import org.geolatte.geom.Point;
import org.geolatte.geom.Points;
import org.junit.Test;

import static org.junit.Assert.*;

//TODO -- this should be moved to geolatte-geom

public class TestBoundingBox {


    @Test
    public void test_invalid_creation() {

        Envelope test = new Envelope(50, 50, 25, 75);
        assertTrue(test.isEmpty());


        test = new Envelope(50, 50, 75, 25);
        assertTrue(test.isEmpty());

        test = new Envelope(50, 50, 25, 25);
        assertTrue(test.isEmpty());

    }

    @Test
    public void test_union() {
        Envelope b1 = new Envelope(0, 0, 45, 45);
        Envelope b2 = new Envelope(45, 90, 90, 135);
        Envelope expected = new Envelope(0, 0, 90, 135);
        Envelope received = Envelope.union(b1, b2);
        assertEquals(expected, received);
    }

    @Test
    public void test_isWithin() {
        Envelope b0 = new Envelope(0, 0, 500, 100);
        assertTrue(b0.within(b0));

        Envelope b1 = new Envelope(10, 20, 30, 40);
        assertTrue(b1.within(b0));
        assertFalse(b0.within(b1));

        b1 = new Envelope(-10, 20, 30, 40);
        assertFalse(b1.within(b0));
        assertFalse(b0.within(b1));

        b1 = new Envelope(-10, -10, -5, -5);
        assertFalse(b1.within(b0));

        b1 = new Envelope(-10, -10, 600, 120);
        assertFalse(b1.within(b0));

        b1 = new Envelope(600, 50, 700, 80);
        assertFalse(b1.within(b0));


    }

    @Test
    public void test_intersect() {
        //clip boundingbox
        Envelope b0 = new Envelope(0, 0, 100, 100);

        Envelope b1 = new Envelope(10, 20, 130, 110);
        Envelope expected1 = new Envelope(10, 20, 100, 100);
        Envelope received1 = b0.intersect(b1);
        assertEquals(expected1, received1);

        Envelope b2 = new Envelope(-10, -8, 80, 90);
        Envelope expected2 = new Envelope(0, 0, 80, 90);
        Envelope received2 = b0.intersect(b2);
        assertEquals(expected2, received2);

        Envelope b3 = new Envelope(-10, -20, 130, 110);
        Envelope expected3 = new Envelope(0, 0, 100, 100);
        Envelope received3 = b0.intersect(b3);
        assertEquals(expected3, received3);

        Envelope b4 = new Envelope(-10, -10, -1, -1);
        Envelope received4 = b0.intersect(b4);
        assertEquals(Envelope.EMPTY, received4);

        Envelope b5 = new Envelope(110, 105, 120, 130);
        Envelope received5 = b0.intersect(b5);
        assertEquals(Envelope.EMPTY, received5);

        Envelope b6 = new Envelope(-10, 10, 50, 110);
        Envelope expected6 = new Envelope(0, 10, 50, 100);
        Envelope received6 = b0.intersect(b6);
        assertEquals(expected6, received6);

        Envelope b7 = new Envelope(10, 110, 90, 115);
        Envelope received7 = b0.intersect(b7);
        assertEquals(Envelope.EMPTY, received7);

        Envelope b8 = new Envelope(10, 20, 50, 60);
        Envelope received8 = b0.intersect(b8);
        assertEquals(b8, received8);

        Envelope b9 = new Envelope(110, 20, 115, 60);
        Envelope received9 = b0.intersect(b9);
        assertEquals(Envelope.EMPTY, received9);
    }

    @Test
    public void test_contains() {
        Envelope box = new Envelope(0, 0, 100, 100);
        Point p = Points.create2D(0, 0);
        assertTrue(box.contains(p));

        p = Points.create2D(100, 100);
        assertTrue(box.contains(p));

        p = Points.create2D(10, 20);
        assertTrue(box.contains(p));

        p = Points.create2D(-10, 0);
        assertFalse(box.contains(p));

        p = Points.create2D(0, -10);
        assertFalse(box.contains(p));

        p = Points.create2D(110, 0);
        assertFalse(box.contains(p));

        p = Points.create2D(0, 110);
        assertFalse(box.contains(p));

        p = Points.create2D(110, 110);
        assertFalse(box.contains(p));

    }
}
