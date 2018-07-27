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

import java.io.*;

import static java.lang.String.format;

/**
 * A <code>TileImageSource</code> that retrieves the <code>TileImage</code>
 * from a file
 */
public class FileTileImageSource implements TileImageSource {

    private final File src;

    FileTileImageSource(File tileImageFile) {
        src = tileImageFile;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream toInputStream() throws IOException {
        return new BufferedInputStream(new FileInputStream(src));
    }

    @Override
    public OutputStream toOutputStream() throws IOException {
        ensureParentDirs();
        return new BufferedOutputStream(new FileOutputStream(src));
    }

    private void ensureParentDirs() {
        if(!src.getParentFile().exists()) {
            createParentDirs();
        }

    }

    private void createParentDirs() {
        boolean success = src.getParentFile().mkdirs();
        if(!success){
            throw new RuntimeException(format("Failed to write parent directories for tile %s", src.getAbsolutePath()));
        }
    }
}
