package org.geolatte.mapserver.ows;

import org.geolatte.mapserver.image.ImageFormat;

import java.awt.*;

public interface GetLegendGraphicRequest extends MapServerRequest{

    String getLayerName();

    ImageFormat getImageFormat();

    Dimension getDimension();
}
