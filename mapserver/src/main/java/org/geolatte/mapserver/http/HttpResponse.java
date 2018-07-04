package org.geolatte.mapserver.http;

/**
 * Represents an Http Response
 *
 * <p>We provide this interface to allow integration with as much Http Libraries as possible.
 * </p>
 *
 *
 * Created by Karel Maesen, Geovise BVBA on 05/07/2018.
 */
public interface HttpResponse {

    HttpHeaders headers();

    byte[] body();

    public int statusCode();

}
