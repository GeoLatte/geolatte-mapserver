package org.geolatte.mapserver;

import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.request.GetMapRequest;

/**
 *
 *
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public interface Layer {

    String getName();

    LayerType getType();

    Image createMapImage(GetMapRequest request);

}
