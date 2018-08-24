package org.geolatte.mapserver.ows;

import org.geolatte.mapserver.Layer;
import org.geolatte.mapserver.LayerRegistry;
import org.geolatte.mapserver.RequestHandler;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.http.HttpResponse;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.image.Imaging;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.geolatte.mapserver.http.HttpResponseHelper.futureNotFoundResponse;
import static org.geolatte.mapserver.http.HttpResponseHelper.imageToResponse;

public class GetLegendGraphicRequestHandler implements RequestHandler<HttpResponse> {

    private final GetLegendGraphicRequest request;
    private final LayerRegistry layerRegistry;
    private final Imaging imaging;

    GetLegendGraphicRequestHandler(GetLegendGraphicRequest request, ServiceLocator serviceLocator) {
        this.request = request;
        this.layerRegistry = serviceLocator.layerRegistry();
        this.imaging = serviceLocator.imaging();
    }

    @Override
    public CompletableFuture<HttpResponse> handle() {
        //TODO handle failure case if any
        return layerRegistry.getLayer(request.getLayerName())
                .map(layer -> layer.createLegendGraphic(request, imaging)
                        .thenApply( img -> imageToResponse(img, request.getImageFormat()))
                ).orElse(futureNotFoundResponse());
    }

}
