package org.geolatte.mapserver;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public interface RequestHandler<R> {

    CompletableFuture<R> handle();

}
