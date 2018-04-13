package org.geolatte.mapserver.core;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CrsId;

import java.awt.*;

/**
 *
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public class MapRequest implements MapServerRequest{

    private Envelope<C2D> bbox;
    private String layerName;
    private CrsId crs;
    private Dimension dimension;
    private boolean transparent;
    private Color backgroundColor;
    private Style style;


    public MapRequest(Envelope<C2D> bbox, String layerName, CrsId crs, Dimension dimension, boolean transparent, Color backgroundColor, Style style) {
        this.bbox = bbox;
        this.layerName = layerName;
        this.crs = crs;
        this.dimension = dimension;
        this.transparent = transparent;
        this.backgroundColor = backgroundColor;
        this.style = style;
    }


    public Envelope<C2D> getBbox() {
        return bbox;
    }

    public String getLayerName() {
        return layerName;
    }

    public CrsId getCrs() {
        return crs;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Style getStyle() {
        return style;
    }
}
