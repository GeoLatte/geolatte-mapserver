package org.geolatte.mapserver;

import org.geolatte.mapserver.boot.BootServiceRegistry;
import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.features.FeatureSourceFactory;
import org.geolatte.mapserver.image.Imaging;
import org.geolatte.mapserver.protocols.ProtocolAdapter;

import java.util.Optional;

/**
 * Created by Karel Maesen, Geovise BVBA on 01/05/2018.
 */
public interface ServiceRegistry {

    //TODO this method makes it way to easy to couple everywhere -- move ServiceRegistry into the constructor so a different
    // ServiceRegistry can be used e.g. for testing or mocking.
    @Deprecated
    static ServiceRegistry getInstance(){
        return BootServiceRegistry.INSTANCE;
    }

    Imaging imaging();

    ProtocolAdapter protocolAdapter();

    LayerRegistry layerRegistry();

    ServiceMetadata serviceMetadata();

    FeatureSourceFactoryRegistry featureSourceFactoryRegistry();

    AsyncOperationsStatus asyncOperationsStatus();

}
