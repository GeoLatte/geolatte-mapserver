package org.geolatte.mapserver.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.geom.crs.CrsRegistry;
import org.geolatte.mapserver.Layer;
import org.geolatte.mapserver.LayerRegistry;
import org.geolatte.mapserver.tilemap.TileMapBuilder;
import org.geolatte.mapserver.tilemap.TileMapLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
class LayerRegistryBuilder {

    private final static Logger logger = LoggerFactory.getLogger(LayerRegistryBuilder.class);
    private final Config config;

    LayerRegistryBuilder(Config config) {
        this.config = config;
    }

    LayerRegistry build(){

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

        throw new IllegalStateException(format("Cannot build layers of type %s", layerType));
    }



}


