package org.geolatte.mapserver.config;

import com.typesafe.config.Config;
import org.geolatte.mapserver.FeatureSourceFactoryRegistry;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.layers.DynamicLayer;
import org.geolatte.mapserver.render.BboxFactors;
import org.geolatte.mapserver.render.RenderContext;

import static org.geolatte.mapserver.render.BboxFactors.factor;
import static org.geolatte.mapserver.render.BboxFactors.upp;

/**
 * Created by Karel Maesen, Geovise BVBA on 03/08/2018.
 */
public class DynamicLayerBuilder extends LayerBuilder {

    final private Config config;


    public DynamicLayerBuilder(String name, FeatureSourceFactoryRegistry featureSourceFactoryRegistry,
                               Config config, ServiceLocator locator) {
        super(name, featureSourceFactoryRegistry, locator);
        this.config = config;
    }

    @Override
    public DynamicLayer build() {
        FeatureSource fs = mkFeatureSource(config.getConfig("source"));
        String painter = config.getString("painter");
        BboxFactors factors =  mkBboxFactors();
        RenderContext renderContext = RenderContext.from(fs, painter, factors);
        return new DynamicLayer(name, renderContext, this.serviceLocator);
    }

    private BboxFactors mkBboxFactors() {
        Double factor = config.hasPath("bboxFactor") ? config.getDouble("bboxFactor") : null;
        BboxFactors factors =  factor == null? new BboxFactors() : new BboxFactors(factor);
        if (config.hasPath("bboxFactors")) {
            addFactorBreaks(factors);
        }
        return factors;
    }

    private void addFactorBreaks(BboxFactors factors) {
        config.getObject("bboxFactors")
                .entrySet()
                .stream()
                .forEach( entry ->
                        factors.put(
                                upp(Double.valueOf(entry.getKey())),
                                factor(((Number) entry.getValue().unwrapped()).doubleValue()))
                );
    }

}
