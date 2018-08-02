package org.geolatte.mapserver.ows;

import org.geolatte.mapserver.Layer;
import org.geolatte.mapserver.LayerRegistry;
import org.geolatte.mapserver.RequestHandler;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.http.BasicHttpResponse;
import org.geolatte.mapserver.http.HttpResponse;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.image.ImageFormat;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
        if (!layerSource.isPresent()) {
            return CompletableFuture.completedFuture(buildNotFoundResponse());
        }
        //TODO handle failure case if any
        return buildOkResponse(request, layerSource.get());
    }

    private CompletableFuture<HttpResponse> buildOkResponse(GetMapRequest request, Layer layerSource) {
        return layerSource.createMapImage(request)
                .thenApply(img ->  formatResponse(img, request.getImageFormat()));
    }

    private HttpResponse buildNotFoundResponse() {
        BasicHttpResponse.Builder builder = BasicHttpResponse.builder();
        builder.NotFound();
        return builder.build();
    }

    private HttpResponse formatResponse(Image img, ImageFormat fmt) {
        return BasicHttpResponse.builder()
                .ok()
                .setHeader("Content-type", fmt.getMimeType())
                .body(toBytes(img, fmt))
                .build();
    }

    private byte[] toBytes(Image img, ImageFormat fmt) {
        try {
            return img.toByteArray(fmt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
