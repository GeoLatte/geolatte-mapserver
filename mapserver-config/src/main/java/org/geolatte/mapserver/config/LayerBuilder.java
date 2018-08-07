package org.geolatte.mapserver.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import org.geolatte.mapserver.FeatureSourceFactoryRegistry;
import org.geolatte.mapserver.Layer;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.features.FeatureSourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Created by Karel Maesen, Geovise BVBA on 03/08/2018.
 */
public abstract class LayerBuilder {

    final private static Logger logger = LoggerFactory.getLogger(LayerBuilder.class);
    final private FeatureSourceFactoryRegistry featureSourceFactoryRegistry;
    final ServiceLocator serviceLocator;

    final String name;

    FeatureSource featureSource;

    LayerBuilder(String name, FeatureSourceFactoryRegistry featureSourceFactoryRegistry, ServiceLocator locator) {
        this.name = name;
        this.featureSourceFactoryRegistry = featureSourceFactoryRegistry;
        this.serviceLocator = locator;
    }

    abstract public Layer build();


    FeatureSource mkFeatureSource(Config sourceConfig) {
        String featureSourceClass = sourceConfig.getString("type");
        Optional<FeatureSource> featureSource = featureSourceFactoryRegistry
                .featureSourceFactoryForType(featureSourceClass)
                .map(f -> {
                    FeatureSourceConfig fsc = ConfigBeanFactory.create(sourceConfig, f.configClass());
                    return f.mkFeatureSource(fsc);
                });

        //Let's make configuration lenient and just log that we can't seem to
        //create this layer
        if (!featureSource.isPresent()) {
            getLogger().warn(String.format("Can't create layer %s: no factory for type %s configured", name, featureSourceClass));
            return null;
        } else {
            return featureSource.get();
        }
    }

    private Logger getLogger() {
        return logger;
    }

}
