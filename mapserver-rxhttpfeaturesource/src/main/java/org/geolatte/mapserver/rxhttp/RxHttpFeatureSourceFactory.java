package org.geolatte.mapserver.rxhttp;

import org.geolatte.mapserver.ServiceLocator;
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
    private ServiceLocator serviceLocator;

    @Override
    public Class<? extends FeatureSource> resultClass() {
        return RxHttpFeatureSource.class;
    }

    @Override
    public void setServiceLocator(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    @Override
    public RxHttpFeatureSource mkFeatureSource(FeatureSourceConfig config) {
        if (config instanceof RxHttpFeatureSource) {
            throw new IllegalStateException(format("Unexpected type of config: %s ", config.getClass().getCanonicalName()));
        }
        RxHttpFeatureSourceConfig featureSourceConfig = (RxHttpFeatureSourceConfig) config;
        FeatureDeserializerFactory factory = instantiate(featureSourceConfig.getFeatureDeserializerFactory());
        //assume the source doesn't do the coordinate transforms for us, so we need to do the transforms client-side
        return new RxHttpFeatureSource(featureSourceConfig, factory, serviceLocator.coordinateTransforms());
    }

    private FeatureDeserializerFactory instantiate(String factoryClassName) {
        if (factoryClassName == null) {
            return instantiateStandardDeserFactory(factoryClassName);
        }
        return instantiateConfiguredClassName(factoryClassName);
    }

    private FeatureDeserializerFactory instantiateConfiguredClassName(String factoryClassName) {
        try {
            logger.info(format("RxHttpFeatureSource is configured with %s as FeatureDeserializerFactory", factoryClassName));
            return (FeatureDeserializerFactory) Class.forName(factoryClassName).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e) {
            logger.error(format("Failed to instantiate instance of %s as FeatureDeserializerFactory", factoryClassName), e);
            throw new IllegalStateException(e);
        }
    }

    private FeatureDeserializerFactory instantiateStandardDeserFactory(String factoryClassName) {
        logger.info(format("RxHttpFeatureSource uses the standard FeatureDeserializerFactory %s", factoryClassName));
        return new GeoJsonFeatureDeserializerFactory();
    }

    @Override
    public Class<? extends FeatureSourceConfig> configClass() {
        return RxHttpFeatureSourceConfig.class;
    }


}
