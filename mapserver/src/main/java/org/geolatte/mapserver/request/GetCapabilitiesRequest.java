package org.geolatte.mapserver.request;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public interface GetCapabilitiesRequest extends MapServerRequest {

    String getService();

    String getVersion();

    String getMimeType();
}
