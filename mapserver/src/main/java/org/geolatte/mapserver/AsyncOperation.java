package org.geolatte.mapserver;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * An interface for ows progress
 *
 * Created by Karel Maesen, Geovise BVBA on 25/07/2018.
 */
public interface AsyncOperation {

    CompletableFuture<Boolean> asFuture();

    default boolean isDone(){
        return asFuture().isDone();
    }

}
