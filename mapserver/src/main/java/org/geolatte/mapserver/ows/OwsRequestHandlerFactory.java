package org.geolatte.mapserver.ows;

import org.geolatte.mapserver.*;
import org.geolatte.mapserver.http.HttpRequest;
import org.geolatte.mapserver.http.HttpResponse;
import org.geolatte.mapserver.protocols.ProtocolAdapter;

import static java.lang.String.format;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public class OwsRequestHandlerFactory implements RequestHandlerFactory<HttpRequest, HttpResponse> {

    private final LayerRegistry layerRegistry;
    private final ServiceMetadata serviceMetadata;
    private final ProtocolAdapter protocolAdapter;

    public OwsRequestHandlerFactory(ServiceLocator serviceLocator) {
        layerRegistry = serviceLocator.layerRegistry();
        serviceMetadata = serviceLocator.serviceMetadata();
        protocolAdapter = serviceLocator.protocolAdapter();
    }

    public OwsRequestHandlerFactory() {
        this(ServiceLocator.defaultInstance());
    }

    @Override
    public RequestHandler<HttpResponse> create(HttpRequest httpRequest) {
        MapServerRequest request = protocolAdapter.adapt(httpRequest);
        if (request instanceof GetMapRequest) {
            return new GetMapRequestHandler((GetMapRequest) request, layerRegistry);
        }

        if (request instanceof GetCapabilitiesRequest) {
            return new GetCapabilitiesHandler((GetCapabilitiesRequest) request, serviceMetadata, protocolAdapter);
        }

        throw new IllegalArgumentException(format("Can't handle Request of type %s", request.getClass().getName()));
    }

    @Override
    public void close() throws Exception {
        layerRegistry.close();
    }
}

