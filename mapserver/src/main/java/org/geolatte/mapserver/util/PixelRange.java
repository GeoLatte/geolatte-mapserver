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

package org.geolatte.mapserver.util;

import org.python.modules.jffi.CType;

import java.awt.*;

/**
 * Specifies a Rectangular subregion of the raster image.
 */
public class PixelRange {
    final private Dimension dimension;
    final private int minX;
    final private int minY;

    public PixelRange(int minX, int minY, int width, int height) {
        this(minX, minY, new Dimension(width, height));
    }

    public PixelRange(Pixel origin, Dimension dimension) {
        this(origin.x, origin.y, dimension);
    }

    public PixelRange(int minX, int minY, Dimension dimension) {
        this.minX = minX;
        this.minY = minY;
        this.dimension = new Dimension(dimension);
    }

    // for working with transform image coordiantes
    public static PixelRange fromArray(double[] arr){
        int minX = (int)Math.floor( Math.min(arr[0], arr[2]));
        int minY = (int) Math.floor(Math.min(arr[1], arr[3]));
        int maxX = (int) Math.ceil(Math.max(arr[0], arr[2]));
        int maxY = (int) Math.ceil(Math.max(arr[1], arr[3]));
        return new PixelRange(minX, minY, maxX - minX, maxY - minY);
    }

    public static PixelRange union(PixelRange b1, PixelRange b2) {
        if (b1 == null) return b2;
        if (b2 == null) return b1;
        int minX = Math.min(b1.getMinX(), b2.getMinX());
        int minY = Math.min(b1.getMinY(), b2.getMinY());
        int maxX = Math.max(b1.getMaxX(), b2.getMaxX());
        int maxY = Math.max(b1.getMaxY(), b2.getMaxY());
        Dimension dim = new Dimension(maxX - minX + 1, maxY - minY + 1);
        return new PixelRange(minX, minY, dim);
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getWidth() {
        return dimension.width;
    }

    public int getHeight() {
        return dimension.height;
    }

    public int getMaxX() {
        return minX + getWidth() - 1;
    }

    public int getMaxY() {
        return minY + getHeight() - 1;
    }

    /**
     * Tests whether this <code>PixelRange</code> falls completely
     * within the specified <code>PixelRange</code>.
     *
     * @param bounds
     * @return
     */
    public boolean within(PixelRange bounds) {
        return this.getMinX() >= bounds.getMinX() &&
                this.getMaxX() <= bounds.getMaxX() &&
                this.getMinY() >= bounds.getMinY() &&
                this.getMaxY() <= bounds.getMaxY();
    }

    public boolean contains(Pixel pixel) {
        return this.getMinX() <= pixel.x &&
                this.getMaxX() >= pixel.x &&
                this.getMinY() <= pixel.y &&
                this.getMaxY() >= pixel.y;
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("MinX=")
                .append(this.getMinX())
                .append(",MinY=")
                .append(this.getMinY())
                .append(",MaxX=")
                .append(this.getMaxX())
                .append(", MaxY=")
                .append(this.getMaxY());
        return b.toString();
    }

    public Dimension getDimension() {
        return new Dimension(getWidth(), getHeight());
    }

    public double[] toArray(double[] dst){
        if(dst == null)
            return new double[]{getMinX(), getMinY(), getMaxX(), getMaxY()};
        dst[0] = getMinX();
        dst[1] = getMinY();
        dst[2] = getMaxX();
        dst[3] = getMaxY();
        return dst;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PixelRange that = (PixelRange) o;

        if (minX != that.minX) return false;
        if (minY != that.minY) return false;
        if (dimension != null ? !dimension.equals(that.dimension) : that.dimension != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dimension != null ? dimension.hashCode() : 0;
        result = 31 * result + minX;
        result = 31 * result + minY;
        return result;
    }

    public Rectangle toRect() {
        return new Rectangle(this.minX, this.minY, getWidth(), getHeight());
    }
}
