package org.geolatte.mapserver.test;

import org.geolatte.geom.*;
import org.geolatte.geom.generator.Generators;
import org.geolatte.geom.generator.GeometryGenerator;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.geolatte.mapserver.features.FeatureSource;
import rx.Observable;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Karel Maesen, Geovise BVBA on 03/08/2018.
 */
public class FeatureSourceGenerate implements FeatureSource {


    @Override
    public Observable<PlanarFeature> query(Envelope<C2D> bbox, String query) {

        GeometryGenerator<C2D, Point<C2D>> point = Generators.point(bbox, 52);

        return Observable.range(0, 10)
                .map(i -> point.generate())
                .map(geom -> new FeatureTestImpl(geom))
                .map(PlanarFeature::from);
    }

    @Override
    public void close() throws IOException {

    }
}

class FeatureTestImpl implements Feature{

    private final Geometry<C2D> geom;

    FeatureTestImpl(Geometry<C2D> geom){
        this.geom = geom;
    }

    @Override
    public Geometry getGeometry() {
        return geom;
    }

    @Override
    public Object getId() {
        return this.hashCode();
    }

    @Override
    public Map<String, Object> getProperties() {
        return Collections.emptyMap();
    }
}