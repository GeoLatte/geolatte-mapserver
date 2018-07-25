package org.geolatte.mapserver;

/**
 * Created by Karel Maesen, Geovise BVBA on 26/07/2018.
 */
public interface RequestHandlerFactory<REQUEST, RESPONSE> extends AutoCloseable {

    RequestHandler<RESPONSE> create(REQUEST request);

    @Override
    void close() throws Exception;
}
