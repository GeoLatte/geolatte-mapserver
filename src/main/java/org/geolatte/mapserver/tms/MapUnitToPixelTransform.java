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

import org.geolatte.geom.Envelope;
import org.geolatte.geom.Point;
import org.geolatte.geom.Points;
import org.geolatte.mapserver.util.Pixel;
import org.geolatte.mapserver.util.PixelRange;

import java.awt.geom.AffineTransform;

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
 *         creation-date: Jul 8, 2010
 */
public class MapUnitToPixelTransform {
    private final Envelope extent;
    private final double mapUnitsPerPixelX;
    private final double mapUnitsPerPixelY;
    private final PixelRange pixelRange;

    /**
     * Constructor
     * @param extent the maximum extent in map units
     * @param pixelRange the maximum extent in pixel coordinates
     */
    public MapUnitToPixelTransform(Envelope extent, PixelRange pixelRange) {
        this.extent = extent;
        this.mapUnitsPerPixelX = extent.getWidth() / pixelRange.getWidth();
        this.mapUnitsPerPixelY = extent.getHeight() / pixelRange.getHeight();
        this.pixelRange = pixelRange;
    }

    /**
     * Constructor
     * @param extent the maximum extent in map units
     * @param minPixelX the minimum pixel coordinate in the X-axis
     * @param minPixelY the minimum pixel coordinate in the Y-axis
     * @param mapUnitsPerPixel the map units per pixel for the <code>PixelRange</code>
     */
    public MapUnitToPixelTransform(Envelope extent, int minPixelX, int minPixelY, double mapUnitsPerPixel) {
        this.extent = extent;
        this.mapUnitsPerPixelX = mapUnitsPerPixel;
        this.mapUnitsPerPixelY = mapUnitsPerPixel;
        int pixelRangeWidth = (int) Math.ceil(this.extent.getWidth() / mapUnitsPerPixel);
        int pixelRangeHeight = (int) Math.ceil(this.extent.getHeight() / mapUnitsPerPixel);
        this.pixelRange = new PixelRange(minPixelX, minPixelY, pixelRangeWidth, pixelRangeHeight);
    }

    /**
     * Constructor
     * @param extent extent the maximum extent in map units
     * @param mapUnitsPerPixel  the map units per pixel for the <code>PixelRange</code>
     */
    public MapUnitToPixelTransform(Envelope extent, double mapUnitsPerPixel) {
        this(extent, 0, 0, mapUnitsPerPixel);
    }

    /**
     * Returns the range of the transform.
     *
     * @return
     */
    public PixelRange getRange(){
        return this.pixelRange;
    }

    /**
     * Returns the domain of the transform.
     *
     * @return
     */
    public Envelope getDomain(){
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
    public Point toPoint(Pixel pixel) {
        double x = extent.getMinX() + mapUnitsPerPixelX * (pixel.x - this.pixelRange.getMinX());
        double y = extent.getMaxY() - mapUnitsPerPixelY * (pixel.y - this.pixelRange.getMinY());
        return Points.create2D(x, y, extent.getCrsId());
    }


    /**
     * Maps a point to a pixel in this instance's <code>PixelRange</code>
     *
     * <p>If the point falls on the boundary between two pixels it is mapped to the pixel on the right and/or below the
     * boundary</p>
     * @param point
     * @return
     */
    public Pixel toPixel(Point point) {
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
     * @param point the <code>Point</code> to map
     * @param leftBorderInclusive if true, points that map to the left-boundary of a pixel are mapped to that pixel
     * @param lowerBorderInclusive if true, points that map to the lower-boundary of a pixel are mapped to that pixel
     * @return
     */
    public Pixel toPixel(Point point, boolean leftBorderInclusive, boolean lowerBorderInclusive) {
        double xOffset = (point.getX() - extent.getMinX());
        double yOffset = (extent.getMaxY() - point.getY());
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
        if(Math.abs(Math.round(x) - x) < 1e-3)
            return Math.round(x);
        return x;
    }

    /**
     * The <code>PixelRange</code> that corresponds to the specified <code>BoundingBox</code>
     * @param bbox
     * @return
     */
    public PixelRange toPixelRange(Envelope bbox) {
        Pixel ulPx = toPixel(bbox.upperLeft(), false, false);
        Pixel lrPx = toPixel(bbox.lowerRight(), true, true);
        int minX = ulPx.x;
        int minY = ulPx.y;
        int width = lrPx.x - ulPx.x + 1;
        int height = lrPx.y - ulPx.y + 1;
        return new PixelRange(minX, minY, width, height);
    }

    //TODO add unit tests
    //TODO -- refactor to remove code duplication with toPixel
    public AffineTransform toAffineTransform() {
        double m00 = 1.0/mapUnitsPerPixelX;
        double m01 = 0;
        double m02 = pixelRange.getMinX() - extent.getMinX()/mapUnitsPerPixelX;
        double m10 = 0;
        double m11 = -1.0/mapUnitsPerPixelY;
        double m12 = pixelRange.getMinY() + extent.getMaxY()/mapUnitsPerPixelY;
        return new AffineTransform(m00, m10, m01, m11, m02, m12);
    }
}
