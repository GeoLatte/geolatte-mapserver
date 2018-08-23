package org.geolatte.mapserver.protocols;

import org.geolatte.mapserver.image.ImageFormat;
import org.geolatte.mapserver.ows.GetLegendGraphicRequest;

import java.awt.*;

/**
 * Created by Karel Maesen, Geovise BVBA on 05/07/2018.
 */
public class OsmGetLegendGraphicRequest implements GetLegendGraphicRequest {

    private final String layerName;
    private final Dimension dimension;
    private final ImageFormat imageFormat;

    public OsmGetLegendGraphicRequest(String layerName, Dimension dimension, String format) {
        this.layerName = layerName;
        this.dimension = dimension;
        this.imageFormat = ImageFormat.forMimeType(format);
    }

    @Override
    public String getLayerName() {
        return layerName;
    }

    @Override
    public Dimension getDimension() {
        return dimension;
    }

    @Override
    public ImageFormat getImageFormat() {
        return imageFormat;
    }
}
