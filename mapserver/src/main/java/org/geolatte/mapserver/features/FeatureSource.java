package org.geolatte.mapserver.features;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.geolatte.mapserver.render.BboxFactors;
import rx.Observable;

import java.awt.*;
import java.io.Closeable;

/**
 * An asynchrounous source for @{code Features}
 *
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public interface FeatureSource extends Closeable {

    Observable<PlanarFeature> query(Envelope<C2D> bbox, String query, double bboxScaleFactor);

    default Observable<PlanarFeature> query(Envelope<C2D> bbox, double bboxScaleFactor) {
        return query( bbox, null, bboxScaleFactor);
    }
}
