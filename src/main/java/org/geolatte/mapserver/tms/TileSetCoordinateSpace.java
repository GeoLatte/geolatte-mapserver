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

import org.geolatte.mapserver.util.BoundingBox;
import org.geolatte.mapserver.util.Pixel;
import org.geolatte.mapserver.util.PixelRange;
import org.geolatte.mapserver.util.Point;

import java.awt.*;

public class TileSetCoordinateSpace {

    private final Point origin;
    private final Dimension tileDimension;
    private final BoundingBox extent;
    private final double unitsPerPixel;
    private final MapUnitToPixelTransform mupTransform;


    TileSetCoordinateSpace(Point origin, Dimension tileDimension, BoundingBox extent, double unitsPerPixel) {
        this.origin = origin;
        this.tileDimension = tileDimension;
        this.unitsPerPixel = unitsPerPixel;
        this.extent = extent;
        this.mupTransform = new MapUnitToPixelTransform(this.extent, this.unitsPerPixel);
    }

    public BoundingBox boundingBox(TileCoordinate coordinate) {
        double width = tileDimension.getWidth() * unitsPerPixel;
        double height = tileDimension.getHeight() * unitsPerPixel;
        double x = origin.x + (coordinate.i * width);
        double y = origin.y + (coordinate.j * height);
        BoundingBox result = new BoundingBox(x, y, x + width, y + height);
//        if (!result.isWithin(extent))
//            throw new IllegalArgumentException("Specified TileCoordinate falls outside of TileSet extent.");
        return result;
    }

    public PixelRange tilePixelRange(TileCoordinate coordinate) {
        BoundingBox bbox = boundingBox(coordinate);
        return this.mupTransform.toPixelRange(bbox);
    }

    public Dimension tileSetPixelDimension() {
        int h = (int) (extent.getHeight() / unitsPerPixel);
        int w = (int) (extent.getWidth() / unitsPerPixel);
        return new Dimension(w, h);
    }

    public double tileWidthInMapUnits() {
        Dimension dim = tileDimension;
        return unitsPerPixel * dim.getWidth();
    }

    public double tileHeightInMapUnits() {
        Dimension dim = tileDimension;
        return unitsPerPixel * dim.getHeight();
    }

    /**
     * Returns the <code>TileCoordinate</code> for the tile that contains the point.
     *
     * @param point
     * @param lowerLeftInclusive if set, points on the bottom or left border of a tile count as enclosed by that tile
     * @return The <code>TileIndex</code> that encloses this point.
     */
    public TileCoordinate tileCoordinateContaining(Point point, boolean lowerLeftInclusive) {
        if (!this.extent.contains(point))
            throw new IllegalArgumentException(String.format("Point %s outside the extent of this TileSet", point.toString()));
        Point relativeToOrigin = relativeToOrigin(point);
        double x = relativeToOrigin.x;
        double y = relativeToOrigin.y;
        double width = tileWidthInMapUnits();
        double height = tileHeightInMapUnits();
        int i = (int) (x / width);
        int j = (int) (y / height);
        if (!lowerLeftInclusive && (x % width == 0)) i -= 1;
        if (!lowerLeftInclusive && (y % width == 0)) j -= 1;
        return new TileCoordinate(i, j);
    }

    public double unitsPerPixel() {
        return this.unitsPerPixel;
    }

    public PixelRange pixelRange(BoundingBox bbox) {
        return this.mupTransform.toPixelRange(bbox);
    }

    public Pixel toPixel(Point point) {
        return this.mupTransform.toPixel(point);
    }

    public Point toPoint(Pixel pixel) {
        return this.mupTransform.toPoint(pixel);
    }

    private Point relativeToOrigin(Point point) {
        return new Point(point.x - origin.x, point.y - origin.y);
    }
}
