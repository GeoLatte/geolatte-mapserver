package org.geolatte.mapserver.util;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;

import static java.lang.Math.ceil;

/**
 * Created by Karel Maesen, Geovise BVBA on 12/04/2018.
 */
public class EnvelopUtils {

    public static double width(Envelope<C2D> env) {
        return env.upperRight().getX() - env.lowerLeft().getX();
    }

    public static double height(Envelope<C2D> env) {
        return env.upperRight().getY() - env.lowerLeft().getY();
    }

    public static Envelope<C2D> bufferRounded(Envelope<C2D> env, double factor) {

        if (factor <= 1) {
            throw new IllegalArgumentException("Factor needs to be > 1");
        }

        double halfWidth = factor*width(env) / 2;
        double halfHeight = factor*height(env) /2;

        C2D lowerLeft = new C2D(
                 ceil(env.lowerLeft().getX() - halfWidth),
                ceil(env.lowerLeft().getY()  - halfHeight)
                );
        C2D upperRight = new C2D(
                ceil(env.upperRight().getX() + halfWidth),
                ceil(env.upperRight().getY() + halfHeight));

        return new Envelope<C2D>(lowerLeft, upperRight, env.getCoordinateReferenceSystem());
    }

}
