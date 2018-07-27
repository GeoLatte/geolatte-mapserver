package org.geolatte.mapserver.image;

import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.mapserver.tilemap.MapUnitToPixelTransform;
import org.geolatte.mapserver.util.PixelRange;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Created by Karel Maesen, Geovise BVBA on 12/04/2018.
 */
public interface Imaging {


    Image createEmptyImage(Dimension dimension, ImageFormat tileImageFormat);

    Image createEmptyImage(Image result, Dimension dimension);

    Image mosaic(java.util.List<Image> images, PixelRange imgBounds);

    /**
     * Crops a <code>TileImage</code> to the specified <code>PixelRange</code>.
     *
     * @param image      the <code>TileImge</code> to crop
     * @param cropBounds the <code>PixelRange</code> to which to crop
     * @return the cropped <code>TileImage</code>
     */
    Image crop(Image image, PixelRange cropBounds);

    /**
     * Scales a <code>TileImage</code> to the specified <code>Dimension</code>s
     *
     * @param image     the <code>TileImage</code> to scale
     * @param dimension the dimensions to which to scale
     * @return the scaled <code>TileImage</code>
     */
    Image scale(Image image, Dimension dimension);

    /**
     * Overlays to <code>TileImages</code>.
     *
     * <p>The overlay operation can be described by the following pseudo-code</p>
     * <code>
     *  if (srcs[1] contains the point (x, y)) {
     *      dst[x][y][b] = srcs[1][x][y][b];
     *  } else {
     *      dst[x][y][b] = srcs[0][x][y][b];
     *  }
     *</code>
     *
     * @param source1 the first <code>TileImage</code>
     * @param source2 the <code>TileImage</code> which is to be overlayed on top of the first.
     * @return the overlay of the source <code>TileImage</code>s and having the same <code>PixelRange</code>
     *         as the first source.
     */
    Image overlay(Image source1, Image source2);

    @Deprecated //TODO -- decide what to do with this
    Image affineTransform(Image image, AffineTransform atf);

    Image affineTransform(Image image, int tx, int ty, double sx, double sy);

    Image read(InputStream is, int minX, int minY, boolean forceArgb) throws IOException;

    Image fromRenderedImage(RenderedImage image);

    Image reprojectByWarping(Image srcImg, MapUnitToPixelTransform mupTransform, CrsId srs, CoordinateReferenceSystem<?> requestedSRS, MapUnitToPixelTransform targetMupTransform, double v);

}
