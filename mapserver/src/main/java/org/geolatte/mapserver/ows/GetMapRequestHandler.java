package org.geolatte.mapserver.ows;

import org.geolatte.mapserver.Layer;
import org.geolatte.mapserver.LayerRegistry;
import org.geolatte.mapserver.RequestHandler;
import org.geolatte.mapserver.http.BasicHttpResponse;
import org.geolatte.mapserver.http.HttpResponse;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.image.ImageFormat;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public class GetMapRequestHandler implements RequestHandler<HttpResponse> {


    private final GetMapRequest request;
    private final LayerRegistry layerRegistry;

    GetMapRequestHandler(GetMapRequest request, LayerRegistry layerRegistry) {
        this.request = request;
        this.layerRegistry = layerRegistry;
    }

    @Override
    public HttpResponse handle() {
        Optional<Layer> layerSource = layerRegistry.getLayer(request.getLayerName());
        if (!layerSource.isPresent()) {
            return buildNotFoundResponse();
        }
        return buildOkResponse(request, layerSource.get());
    }

    private HttpResponse buildOkResponse(GetMapRequest request, Layer layerSource) {
        BasicHttpResponse.Builder builder = BasicHttpResponse.builder();
        Image img = layerSource.createMapImage(request);
        formatResponse(request, builder, img);
        return builder.build();
    }

    private HttpResponse buildNotFoundResponse() {
        BasicHttpResponse.Builder builder = BasicHttpResponse.builder();
        builder.NotFound();
        return builder.build();
    }

    private void formatResponse(GetMapRequest request, BasicHttpResponse.Builder builder, Image img) {
        builder.ok()
                .setHeader("Content-type", request.getImageFormat().getMimeType())
                .body(toBytes(img, request.getImageFormat()));
    }

    private byte[] toBytes(Image img, ImageFormat fmt) {
        try {
            return img.toByteArray(fmt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
