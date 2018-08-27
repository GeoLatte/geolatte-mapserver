package org.geolatte.mapserver.ows;

import org.geolatte.mapserver.Layer;
import org.geolatte.mapserver.LayerRegistry;
import org.geolatte.mapserver.RequestHandler;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.http.HttpResponse;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.geolatte.mapserver.http.HttpResponseHelper.futureNotFoundResponse;
import static org.geolatte.mapserver.http.HttpResponseHelper.imageToResponse;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public class GetMapRequestHandler implements RequestHandler<HttpResponse> {


    private final GetMapRequest request;
    private final LayerRegistry layerRegistry;

    GetMapRequestHandler(GetMapRequest request, ServiceLocator serviceLocator) {
        this.request = request;
        this.layerRegistry = serviceLocator.layerRegistry();
    }

    @Override
    public CompletableFuture<HttpResponse> handle() {
        Optional<Layer> layerSource = layerRegistry.getLayer(request.getLayerName());
        return layerSource.map(ls -> mkResponse(request, ls))
                .orElse( futureNotFoundResponse() );
    }

    private CompletableFuture<HttpResponse> mkResponse(GetMapRequest request, Layer layerSource) {
        return layerSource.createMapImage(request)
                .thenApply(img -> imageToResponse(img, request.getImageFormat()));
    }


}
