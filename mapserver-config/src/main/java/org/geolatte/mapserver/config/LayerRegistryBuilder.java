package org.geolatte.mapserver.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigObject;
import org.geolatte.mapserver.FeatureSourceFactoryRegistry;
import org.geolatte.mapserver.Layer;
import org.geolatte.mapserver.LayerRegistry;
import org.geolatte.mapserver.ServiceRegistry;
import org.geolatte.mapserver.features.FeatureLayer;
import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.features.FeatureSourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.lang.String.format;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
class LayerRegistryBuilder {

    private final static Logger logger = LoggerFactory.getLogger(LayerRegistryBuilder.class);
    private final Config config;
    private final FeatureSourceFactoryRegistry featureSourceFactoryRegistry;

    LayerRegistryBuilder(FeatureSourceFactoryRegistry featureSourceFactoryRegistry, Config config) {
        this.featureSourceFactoryRegistry = featureSourceFactoryRegistry;
        this.config = config;
    }

    LayerRegistry build() {

        LayerRegistryImpl registry = new LayerRegistryImpl();
        ConfigObject root = this.config.root();
        root.entrySet()
                .stream()
                .forEach(entry -> registry.add(
                        buildLayer(entry.getKey(), (ConfigObject) entry.getValue())
                        )
                );
        return registry;
    }

    private Layer buildLayer(String name, ConfigObject layerConfigObj) {
        Config layerConfig = layerConfigObj.toConfig();
        String layerType = layerConfig.getString("type");
        logger.info(format("Builder layer %s of type %s", name, layerType));

        if ("tilemap".equalsIgnoreCase(layerType)) {
            return new TileMapLayerBuilder(name, layerConfig).build();
        }

        if ("vector".equalsIgnoreCase(layerType)) {
            return mkFeatureSource(name, layerConfig).
                    map(fs -> new FeatureLayer(name, fs))
                    .orElse(null);
        }

        throw new IllegalStateException(format("Cannot build layers of type %s", layerType));
    }

    private Optional<FeatureSource> mkFeatureSource(String name, Config layerConfig) {
        String featureSourceClass = layerConfig.getString("source-type");
        Optional<FeatureSource> featureSource = featureSourceFactoryRegistry
                .featureSourceFactoryForType(featureSourceClass)
                .map(f -> {
                    FeatureSourceConfig fsc = ConfigBeanFactory.create(layerConfig, f.configClass());
                    return f.mkFeatureSource(fsc);
                });

        //Let's make configuration lenient and just log that we can't seem to
        //create this layer
        if (!featureSource.isPresent()) {
            logger.warn(format("Can't create layer %s: no factory for type %s configured", name, featureSourceClass));
        }

        return featureSource;
    }


}


