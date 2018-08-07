package org.geolatte.mapserver.rxhttp;

import org.geolatte.mapserver.features.FeatureDeserializer;
import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.features.FeatureSourceConfig;
import org.geolatte.mapserver.features.FeatureSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

/**
 * Created by Karel Maesen, Geovise BVBA on 20/07/2018.
 */
public class RxHttpFeatureSourceFactory implements FeatureSourceFactory {

    private static Logger logger = LoggerFactory.getLogger(RxHttpFeatureSourceFactory.class);

    @Override
    public Class<? extends FeatureSource> resultClass() {
        return RxHttpFeatureSource.class;
    }

    @Override
    public FeatureSource mkFeatureSource(FeatureSourceConfig config) {
        if(config instanceof RxHttpFeatureSource) {
            throw new IllegalStateException(format("Unexpected type of config: %s ", config.getClass().getCanonicalName()));
        }
        RxHttpFeatureSourceConfig featureSourceConfig = (RxHttpFeatureSourceConfig) config;
        FeatureDeserializerFactory factory =  instantiate(featureSourceConfig.getFeatureDeserializerFactory());
        return new RxHttpFeatureSource(featureSourceConfig, factory);
    }

    private FeatureDeserializerFactory instantiate(String factoryClassName) {
        if(factoryClassName == null) {
            logger.info("RxHttpFeatureSource is using the standard GeoJsonFeatureDeserializer factory");
            return new GeoJsonFeatureDeserializerFactory();
        } else {
            try {
                logger.info(format("RxHttpFeatureSource is configured with %s as FeatureDeserializerFactory", factoryClassName));
                return (FeatureDeserializerFactory) Class.forName(factoryClassName).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e) {
                logger.error(format("Failed to instantiate instance of %s as FeatureDeserializerFactory", factoryClassName), e);
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public Class<? extends FeatureSourceConfig> configClass() {
        return RxHttpFeatureSourceConfig.class;
    }


}
