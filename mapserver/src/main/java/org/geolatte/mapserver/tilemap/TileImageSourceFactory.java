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

/**
 * Creates <code>TileImageSource</code>s for reading <code>TileImage</code>s
 */
public interface TileImageSourceFactory {


    //TODO :: remove the extension parameter from the interface

    /**
     * Creates the <code>TileImageSource</code> for the specified <code>Tile</code>
     *
     * @param set        the <code>TileSet</code> containing the <code>tile</code>
     * @param coordinate the <code>Tilecoordinate</code> for the <code>Tile</code>
     * @param extension  the expected extension of the image file
     * @return a <code>TileImageSource</code> for the <code>Tile</code> specified by the set and coordinate parameters
     */
    TileImageSource create(TileSet set, TileCoordinate coordinate, String extension);
}
