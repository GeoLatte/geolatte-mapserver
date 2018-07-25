package org.geolatte.mapserver;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public interface RequestHandler<R> {

    R handle();

}
