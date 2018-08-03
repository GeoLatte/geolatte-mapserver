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

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A <code>TileImageSourceFactory</code> that creates URL-based <code>TileImageSource</code>s.
 *
 * @author Karel Maesen
 * Date: Nov 12, 2009
 */
public class URLTileImageSourceFactory implements TileImageSourceFactory {

    /**
     * Creates a <code>TileImageSource</code> for the tile.
     *
     * @param set        the <code>TileSet</code> to which the tile belongs
     * @param coordinate the <code>TileCoordinate</code> of the tile
     * @param extension  the extension of the associated image file.
     * @return the <code>TileImageSource</code> for the specified tile
     * @throws MalformedURLException in case the URL property of the set is not a valid URL.
     */
    public TileImageSource create(TileSet set, TileCoordinate coordinate, String extension) {
        String urlStr = set.href + "/" + coordinate.i + "/" + coordinate.j + "." + extension;
        try {
            URL url = new URL(urlStr);
            return URLTileImageSource.create(url);
        } catch (MalformedURLException e) {
            //TODO -- change by proper exception.
            throw new RuntimeException(e);
        }
    }
}
