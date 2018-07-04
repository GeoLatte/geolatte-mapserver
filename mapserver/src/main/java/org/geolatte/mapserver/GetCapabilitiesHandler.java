package org.geolatte.mapserver;

import org.geolatte.mapserver.http.BasicHttpResponse;
import org.geolatte.mapserver.http.HttpResponse;
import org.geolatte.mapserver.protocols.ProtocolAdapter;
import org.geolatte.mapserver.request.GetCapabilitiesRequest;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
class GetCapabilitiesHandler implements RequestHandler {

    private final GetCapabilitiesRequest request;
    private final ServiceMetadata serviceMetadata;
    private final ProtocolAdapter protocolAdapter;


    GetCapabilitiesHandler(GetCapabilitiesRequest request, ServiceMetadata serviceMetadata, ProtocolAdapter protocolAdapter) {
        this.request = request;
        this.serviceMetadata = serviceMetadata;
        this.protocolAdapter = protocolAdapter;
    }

    @Override
    public HttpResponse handle() {
        Capabilities capabilities = new Capabilities(serviceMetadata, null);
        byte[] output = protocolAdapter.adaptGetCapabilities(request, capabilities);
        return formatToHttpResponse(output);
    }

    private HttpResponse formatToHttpResponse(byte[] output) {
        return new BasicHttpResponse.Builder()
                .ok()
                .body(output)
                .setHeader("Content-type", "application/xml")
                .build();
    }
}
