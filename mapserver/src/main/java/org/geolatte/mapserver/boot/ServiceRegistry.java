package org.geolatte.mapserver.boot;

import org.geolatte.mapserver.spi.Imaging;

/**
 * Created by Karel Maesen, Geovise BVBA on 01/05/2018.
 */
public interface ServiceRegistry {

    ServiceRegistry INSTANCE = new BootServiceRegistry();

    static ServiceRegistry getDefault(){
        return INSTANCE;
    }

    Imaging getImaging();
}
