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
import org.geolatte.mapserver.util.Pixel;
import org.geolatte.mapserver.util.PixelRange;

import java.awt.geom.AffineTransform;

import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.mapserver.util.EnvelopUtils.height;
import static org.geolatte.mapserver.util.EnvelopUtils.width;

/**
 * Transforms between coordinates in map units and pixel-coordinates.
 * <p/>
 * <p>
 * A <code>MapUnitToPixelTransform</code> associates a <code>BoundingBox</code> in map units with a
 * <code>PixelRange</code>, such that the upper-left corner of the <code>BoundingBox</code> is mapped
 * to the pixel (minPixelX, minPixelY), and the bottom-right corner to (maxPixelX, maxPixelY).
 * </p>
 *
 * @author Karel Maesen, Geovise BVBA
 * creation-date: Jul 8, 2010
 */
public class MapUnitToPixelTransform {
    private final Envelope<C2D> extent;
    private final double mapUnitsPerPixelX;
    private final double mapUnitsPerPixelY;
    private final PixelRange pixelRange;

    /**
     * Constructor
     *
     * @param extent     the maximum extent in map units
     * @param pixelRange the maximum extent in pixel coordinates
     */
    public MapUnitToPixelTransform(Envelope<C2D> extent, PixelRange pixelRange) {
        this.extent = extent;
        this.mapUnitsPerPixelX = width(extent) / pixelRange.getWidth();
        this.mapUnitsPerPixelY = height(extent) / pixelRange.getHeight();
        this.pixelRange = pixelRange;
    }

    /**
     * Constructor
     *
     * @param extent           the maximum extent in map units
     * @param minPixelX        the minimum pixel coordinate in the X-axis
     * @param minPixelY        the minimum pixel coordinate in the Y-axis
     * @param mapUnitsPerPixel the map units per pixel for the <code>PixelRange</code>
     */
    public MapUnitToPixelTransform(Envelope<C2D> extent, int minPixelX, int minPixelY, double mapUnitsPerPixel) {
        this.extent = extent;
        this.mapUnitsPerPixelX = mapUnitsPerPixel;
        this.mapUnitsPerPixelY = mapUnitsPerPixel;
        int pixelRangeWidth = (int) Math.ceil(width(this.extent) / mapUnitsPerPixel);
        int pixelRangeHeight = (int) Math.ceil(height(this.extent) / mapUnitsPerPixel);
        this.pixelRange = new PixelRange(minPixelX, minPixelY, pixelRangeWidth, pixelRangeHeight);
    }

    /**
     * Constructor
     *
     * @param extent           extent the maximum extent in map units
     * @param mapUnitsPerPixel the map units per pixel for the <code>PixelRange</code>
     */
    public MapUnitToPixelTransform(Envelope<C2D> extent, double mapUnitsPerPixel) {
        this(extent, 0, 0, mapUnitsPerPixel);
    }

    /**
     * Returns the range of the transform.
     *
     * @return
     */
    public PixelRange getRange() {
        return this.pixelRange;
    }

    /**
     * Returns the domain of the transform.
     *
     * @return
     */
    public Envelope<C2D> getDomain() {
        return this.extent;
    }

    /**
     * Maps a pixel to a point in this instance's <code>BoundingBox</code>
     * <p/>
     * <p>The point corresponds exactly to the the upper-left corner of the pixel.</p>
     *
     * @param pixel
     * @return
     */
    public Point<C2D> toPoint(Pixel pixel) {
        double x = extent.lowerLeft().getX() + mapUnitsPerPixelX * (pixel.x - this.pixelRange.getMinX());
        double y = extent.upperRight().getY() -  mapUnitsPerPixelY * (pixel.y - this.pixelRange.getMinY());
        return point(extent.getCoordinateReferenceSystem(), c(x, y));
    }


    /**
     * Maps a point to a pixel in this instance's <code>PixelRange</code>
     *
     * <p>If the point falls on the boundary between two pixels it is mapped to the pixel on the right and/or below the
     * boundary</p>
     *
     * @param point
     * @return
     */
    public Pixel toPixel(C2D point) {
        if (point.equals(this.extent.upperRight())) {
            return toPixel(point, true, false);
        }
        if (point.equals(this.extent.upperLeft())) {
            return toPixel(point, false, false);
        }
        if (point.equals(this.extent.lowerLeft())) {
            return toPixel(point, false, true);
        }
        if (point.equals(this.extent.lowerRight())) {
            return toPixel(point, true, true);
        }
        return toPixel(point, false, false);
    }

    /**
     * Maps a point to a pixel in this instance's <code>PixelRange</code>
     *
     * <p>   </p>
     *
     * @param point                the <code>Point</code> to map
     * @param leftBorderInclusive  if true, points that map to the left-boundary of a pixel are mapped to that pixel
     * @param lowerBorderInclusive if true, points that map to the lower-boundary of a pixel are mapped to that pixel
     * @return
     */
    public Pixel toPixel(C2D point, boolean leftBorderInclusive, boolean lowerBorderInclusive) {
        double xOffset = (point.getX() - extent.lowerLeft().getX());
        double yOffset = (extent.upperRight().getY() - point.getY());
        double x = this.pixelRange.getMinX() + xOffset / mapUnitsPerPixelX;
        double y = this.pixelRange.getMinY() + yOffset / mapUnitsPerPixelY;
        x = removeRoundingError(x);
        y = removeRoundingError(y);
        int xPix = (leftBorderInclusive && x == Math.floor(x)) ? (int) (x - 1) : (int) x;
        int yPix = (lowerBorderInclusive && y == Math.floor(y)) ? (int) (y - 1) : (int) y;
        return new Pixel(xPix, yPix);
    }

    /**
     * In tilemaps where the origin lies on border of the map extend, a calculation is performed where terms can be
     * crossed out. Because of rounding errors (e.g. x = 160.999999 instead of 161) a map coordinate could be mapped
     * onto the wrong pixel. As a consequence the pixelBounds could be too large and a transparent border is added to
     * the tiles.
     *
     * @param x The pixelCoordinate to be rounded
     * @return Math.round(x) if x is close to an integer number.
     */
    private double removeRoundingError(double x) {
        if (Math.abs(Math.round(x) - x) < 1e-3)
            return Math.round(x);
        return x;
    }

    /**
     * The <code>PixelRange</code> that corresponds to the specified <code>BoundingBox</code>
     *
     * @param bbox
     * @return
     */
    public PixelRange toPixelRange(Envelope<C2D> bbox) {
        Pixel ulPx = toPixel(bbox.upperLeft(), false, false);
        Pixel lrPx = toPixel(bbox.lowerRight(), true, true);
        int minX = ulPx.x;
        int minY = ulPx.y;
        int width = lrPx.x - ulPx.x + 1;
        int height = lrPx.y - ulPx.y + 1;
        return new PixelRange(minX, minY, width, height);
    }

}
