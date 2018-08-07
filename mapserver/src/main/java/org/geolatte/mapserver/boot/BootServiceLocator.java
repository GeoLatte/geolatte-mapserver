package org.geolatte.mapserver.boot;

import org.geolatte.mapserver.*;
import org.geolatte.mapserver.features.FeatureDeserializer;
import org.geolatte.mapserver.features.FeatureSourceFactory;
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

    final public static BootServiceLocator INSTANCE = new BootServiceLocator();

    static  {
        // we need ServiceLocator to build Layers. So we first build the INSTANCE, and then use it to
        // set build the LayerRegistry and set it in the instance.
        //
        // It would be better to have a second phase bootstrapping for initializing the LayerRegistry
        // (e.g. Hibernate has this) after the more general servcies are initialized.
        FeatureSourceFactoryRegistry fsf = new StdFeatureSourceFactory(loadAllFeatureSourceFactories());
        LayerRegistry registry = loadFirst(LayerRegistryProvider.class).layerRegistry(fsf, INSTANCE);
        INSTANCE.setLayerRegistry(registry);
    }

    final private Imaging imagingInstance;
    final private ProtocolAdapter protocolAdapter;
    final private ServiceMetadata serviceMetadata;
    final private ExecutorService executorService;
    private final AggregatePainterFactory painterFactory;


    private LayerRegistry layerRegistry;


    static <T> T loadFirst(Class<T> providerType){
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

    private static List<FeatureSourceFactory> loadAllFeatureSourceFactories() {
        return loadAll(FeatureSourceFactoryProvider.class).stream()
                .map( FeatureSourceFactoryProvider::featureSourceFactory)
                .collect(Collectors.toList());
    }

    private BootServiceLocator() {
        executorService = createExecutor();
        imagingInstance = loadFirst(ImagingProvider.class).imaging();
        protocolAdapter = loadFirst(ProtocolAdapterProvider.class).protocolAdapter();
        painterFactory = new AggregatePainterFactory(loadAllPainterFactories());
        serviceMetadata = loadFirst(ServiceMetadataProvider.class).serviceMetadata();
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
        return layerRegistry;
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

    private void setLayerRegistry(LayerRegistry r){
        this.layerRegistry = r;
    }

}
