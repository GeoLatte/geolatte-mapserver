package org.geolatte.mapserver;

import org.geolatte.mapserver.protocols.ProtocolAdapter;
import org.geolatte.mapserver.request.GetCapabilitiesRequest;
import org.geolatte.mapserver.request.GetMapRequest;
import org.geolatte.mapserver.request.MapServerRequest;


import static java.lang.String.format;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public class RequestHandlerFactory {

    private final LayerRegistry layerRegistry;
    private final ServiceMetadata serviceMetadata;
    private final ProtocolAdapter protocolAdapter;

    public RequestHandlerFactory() {
        layerRegistry = ServiceRegistry.getInstance().layerRegistry() ;
        serviceMetadata = ServiceRegistry.getInstance().serviceMetadata();
        protocolAdapter = ServiceRegistry.getInstance().protocolAdapter();
    }

    RequestHandler create(MapServerRequest request){
        if (request instanceof GetMapRequest) {
            return new GetMapRequestHandler((GetMapRequest) request, layerRegistry);
        }

        if (request instanceof GetCapabilitiesRequest) {
            return new GetCapabilitiesHandler((GetCapabilitiesRequest) request, serviceMetadata, protocolAdapter);
        }

        throw new IllegalArgumentException(format("Can't handle Request of type %s", request.getClass().getName()));
    }
}

