package org.geolatte.mapserver.layers;

import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.features.FeatureSource;

/**
 * Created by Karel Maesen, Geovise BVBA on 28/07/2018.
 */
public class RenderContext {

    final private FeatureSource featureSource;
    final private String painterRef;

    public RenderContext(FeatureSource featureSource, String painterRef, ServiceLocator serviceLocator) {
        this.featureSource = featureSource;
        this.painterRef = painterRef;
    }

    public static RenderContext from(FeatureSource featureSource, String painterRef) {
        return new RenderContext(featureSource, painterRef, ServiceLocator.defaultInstance());
    }

    public String getPainterRef() {
        return painterRef;
    }

    public FeatureSource getFeatureSource() {
        return featureSource;
    }


}
