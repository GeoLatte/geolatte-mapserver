package org.geolatte.mapserver.render;

import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.features.FeatureSource;

/**
 * Created by Karel Maesen, Geovise BVBA on 28/07/2018.
 */
public class RenderContext {

    final private FeatureSource featureSource;
    final private String painterRef;

    private RenderContext(FeatureSource featureSource, String painterRef) {
        this.featureSource = featureSource;
        this.painterRef = painterRef;
    }

    public static RenderContext from(FeatureSource featureSource, String painterRef) {
        if (featureSource == null || painterRef == null) {
            throw new IllegalArgumentException("Can't accept null arguments");
        }
        return new RenderContext(featureSource, painterRef);
    }

    public String getPainterRef() {
        return painterRef;
    }

    public FeatureSource getFeatureSource() {
        return featureSource;
    }


}
