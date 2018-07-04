package org.geolatte.mapserver.boot;

import org.geolatte.mapserver.ServiceRegistry;
import org.geolatte.mapserver.image.Imaging;
import org.geolatte.mapserver.protocols.ProtocolAdapter;
import org.geolatte.mapserver.ServiceMetadata;
import org.geolatte.mapserver.spi.ImagingProvider;
import org.geolatte.mapserver.spi.ProtocolAdapterProvider;
import org.geolatte.mapserver.spi.ServiceMetadataProvider;
import org.geolatte.mapserver.LayerRegistry;
import org.geolatte.mapserver.spi.LayerRegistryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

import static java.lang.String.format;

/**
 * Created by Karel Maesen, Geovise BVBA on 01/05/2018.
 */
public class BootServiceRegistry implements ServiceRegistry {

    final private static Logger logger = LoggerFactory.getLogger(BootServiceRegistry.class);

    final public static ServiceRegistry INSTANCE = new BootServiceRegistry();

    final private Imaging imagingInstance;
    final private ProtocolAdapter protocolAdapter;
    final private LayerRegistry layerRegistry;
    final private ServiceMetadata serviceMetadata;

    private static <T> T load(Class<T> providerType){
        ServiceLoader<T> loader = ServiceLoader.load(providerType);
        for (T img : loader) {
            logger.info(format("Loading %s for service %s",
                    img.getClass().getCanonicalName(),  providerType.getCanonicalName()));
            return img;
        }
        throw new IllegalStateException(format("Failure to load essential service: %s", providerType.getCanonicalName()) );
    }

    BootServiceRegistry () {
        imagingInstance = load(ImagingProvider.class).imaging();
        protocolAdapter = load(ProtocolAdapterProvider.class).protocolAdapter();
        layerRegistry = load(LayerRegistryProvider.class).layerSourceRegistry();
        serviceMetadata = load(ServiceMetadataProvider.class).serviceMetadata();
    }

    @Override
    public Imaging imaging() {
        return imagingInstance;
    }

    @Override
    public ProtocolAdapter protocolAdapter() {
        return protocolAdapter;
    }

    @Override
    public LayerRegistry layerRegistry() {
        return this.layerRegistry;
    }

    @Override
    public ServiceMetadata serviceMetadata() {
        return this.serviceMetadata;
    }


}
