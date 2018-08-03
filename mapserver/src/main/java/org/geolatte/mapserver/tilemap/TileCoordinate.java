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

import java.util.ArrayList;
import java.util.List;

public class TileCoordinate {

    final public int i, j;

    TileCoordinate(int i, int j) {
        this.i = i;
        this.j = j;
    }


    public static TileCoordinate valueOf(int i, int j) {
        return new TileCoordinate(i, j);
    }

    public static List<TileCoordinate> range(TileCoordinate start, TileCoordinate stop) {
        List<TileCoordinate> list = new ArrayList<TileCoordinate>();
        for (int i = getMinRow(start, stop); i <= getMaxRow(start, stop); i++) {
            addCoordinatesForRow(i, getMinCol(start, stop), getMaxCol(start, stop), list);
        }
        return list;
    }

    private static int getMaxCol(TileCoordinate start, TileCoordinate stop) {
        return Math.max(start.j, stop.j);
    }

    private static int getMinCol(TileCoordinate start, TileCoordinate stop) {
        return Math.min(start.j, stop.j);
    }


    private static int getMaxRow(TileCoordinate start, TileCoordinate stop) {
        return Math.max(start.i, stop.i);
    }

    private static int getMinRow(TileCoordinate start, TileCoordinate stop) {
        return Math.min(start.i, stop.i);
    }

    private static void addCoordinatesForRow(int i, int startCol, int stopCol, List<TileCoordinate> list) {
        for (int j = startCol; j <= stopCol; j++) {
            list.add(new TileCoordinate(i, j));
        }
    }

    public String toString() {
        return "(" + i + "," + j + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TileCoordinate tileCoordinate = (TileCoordinate) o;

        if (i != tileCoordinate.i) return false;
        if (j != tileCoordinate.j) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = i;
        result = 31 * result + j;
        return result;
    }
}
