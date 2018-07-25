package org.geolatte.mapserver.ows;

import org.geolatte.mapserver.http.HttpRequest;
import org.geolatte.mapserver.http.HttpResponse;
import org.geolatte.mapserver.ServiceRegistry;
import org.geolatte.mapserver.boot.BootServiceRegistry;
import org.geolatte.mapserver.protocols.ProtocolAdapter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The standard implementation of a {@code RequestProcessor}
 * <p>
 * Created by Karel Maesen, Geovise BVBA on 05/07/2018.
 */
public class StdHttpService implements HttpService {

    private final OwsRequestHandlerFactory handlerFactory;
    private final ExecutorService executorService;

    public StdHttpService(ExecutorService executorService) {
        this.executorService = executorService;
        ServiceRegistry registry = BootServiceRegistry.INSTANCE;

        this.handlerFactory = new OwsRequestHandlerFactory();
    }

    public StdHttpService() {
        this(Executors.newSingleThreadExecutor());
    }

    @Override
    public CompletableFuture<HttpResponse> process(HttpRequest httpRequest) {
        return CompletableFuture.supplyAsync( () -> handlerFactory.create(httpRequest).handle(), executorService);
    }

    @Override
    public void close() throws Exception {
        handlerFactory.close();
    }
}
