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

/**
 * Date: Oct 28, 2009
 */
public class BoundingBox {

    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;

    //TODO -- boundingbox should have a reference to a SRS 

    public BoundingBox(Point lowerLeft, Point upperRight) {
        this(lowerLeft.x, lowerLeft.y, upperRight.x, upperRight.y);
    }

    public BoundingBox(double minX, double minY, double maxX, double maxY) {
        if (minX > maxX || minY > maxY)
            throw new IllegalArgumentException("Valid Bounding boxes require minX <= maxX and minY <= maxY");
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getWidth() {
        return maxX - minX;
    }

    public double getHeight() {
        return maxY - minY;
    }

    public Point lowerLeft() {
        return new Point(minX, minY);
    }

    public Point upperRight() {
        return new Point(maxX, maxY);
    }

    public Point upperLeft() {
        return new Point(minX, maxY);
    }

    public Point lowerRight() {
        return new Point(maxX, minY);
    }


    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LL: ")
                .append(minX)
                .append(",")
                .append(minY)
                .append(" - UR: ")
                .append(maxX)
                .append(",")
                .append(maxY);
        return builder.toString();
    }

    public static BoundingBox union(BoundingBox b1, BoundingBox b2) {
        if (b1 == null) return b2;
        if (b2 == null) return b1;
        double minX = Math.min(b1.getMinX(), b2.getMinX());
        double minY = Math.min(b1.getMinY(), b2.getMinY());
        double maxX = Math.max(b1.getMaxX(), b2.getMaxX());
        double maxY = Math.max(b1.getMaxY(), b2.getMaxY());
        return new BoundingBox(minX, minY, maxX, maxY);
    }

    /**
     * Intersects the specified BoundingBox with this BoundingBox and returns the result.
     *
     * @param bbox the BoundingBox to intersect.
     * @return
     */
    public BoundingBox intersect(BoundingBox bbox) {
        double minX = Math.max(bbox.getMinX(), getMinX());
        double minY = Math.max(bbox.getMinY(), getMinY());
        double maxX = Math.min(bbox.getMaxX(), getMaxX());
        double maxY = Math.min(bbox.getMaxY(), getMaxY());
        if (minX > maxX || minY > maxY)
            return new BoundingBox(0, 0, 0, 0);

        return new BoundingBox(minX, minY, maxX, maxY);

    }

    public boolean isEmpty() {
        return getWidth() == 0 || getHeight() == 0;
    }

    /**
     * Checks whether this BoundingBox falls within the specified BoundingBox
     *
     * @param bbox
     */
    public boolean isWithin(BoundingBox bbox) {
        return bbox.getMinX() <= this.getMinX() &&
                bbox.getMaxX() >= this.getMaxX() &&
                bbox.getMinY() <= this.getMinY() &&
                bbox.getMaxY() >= this.getMaxY();
    }

    public boolean contains(Point p) {
        return getMinX() <= p.x && getMaxX() >= p.x && getMinY() <= p.y && getMaxY() >= p.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoundingBox that = (BoundingBox) o;

        if (Double.compare(that.maxX, maxX) != 0) return false;
        if (Double.compare(that.maxY, maxY) != 0) return false;
        if (Double.compare(that.minX, minX) != 0) return false;
        if (Double.compare(that.minY, minY) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = minX != +0.0d ? Double.doubleToLongBits(minX) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = maxX != +0.0d ? Double.doubleToLongBits(maxX) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = minY != +0.0d ? Double.doubleToLongBits(minY) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = maxY != +0.0d ? Double.doubleToLongBits(maxY) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

}
