package org.geolatte.mapserver;

import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.image.Imaging;
import org.geolatte.mapserver.ows.GetLegendGraphicRequest;
import org.geolatte.mapserver.ows.GetMapRequest;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.geolatte.mapserver.http.HttpResponseHelper.imageToResponse;

/**
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public interface Layer {

    String getName();

    CompletableFuture<Image> createMapImage(GetMapRequest request);

    default CompletableFuture<Image> createLegendGraphic(GetLegendGraphicRequest request, Imaging imaging) {
        Image img = imaging.createEmptyImage(request.getDimension(), request.getImageFormat());
        return completedFuture(img);
    }
}
