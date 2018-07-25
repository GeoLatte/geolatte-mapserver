package org.geolatte.mapserver.ows;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.mapserver.image.ImageFormat;

import java.awt.*;

/**
 * Marker interface for requests
 *
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public interface GetMapRequest extends MapServerRequest{

    Envelope<C2D> getBbox();

    String getLayerName();

    CrsId getCrs();

    Dimension getDimension();

    boolean isTransparent();

    Color getBackgroundColor();

    String getStyle();

    ImageFormat getImageFormat();

}
