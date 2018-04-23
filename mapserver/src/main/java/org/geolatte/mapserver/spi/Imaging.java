package org.geolatte.mapserver.spi;

import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.mapserver.core.ImageFormat;
import org.geolatte.mapserver.tilemap.MapUnitToPixelTransform;
import org.geolatte.mapserver.tilemap.TileImage;
import org.geolatte.mapserver.util.PixelRange;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.InputStream;
import java.util.Set;

/**
 * Created by Karel Maesen, Geovise BVBA on 12/04/2018.
 */
public interface Imaging {


    TileImage createEmptyImage(Dimension dimension, ImageFormat tileImageFormat);

    TileImage createEmptyImage(TileImage result, Dimension dimension);

    TileImage mosaic(Set<TileImage> images, PixelRange imgBounds);

    /**
     * Crops a <code>TileImage</code> to the specified <code>PixelRange</code>.
     *
     * @param image      the <code>TileImge</code> to crop
     * @param cropBounds the <code>PixelRange</code> to which to crop
     * @return the cropped <code>TileImage</code>
     */
    TileImage crop(TileImage image, PixelRange cropBounds);

    /**
     * Scales a <code>TileImage</code> to the specified <code>Dimension</code>s
     *
     * @param image     the <code>TileImage</code> to scale
     * @param dimension the dimensions to which to scale
     * @return the scaled <code>TileImage</code>
     */
    TileImage scale(TileImage image, Dimension dimension);


    TileImage overlay(TileImage empty, TileImage result);

    TileImage affineTransform(TileImage result, AffineTransform atf);

    TileImage read(InputStream is, int minX, int minY, boolean forceArgb);

    TileImage reprojectByWarping(TileImage srcImg, MapUnitToPixelTransform mupTransform, CrsId srs, CoordinateReferenceSystem<?> requestedSRS, MapUnitToPixelTransform targetMupTransform, double v);

}
