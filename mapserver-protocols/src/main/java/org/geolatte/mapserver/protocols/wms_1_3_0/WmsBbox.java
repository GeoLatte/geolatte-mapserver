package org.geolatte.mapserver.protocols.wms_1_3_0;

import java.util.Objects;

/**
 * Created by Karel Maesen, Geovise BVBA on 10/05/2018.
 */
public class WmsBbox {

    private final double minX;
    private final double minY;
    private final double maxX;
    private final double maxY;

    public WmsBbox(double minX, double minY, double maxX, double maxY) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WmsBbox wmsBbox = (WmsBbox) o;
        return Double.compare(wmsBbox.minX, minX) == 0 &&
                Double.compare(wmsBbox.minY, minY) == 0 &&
                Double.compare(wmsBbox.maxX, maxX) == 0 &&
                Double.compare(wmsBbox.maxY, maxY) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(minX, minY, maxX, maxY);
    }
}
