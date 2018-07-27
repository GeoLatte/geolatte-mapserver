package org.geolatte.mapserver.features;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Feature;
import org.geolatte.maprenderer.map.PlanarFeature;
import rx.Observable;

import java.io.Closeable;

/**
 * An asynchrounous source for @{code Features}
 *
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public interface FeatureSource extends Closeable {

    Observable<PlanarFeature> query(Envelope<C2D> bbox, String query);

    default Observable<PlanarFeature> query(Envelope<C2D> bbox) {
        return query( bbox, null);
    }
}
