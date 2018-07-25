package org.geolatte.mapserver.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import org.geolatte.mapserver.FeatureSourceFactoryRegistry;
import org.geolatte.mapserver.Layer;
import org.geolatte.mapserver.LayerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        logger.info(format("Builder layer %s", name));

        return new LayerBuilder(name, featureSourceFactoryRegistry, layerConfig).build();

    }


}


