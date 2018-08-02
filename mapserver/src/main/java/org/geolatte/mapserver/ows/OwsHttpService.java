package org.geolatte.mapserver.ows;

import org.geolatte.mapserver.http.HttpRequest;
import org.geolatte.mapserver.http.HttpResponse;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.http.HttpService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * The standard implementation of a {@code RequestProcessor}
 * <p>
 * Created by Karel Maesen, Geovise BVBA on 05/07/2018.
 */
public class OwsHttpService implements HttpService {

    private final OwsRequestHandlerFactory handlerFactory;

    public OwsHttpService(ServiceLocator serviceLocator) {
        this.handlerFactory = new OwsRequestHandlerFactory();
    }

    public OwsHttpService() {
        this(ServiceLocator.defaultInstance());
    }

    @Override
    public CompletableFuture<HttpResponse> process(HttpRequest httpRequest) {
        return handlerFactory.create(httpRequest).handle();
    }

    @Override
    public void close() throws Exception {
        handlerFactory.close();
    }
}
