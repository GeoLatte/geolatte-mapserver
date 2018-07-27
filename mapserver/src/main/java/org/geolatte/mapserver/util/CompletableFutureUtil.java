package org.geolatte.mapserver.util;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;

/**
 * Created by Karel Maesen, Geovise BVBA on 27/07/2018.
 */
public class CompletableFutureUtil {

    public static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        return CompletableFuture.allOf(
                futures.toArray(new CompletableFuture<?>[futures.size()])
        ).thenApply(v -> futures.stream().map(CompletableFuture<T>::join).collect(toList()));
    }
}
