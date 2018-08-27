package org.geolatte.mapserver.render;

import org.geolatte.mapserver.features.FeatureSource;

/**
 * Created by Karel Maesen, Geovise BVBA on 28/07/2018.
 */
public class RenderContext {

    final private FeatureSource featureSource;
    final private String painterRef;
    final private BboxFactors bboxFactors;

    private RenderContext(FeatureSource featureSource, String painterRef, BboxFactors bboxFactors) {
        this.featureSource = featureSource;
        this.painterRef = painterRef;
        this.bboxFactors = bboxFactors;
    }

    public static RenderContext from(FeatureSource featureSource, String painterRef,
                                     BboxFactors bboxFactors) {

        if (featureSource == null || painterRef == null) {
            throw new IllegalArgumentException("Can't accept null arguments");
        }
        return new RenderContext(featureSource, painterRef, bboxFactors);
    }

    public static RenderContext from(FeatureSource featureSource, String painterRef) {
        return from(featureSource, painterRef, new BboxFactors());
    }

    public String getPainterRef() {
        return painterRef;
    }

    public FeatureSource getFeatureSource() {
        return featureSource;
    }


    public BboxFactors getBboxFactors() {
        return bboxFactors;
    }
}
