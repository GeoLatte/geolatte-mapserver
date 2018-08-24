package org.geolatte.mapserver.http;

import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.image.ImageFormat;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Utilties to simnplify building HttpResponses
 *
 * Created by Karel Maesen, Geovise BVBA on 24/08/2018.
 */
public class HttpResponseHelper {

    public static HttpResponse notFoundResponse() {
        BasicHttpResponse.Builder builder = BasicHttpResponse.builder();
        builder.NotFound();
        return builder.build();
    }

    public static CompletableFuture<HttpResponse> futureNotFoundResponse() {
        return CompletableFuture.completedFuture(notFoundResponse());
    }

    public static HttpResponse imageToResponse(Image img, ImageFormat fmt) {
        return BasicHttpResponse.builder()
                .ok()
                .setHeader("Content-type", fmt.getMimeType())
                .body(toBytes(img, fmt))
                .build();
    }

    private static byte[] toBytes(Image img, ImageFormat fmt) {
        try {
            return img.toByteArray(fmt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}