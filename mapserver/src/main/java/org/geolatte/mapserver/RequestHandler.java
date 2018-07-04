package org.geolatte.mapserver;

import org.geolatte.mapserver.http.HttpResponse;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
interface RequestHandler {

    HttpResponse handle();

}
