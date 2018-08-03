package org.geolatte.mapserver.protocols;

import org.geolatte.mapserver.Capabilities;
import org.geolatte.mapserver.ows.GetCapabilitiesRequest;
import org.geolatte.mapserver.ows.MapServerRequest;
import org.geolatte.mapserver.http.HttpRequest;

import java.util.List;

/**
 * A ProtocolAdapter adapts protocol-specific Requests to protocol-independent requests
 *
 * Created by Karel Maesen, Geovise BVBA on 04/07/2018.
 */
public interface ProtocolAdapter {

    List<String> supportedProtocols();

    MapServerRequest adapt(HttpRequest request);

    byte[] adaptGetCapabilities(GetCapabilitiesRequest request, Capabilities capabilities);

    boolean canHandle(HttpRequest request);

    boolean canFormat(GetCapabilitiesRequest request);

}
