package org.geolatte.mapserver.boot;

import org.geolatte.mapserver.*;
import org.geolatte.mapserver.image.Imaging;
import org.geolatte.mapserver.protocols.ProtocolAdapter;
import org.geolatte.mapserver.spi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Created by Karel Maesen, Geovise BVBA on 01/05/2018.
 */
public class BootServiceLocator implements ServiceLocator {

    final private static Logger logger = LoggerFactory.getLogger(BootServiceLocator.class);

    final public static ServiceLocator INSTANCE = new BootServiceLocator();

    final private Imaging imagingInstance;
    final private ProtocolAdapter protocolAdapter;
    final private LayerRegistry layerRegistry;
    final private ServiceMetadata serviceMetadata;
    final private ExecutorService executorService;
    private final AggregatePainterFactory painterFactory;

    private static <T> T loadFirst(Class<T> providerType){
        List<T> all = loadAll(providerType);

        if (all.isEmpty()) {
            throw new IllegalStateException(format("Failure to load essential service: %s", providerType.getCanonicalName()));
        }
        T service = all.get(0);
        logger.info(format("Using %s for service %s", service.getClass().getCanonicalName(), providerType.getCanonicalName()));
        return service;

    }

    private static <T> List<T> loadAll(Class<T> providerType) {
        ServiceLoader<T> loader = ServiceLoader.load(providerType);
        List<T> result = new ArrayList<>();
        for (T img : loader) {
            logger.info(format("Loading %s for service %s",
                    img.getClass().getCanonicalName(),  providerType.getCanonicalName()));
            result.add(img);
        }
        return result;
    }

    private static List<PainterFactory> loadAllPainterFactories() {
        return loadAll(PainterFactoryProvider.class).stream()
                .map( PainterFactoryProvider::painterFactory)
                .collect(Collectors.toList());
    }

    private BootServiceLocator() {
        executorService = createExecutor();
        imagingInstance = loadFirst(ImagingProvider.class).imaging();
        protocolAdapter = loadFirst(ProtocolAdapterProvider.class).protocolAdapter();
        // TODO -- we needed to split off the FSFRegistry from ServiceRegistry because
        // the LayerRegistryProvider depends on this. Can we solve this differently? (See how Hibernate solves this)
        FeatureSourceFactoryRegistry featureSourceFactoryRegistry = new FeatureSourceFactoryImpl();
        serviceMetadata = loadFirst(ServiceMetadataProvider.class).serviceMetadata();
        layerRegistry = loadFirst(LayerRegistryProvider.class).layerSourceRegistry(featureSourceFactoryRegistry);
        painterFactory = new AggregatePainterFactory(loadAllPainterFactories());
    }


    public static ServiceLocator instance(){
        return INSTANCE;
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

    //TODO -- make this configurable through a provider
    @Override
    public ExecutorService executorService() {
        return this.executorService;
    }

    @Override
    public PainterFactory painterFactory() {
        return painterFactory;
    }

    private ExecutorService createExecutor() {
        return Executors.newFixedThreadPool(2*Runtime.getRuntime().availableProcessors());
    }

}
