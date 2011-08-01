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

package org.geolatte.mapserver.referencing;

import org.geolatte.mapserver.tms.MapUnitToPixelTransform;
import org.geolatte.mapserver.util.BoundingBox;
import org.geolatte.mapserver.util.SRS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.geotools.referencing.operation.transform.WarpBuilder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.*;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import javax.media.jai.Warp;
import java.awt.*;
import java.awt.geom.*;

/**
 * Transforms bounding boxes to Lat/Lon
 *
 * @author Karel Maesen, Geovise BVBA
 */
public class Referencing {

    private final static CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
    private final static SRS GOOGLE_SRS_CODE = new SRS("EPSG", 900913);
    private final static String GOOGLE_WKT = "PROJCS[\"Google Mercator\", "
            + "GEOGCS[\"WGS 84\", "
            + "DATUM[\"World Geodetic System 1984\", "
            + "SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], "
            + "AUTHORITY[\"EPSG\",\"6326\"]], "
            + "PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], "
            + "UNIT[\"degree\", 0.017453292519943295], "
            + "AXIS[\"Geodetic latitude\", NORTH], "
            + "AXIS[\"Geodetic longitude\", EAST], "
            + "AUTHORITY[\"EPSG\",\"4326\"]],  "
            + "PROJECTION[\"Mercator (1SP)\", AUTHORITY[\"EPSG\",\"9804\"]], "
            + "PARAMETER[\"semi_major\", 6378137.0], "
            + "PARAMETER[\"semi_minor\", 6378137.0], "
            + "PARAMETER[\"latitude_of_origin\", 0.0], "
            + "PARAMETER[\"central_meridian\", 0.0], "
            + "PARAMETER[\"scale_factor\", 1.0],  "
            + "PARAMETER[\"false_easting\", 0.0],  "
            + "PARAMETER[\"false_northing\", 0.0],  "
            + "UNIT[\"m\", 1.0],  "
            + "AXIS[\"Northing\", NORTH], "
            + "AXIS[\"Easting\", EAST],  "
            + "AUTHORITY[\"EPSG\",\"900913\"]]";


    private static CoordinateReferenceSystem GOOGLE_CRS;
    private static final double TOLERANCE = 0.333d;

    static {
        try {
            GOOGLE_CRS = CRS.parseWKT(GOOGLE_WKT);
        } catch (FactoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Transforms the specified <code>BoundingBox</code> to Lat/Lon, assuming that
     * its coordinates are currently expressed in the specified coordinate reference system.
     *
     * @param srs  specified coordinate reference system
     * @param bbox bounding box to transform
     * @return bounding box in Lat/Lon coordinates (EPSG:4326)
     */
    public static BoundingBox transformToLatLong(BoundingBox bbox, SRS srs) {
        SRS target = SRS.parse("EPSG:4326");
        return transform(bbox, srs, target);
    }


    public static BoundingBox transform(BoundingBox bbox, SRS srcSRS, SRS targetSRS) {
        MathTransform mtf = createMathTransform(srcSRS, targetSRS);
        return transform(mtf, bbox);
    }

    public static MathTransform createMathTransform(SRS srs, SRS target) {
        try {
            CoordinateReferenceSystem sourceCRS = create(srs);
            CoordinateReferenceSystem targetCRS = create(target);
            return CRS.findMathTransform(sourceCRS, targetCRS);
        }catch (FactoryException e) {
            throw new RuntimeException(e);
        }
    }

    private static BoundingBox transform(MathTransform transform, BoundingBox sourceBbox) {
        double[] source = new double[8];
        double[] target = new double[8];
        try {
            source[0] = sourceBbox.upperLeft().x;
            source[1] = sourceBbox.upperLeft().y;
            source[2] = sourceBbox.lowerLeft().x;
            source[3] = sourceBbox.lowerLeft().y;
            source[4] = sourceBbox.upperRight().x;
            source[5] = sourceBbox.upperRight().y;
            source[6] = sourceBbox.lowerRight().x;
            source[7] = sourceBbox.lowerRight().y;

            transform.transform(source, 0, target, 0, 4);
            return new BoundingBox(
                    getMin(target[0], target[2], target[4], target[6]),
                    getMin(target[1], target[3], target[5], target[7]),
                    getMax(target[0], target[2], target[4], target[6]),
                    getMax(target[1], target[3], target[5], target[7])
                    );
        } catch (TransformException e) {
            throw new RuntimeException(e);
        }
    }

    private static double getMin(double... v) {
        double min = Double.POSITIVE_INFINITY;
        for (double c : v){
              min = Math.min(min, c);
        }
        return min;
    }

    private static double getMax(double... v) {
        double max = Double.NEGATIVE_INFINITY;
        for (double c : v){
              max = Math.max(max, c);
        }
        return max;
    }


    private static CoordinateReferenceSystem create(SRS srs) throws FactoryException {
        if (GOOGLE_SRS_CODE.equals(srs)) {
            return GOOGLE_CRS;
        }
        return factory.createCoordinateReferenceSystem(srs.toString());

    }

    private static CoordinateReferenceSystem create(String srsString) throws FactoryException {
        return factory.createCoordinateReferenceSystem(srsString);
    }

    /**
     * Derives an JAI Warp that approximates the transform from to Target SRS.
     *
     * @param mupSrcTransform
     * @param sourceSRS
     * @param targetSRS
     * @param mupTargetTransform
     * @param tolerance
     * @return
     * @throws ReferencingException
     */
    public static Warp createWarpApproximation(MapUnitToPixelTransform mupSrcTransform, SRS sourceSRS, SRS targetSRS, MapUnitToPixelTransform mupTargetTransform, double tolerance) throws ReferencingException {

        MathTransformFactory mtFactory = ReferencingFactoryFinder.getMathTransformFactory(null);

        MathTransform srcToTargetSRS = Referencing.createMathTransform(sourceSRS, targetSRS);
        MathTransform targetToSrc = null;
        try {
            AffineTransform pixelToMapUnitTransform = mupSrcTransform.toAffineTransform().createInverse();
            AffineTransform targetM2P = mupTargetTransform.toAffineTransform();
            MathTransform srcToTarget = mtFactory.createConcatenatedTransform(new AffineTransform2D(pixelToMapUnitTransform), srcToTargetSRS);
            srcToTarget = mtFactory.createConcatenatedTransform(srcToTarget, new AffineTransform2D(targetM2P));
            targetToSrc = srcToTarget.inverse();
        } catch (java.awt.geom.NoninvertibleTransformException e) {
            throw new ReferencingException("Can't inverse MapUnitToPixel transform.", e);
        } catch (FactoryException e) {
             throw new ReferencingException(e);
        } catch (NoninvertibleTransformException e) {
            throw new ReferencingException(e);
        }

        if (!(targetToSrc instanceof MathTransform2D)) {
            throw new ReferencingException("Require a 2D transformation");
        }
        //use the GeoTools WarpBuilder to build a good approximation
        WarpBuilder warpBuilder = new WarpBuilder(tolerance);
        try {
            return warpBuilder.buildWarp((MathTransform2D)targetToSrc, mupTargetTransform.getRange().toRect());
        } catch (TransformException e) {
            throw new ReferencingException(e);
        }

    }

}
