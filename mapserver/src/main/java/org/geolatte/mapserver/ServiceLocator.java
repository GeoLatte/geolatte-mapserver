package org.geolatte.mapserver;

import org.geolatte.mapserver.boot.BootServiceLocator;
import org.geolatte.mapserver.features.FeatureDeserializer;
import org.geolatte.mapserver.image.Imaging;
import org.geolatte.mapserver.protocols.ProtocolAdapter;

import java.util.concurrent.ExecutorService;

/**
 * Created by Karel Maesen, Geovise BVBA on 01/05/2018.
 */
public interface ServiceLocator {

    static ServiceLocator defaultInstance(){
        return BootServiceLocator.INSTANCE;
    }

    Imaging imaging();

    ProtocolAdapter protocolAdapter();

    LayerRegistry layerRegistry();

    ServiceMetadata serviceMetadata();

    ExecutorService executorService();

    PainterFactory painterFactory();

}
