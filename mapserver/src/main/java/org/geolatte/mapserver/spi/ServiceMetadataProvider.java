package org.geolatte.mapserver.spi;


import org.geolatte.mapserver.ServiceMetadata;

/**
 * Created by Karel Maesen, Geovise BVBA on 09/05/2018.
 */
public interface ServiceMetadataProvider {


    ServiceMetadata serviceMetadata();

}
