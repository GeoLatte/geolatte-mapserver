package org.geolatte.mapserver.ows;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public interface GetCapabilitiesRequest extends MapServerRequest {

    String getService();

    String getVersion();

    String getMimeType();
}
