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

package org.geolatte.mapserver.tms;

import org.geolatte.mapserver.img.Imaging;

import java.util.concurrent.Callable;

public class TileImageReaderTask implements Callable<TileImage> {

    private final Tile tile;
    private final Imaging imageTool;
    private boolean forceArgb;

    public TileImageReaderTask(Tile tile, Imaging imageTool, boolean forceArgb) {
        this.tile = tile;
        this.imageTool = imageTool;
        this.forceArgb = forceArgb;
    }

    public TileImage call() throws Exception {
        return tile.getImage(imageTool,forceArgb);
    }
}
