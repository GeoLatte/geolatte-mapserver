package org.geolatte.mapserver.http;

import org.geolatte.mapserver.RequestHandlerFactory;
import org.geolatte.mapserver.ServiceRegistry;
import org.geolatte.mapserver.boot.BootServiceRegistry;
import org.geolatte.mapserver.http.HttpRequest;
import org.geolatte.mapserver.http.HttpResponse;
import org.geolatte.mapserver.http.HttpService;
import org.geolatte.mapserver.protocols.ProtocolAdapter;
import org.geolatte.mapserver.request.MapServerRequest;

import static java.lang.String.format;

/**
 * The standard implementation of a {@code RequestProcessor}
 * <p>
 * Created by Karel Maesen, Geovise BVBA on 05/07/2018.
 */
public class StdHttpService implements HttpService {


    private final ProtocolAdapter protocolAdapter;
    private final RequestHandlerFactory handlerFactory;

    public StdHttpService() {
        ServiceRegistry registry = BootServiceRegistry.INSTANCE;
        protocolAdapter = registry.protocolAdapter();
        this.handlerFactory = new RequestHandlerFactory();
    }

    @Override
    public HttpResponse process(HttpRequest httpRequest) {
        MapServerRequest request = protocolAdapter.adapt(httpRequest);
        return handlerFactory.create(request).handle();
    }

    @Override
    public void close() throws Exception {
        handlerFactory.close();
    }
}
