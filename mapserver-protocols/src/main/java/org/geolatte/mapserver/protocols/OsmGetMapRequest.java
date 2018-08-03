package org.geolatte.mapserver.protocols;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.mapserver.image.ImageFormat;
import org.geolatte.mapserver.ows.GetMapRequest;

import java.awt.*;

/**
 * Created by Karel Maesen, Geovise BVBA on 05/07/2018.
 */
public class OsmGetMapRequest implements GetMapRequest {

    private final Envelope<C2D> bbox;
    private final String layerName;
    private final CrsId crsId;
    private final Dimension dimension;
    private final boolean transparent;
    private final Color backgroundColor;
    private final String style; //TODO nees refactoring
    private final ImageFormat imageFormat;

    public OsmGetMapRequest(Envelope<C2D> bbox, String layerName, CrsId crsId, Dimension dimension, boolean isTransparent, Color backgroundColor, String style,
                            String format) {
        this.bbox = bbox;
        this.layerName = layerName;
        this.crsId = crsId;
        this.dimension = dimension;
        this.transparent = isTransparent;
        this.backgroundColor = backgroundColor;
        this.style = style;
        this.imageFormat = ImageFormat.forMimeType(format);
    }

    @Override
    public Envelope<C2D> getBbox() {
        return bbox;
    }

    @Override
    public String getLayerName() {
        return layerName;
    }

    @Override
    public CrsId getCrs() {
        return crsId;
    }

    @Override
    public Dimension getDimension() {
        return dimension;
    }

    @Override
    public boolean isTransparent() {
        return transparent;
    }

    @Override
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public String getStyle() {
        return style;
    }

    @Override
    public ImageFormat getImageFormat() {
        return imageFormat;
    }
}
