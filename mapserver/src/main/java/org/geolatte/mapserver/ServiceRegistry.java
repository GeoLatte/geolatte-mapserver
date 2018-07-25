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
