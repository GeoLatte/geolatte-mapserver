package org.geolatte.mapserver;

import org.geolatte.mapserver.http.HttpRequest;
import org.geolatte.mapserver.http.HttpResponse;

import java.io.Closeable;

/**
 * A RequestProcessor is the main entry point for a MapServer.
 *
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public interface RequestProcessor extends AutoCloseable {

    HttpResponse process(HttpRequest request);

}
