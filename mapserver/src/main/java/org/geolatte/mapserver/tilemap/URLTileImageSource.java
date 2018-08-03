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


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class URLTileImageSource implements TileImageSource {

    private final URL url;
    private URLConnection connection;
    private InputStream inStream;


    private URLTileImageSource(URL url) {
        this.url = url;
    }

    public static URLTileImageSource create(URL url) {
        return new URLTileImageSource(url);
    }

    public InputStream toInputStream() throws IOException {
        connection = url.openConnection();
        inStream = connection.getInputStream();
        return inStream;
    }

    public String toString() {
        return url.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        URLTileImageSource that = (URLTileImageSource) o;
        String urlStr = url.toString();
        if (urlStr != null ?
                !urlStr.equals(that.url.toString()) :
                urlStr != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return url.toString() != null ? url.toString().hashCode() : 0;
    }
}
