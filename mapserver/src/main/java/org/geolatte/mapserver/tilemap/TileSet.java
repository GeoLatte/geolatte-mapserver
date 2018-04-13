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
import org.geolatte.geom.Point;
import org.geolatte.mapserver.util.PixelRange;


/**
 * Represents a tile set in a {@link TileMap}.
 * <p/>
 * <p> A <code>TileSet</code> consists of a grid of {@link Tile}s that collectively
 * cover the bounding box of the {@link TileMap} and have the same resolution (expressed as
 * units per pixel).
 * </p>
 */
public class TileSet {

    final String href;
    final private int order;
    final private TileSetCoordinateSpace tileSetCoordinateSpace;

    TileSet(String href, int order, TileSetCoordinateSpace cs) {
        this.href = href;
        this.order = order;
        this.tileSetCoordinateSpace = cs;
    }

    public String toString() {
        return this.href;
    }

    /**
     * Returns the resolution of this <code>TileSet</code>.
     *
     * @return the resolution in units per pixel.
     */
    public double unitsPerPixel() {
        return this.tileSetCoordinateSpace.unitsPerPixel();
    }

    /**
     * Returns the {@link TileSetCoordinateSpace} associated
     * with this <code>TileSet</code>.
     *
     * @return this <code>TileSet</code>'s <code>TileSetCoordinateSpace</code>.
     */
    public TileSetCoordinateSpace getTileCoordinateSpace() {
        return tileSetCoordinateSpace;
    }

    /**
     * Returns the tileindex for the tile that contains the point.
     *
     * @param point
     * @param lowerLeftInclusive if set, points on the bottom or left border of a tile counts as enclosed by that tile
     * @return The <code>TileIndex</code> that encloses this point.
     */
    public TileCoordinate pointIndex(Point<C2D> point, boolean lowerLeftInclusive) {
        return tileSetCoordinateSpace.tileCoordinateContaining(point, lowerLeftInclusive);
    }

    public PixelRange pixelBounds(Envelope<C2D> bbox) {
        return tileSetCoordinateSpace.pixelRange(bbox);
    }

    public int getOrder() {
        return this.order;
    }

}
