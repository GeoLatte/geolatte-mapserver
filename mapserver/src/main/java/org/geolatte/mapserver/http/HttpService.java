package org.geolatte.mapserver.http;

import org.geolatte.mapserver.http.HttpRequest;
import org.geolatte.mapserver.http.HttpResponse;

import java.util.concurrent.CompletableFuture;

/**
 * A RequestProcessor is the main entry point for a MapServer.
 *
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public interface HttpService extends AutoCloseable {

    CompletableFuture<HttpResponse> process(HttpRequest request);

}
