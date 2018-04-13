package org.geolatte.mapserver.core;

/**
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public interface LayerSource {

    String getName();

    LayerSourceType getType();

}
