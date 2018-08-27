package org.geolatte.mapserver.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import org.geolatte.mapserver.FeatureSourceFactoryRegistry;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.layers.DynamicLayer;
import org.geolatte.mapserver.render.RenderContext;

import java.util.TreeMap;

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
        Double factor = config.hasPath("bboxFactor") ? config.getDouble("bboxFactor") : null;
        TreeMap<Double, Double> dynamicFactors = config.hasPath("bboxFactors") ? toDynamicFactors(config.getObject("bboxFactors")) : null;

        RenderContext renderContext = RenderContext.from(fs, painter);
        return new DynamicLayer(name, renderContext, this.serviceLocator, factor, dynamicFactors);
    }

    private TreeMap<Double, Double> toDynamicFactors(ConfigObject values) {
        return values.entrySet().stream().collect(TreeMap::new,
                (map, entry) -> map.put(Double.valueOf(entry.getKey()),  ((Number) entry.getValue().unwrapped()).doubleValue()),
                (m, c) -> new RuntimeException("No duplicate keys in bboxFactors allowed"));
    }

}
