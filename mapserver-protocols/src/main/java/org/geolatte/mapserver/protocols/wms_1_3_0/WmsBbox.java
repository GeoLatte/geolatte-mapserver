package org.geolatte.mapserver.protocols.wms_1_3_0;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Position;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.geom.crs.CrsRegistry;

import java.util.Objects;

import static java.lang.String.format;

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

    Envelope<C2D> toEnvelope(String srs) {
        CrsId crsId = CrsId.parse(srs);
        if(! crsId.getAuthority().equalsIgnoreCase("EPSG") ) {
            throw new RuntimeException(format("Only EPSG CRS strings are supported (%s)", srs));
        }
        CoordinateReferenceSystem<?> crs = CrsRegistry.getCoordinateReferenceSystemForEPSG(crsId.getCode(), CoordinateReferenceSystems.PROJECTED_2D_METER);
        if (C2D.class.isAssignableFrom(crs.getPositionClass())) {
            return new Envelope<>(minX, minY, maxX, maxY, (CoordinateReferenceSystem<C2D>)crs);
        }  else {
            return new Envelope<>(minX, minY, maxX, maxY, CoordinateReferenceSystems.WEB_MERCATOR);
        }

    }
}
