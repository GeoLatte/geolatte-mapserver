package org.geolatte.mapserver.ows;

import org.geolatte.mapserver.Layer;
import org.geolatte.mapserver.LayerRegistry;
import org.geolatte.mapserver.RequestHandler;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.http.BasicHttpResponse;
import org.geolatte.mapserver.http.HttpResponse;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.image.ImageFormat;
import org.geolatte.mapserver.image.Imaging;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
    Optional<Layer> layerSource = layerRegistry.getLayer(request.getLayerName());
    return layerSource.map(layer -> buildOkResponse(request, layer)).orElseGet(() -> CompletableFuture.completedFuture(buildNotFoundResponse()));
    //TODO handle failure case if any
  }

  private CompletableFuture<HttpResponse> buildOkResponse(GetLegendGraphicRequest request, Layer layerSource) {
    Image img = createMapImage(request, layerSource);
    return CompletableFuture.completedFuture(formatResponse(img, request.getImageFormat()));
  }

  private Image createMapImage(GetLegendGraphicRequest request, Layer layerSource) {
    return imaging.createEmptyImage(request.getDimension(), request.getImageFormat());
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
