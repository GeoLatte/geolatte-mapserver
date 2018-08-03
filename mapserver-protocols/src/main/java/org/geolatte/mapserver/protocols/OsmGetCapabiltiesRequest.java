package org.geolatte.mapserver.protocols;

import org.geolatte.mapserver.ows.GetCapabilitiesRequest;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public class OsmGetCapabiltiesRequest implements GetCapabilitiesRequest {


    private final String service;
    private final String version;
    private final String mimeType;

    public OsmGetCapabiltiesRequest(String service, String version, String mimeType) {
        this.service = service;
        this.version = version;
        this.mimeType = mimeType;
    }

    @Override
    public String getService() {
        return service;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }
}
