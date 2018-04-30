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


import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.mapserver.spi.Imaging;
import org.geolatte.mapserver.util.PixelRange;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class Tile {

    final private TileCoordinate coordinate;
    final private TileImageSource source;
    final private TileSetCoordinateSpace tileSetCoordinateSpace;


    public Tile(TileImageSource source, TileCoordinate coordinate, TileSetCoordinateSpace tileSetCoordinateSpace) {
        this.source = source;
        this.coordinate = coordinate;
        this.tileSetCoordinateSpace = tileSetCoordinateSpace;
    }

    static PixelRange pixelBounds(java.util.List<Tile> tiles) {
        if (tiles.isEmpty()) return new PixelRange(0, 0, new Dimension(0, 0));
        PixelRange boundsTile = null;
        for (Tile tile : tiles) {
            PixelRange b = tile.getPixelBounds();
            boundsTile = PixelRange.union(boundsTile, b);
        }
        return boundsTile;
    }

    static Envelope<C2D> boundingBox(Set<Tile> tiles) {
        if (tiles.isEmpty()) return new Envelope<C2D>(0, 0, 0, 0, null);
        Envelope<C2D> bboxUnion = null;
        for (Tile tile : tiles) {
            Envelope<C2D> bbox = tile.getBoundingBox();
            bboxUnion = bboxUnion.union(bbox);
        }
        return bboxUnion;
    }

    /**
     * Returns the <code>TileImage</code> for this tile.
     *
     * @param imaging the <code>Imaging</code> implementation to use for decoding the image
     * @return
     */
    public TileImage getImage(Imaging imaging, boolean forceArgb) {
        InputStream is = null;
        PixelRange pb = getPixelBounds();
        try {
            is = source.open();
            return imaging.read(is, pb.getMinX(), pb.getMinY(), forceArgb);
        } catch (Throwable e) {
            // no tile found, return an empty tile
            // return img.empty(pb.getWidth(), pb.getHeight());
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // nothing to do;
                }
            }
        }
    }

    PixelRange getPixelBounds() {
        return tileSetCoordinateSpace.tilePixelRange(this.coordinate);
    }


    Envelope<C2D> getBoundingBox() {
        return tileSetCoordinateSpace.boundingBox(this.coordinate);
    }

    @Override
    public String toString() {
        return source.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        if (source != null ? !source.equals(tile.source) : tile.source != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return source != null ? source.hashCode() : 0;
    }
}
