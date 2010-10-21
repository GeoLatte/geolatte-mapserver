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

/**
 * A Chrono class to time several events.
 * <p/>
 * <p/>
 * Date: Nov 23, 2009
 */
public class Chrono {

    private long millis;
    private long start;

    public Chrono() {
        millis = System.currentTimeMillis();
        start = millis;
    }

    /**
     * Returns the time (ms.) between this invocation and the last.
     *
     * @return time in ms. since last stop, reset or creation of this chrono.
     */
    public long stop() {
        long now = System.currentTimeMillis();
        long retval = now - millis;
        millis = now;
        return retval;
    }

    public long total() {
        long now = System.currentTimeMillis();
        long retval = now - start;
        return retval;
    }

    /**
     * Resets the chrono
     *
     * @return
     */
    public void reset() {
        millis = System.currentTimeMillis();
    }

}
