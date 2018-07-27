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
import java.io.OutputStream;

/**
 * A source for a <code>TileImage</code>.
 *
 * @author Karel Maesen, Geovise BVBA
 */
public interface TileImageSource {

    /**
     * Opens an <code>InputStream</code> for reading the <code>TileImage</code>
     *
     * @return an <code>InputStream</code> that provides access to a </code>TileImage</code>
     * @throws IOException
     */
    public abstract InputStream toInputStream() throws IOException;

    /**
     * Open
     * @return
     * @throws IOException
     */
    default OutputStream toOutputStream() throws  IOException {
        throw new UnsupportedOperationException();
    }

}
