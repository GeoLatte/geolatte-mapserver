package org.geolatte.mapserver.config;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.geolatte.mapserver.features.FeatureSource;
import rx.Observable;

import java.io.IOException;

/**
 * Created by Karel Maesen, Geovise BVBA on 02/08/2018.
 */
public class FeatureSourceDouble implements FeatureSource {

    @Override
    public Observable<PlanarFeature> query(Envelope<C2D> bbox, String query) {
        return null; //won't be invoked by test classes so don't worry
    }

    @Override
    public void close() throws IOException {

    }
}
