package org.geolatte.mapserver.ows;

import org.geolatte.mapserver.Capabilities;
import org.geolatte.mapserver.RequestHandler;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.ServiceMetadata;
import org.geolatte.mapserver.http.BasicHttpResponse;
import org.geolatte.mapserver.http.HttpResponse;
import org.geolatte.mapserver.protocols.ProtocolAdapter;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
class GetCapabilitiesHandler implements RequestHandler<HttpResponse> {

    private final GetCapabilitiesRequest request;
    private final ServiceMetadata serviceMetadata;
    private final ProtocolAdapter protocolAdapter;


    GetCapabilitiesHandler(GetCapabilitiesRequest request, ServiceLocator serviceLocator) {
        this.request = request;
        this.serviceMetadata = serviceLocator.serviceMetadata();
        this.protocolAdapter = serviceLocator.protocolAdapter();
    }

    //TODO -- handle failure case properly
    @Override
    public CompletableFuture<HttpResponse> handle() {
        Capabilities capabilities = new Capabilities(serviceMetadata, null);
        byte[] output = protocolAdapter.adaptGetCapabilities(request, capabilities);
        return CompletableFuture.completedFuture(formatToHttpResponse(output));
    }

    private HttpResponse formatToHttpResponse(byte[] output) {
        return new BasicHttpResponse.Builder()
                .ok()
                .body(output)
                .setHeader("Content-type", "application/xml")
                .build();
    }
}
