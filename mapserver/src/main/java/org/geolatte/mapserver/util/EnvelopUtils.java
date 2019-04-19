package org.geolatte.mapserver.util;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Position;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

/**
 * Created by Karel Maesen, Geovise BVBA on 12/04/2018.
 */
public class EnvelopUtils {

    public static <P extends Position> double width(Envelope<P> env) {
        return env.upperRight().getCoordinate( 0 ) - env.lowerLeft().getCoordinate( 0 );
    }

    public static <P extends Position> double height(Envelope<P> env) {
        return env.upperRight().getCoordinate( 1 ) - env.lowerLeft().getCoordinate( 1 );
    }

    public static <P extends Position> Envelope<P> bufferRounded(Envelope<P> env, double factor) {

        if (factor < 1) {
            throw new IllegalArgumentException("Factor needs to be >= 1");
        }

        factor -= 1;

        double halfWidth = factor*width(env) / 2;
        double halfHeight = factor*height(env) /2;

        double minC1 =  floor(env.lowerLeft().getCoordinate( 0 ) - halfWidth);
        double minC2 =  floor(env.lowerLeft().getCoordinate(1)  - halfHeight);

        double maxC1 = ceil(env.upperRight().getCoordinate(0) + halfWidth);
        double maxC2 = ceil(env.upperRight().getCoordinate(1) + halfHeight);
        return new Envelope<P>(minC1, minC2, maxC1, maxC2, env.getCoordinateReferenceSystem());
    }

}
