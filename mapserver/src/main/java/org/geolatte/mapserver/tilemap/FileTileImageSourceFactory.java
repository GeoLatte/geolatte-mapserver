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

import java.io.File;

/**
 * Creates <code>TileImageSource</code>s that read from the file system.
 */
public class FileTileImageSourceFactory implements TileImageSourceFactory {

    /**
     * {@inheritDoc}
     */
    public TileImageSource create(TileSet set, TileCoordinate coordinate, String extension) {
        File tileSetDirectory = new File(set.href);
        File tileImageFile = pathToTile(tileSetDirectory, coordinate, extension);
        return new FileTileImageSource(tileImageFile);
    }

    private File pathToTile(File parent, TileCoordinate coordinate, String extension) {
        File xDir = new File(parent, String.valueOf(coordinate.i));
        return new File(xDir, String.valueOf(coordinate.j) + "." + extension);
    }
}
