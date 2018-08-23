package org.geolatte.mapserver.config;

import com.typesafe.config.Config;
import org.geolatte.mapserver.FeatureSourceFactoryRegistry;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.layers.DynamicLayer;
import org.geolatte.mapserver.render.RenderContext;

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

        RenderContext renderContext = RenderContext.from(fs, painter);
        return new DynamicLayer(name, renderContext, this.serviceLocator, factor);
    }

}
