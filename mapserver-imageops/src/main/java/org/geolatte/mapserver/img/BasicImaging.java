package org.geolatte.mapserver.img;

import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.mapserver.core.ImageFormat;
import org.geolatte.mapserver.spi.Imaging;
import org.geolatte.mapserver.tilemap.MapUnitToPixelTransform;
import org.geolatte.mapserver.tilemap.TileImage;
import org.geolatte.mapserver.util.PixelRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.InputStream;
import java.util.Set;

/**
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public class BasicImaging implements Imaging {

    private static final Logger logger = LoggerFactory.getLogger(BasicImaging.class);

    //TODO -- do we still need tileImageFormat??
    @Override
    public TileImage createEmptyImage(Dimension dimension, ImageFormat tileImageFormat) {
        BufferedImage img = createEmptyImage(dimension);
        return new BasicTileImage(img);
    }

    private BufferedImage createEmptyImage(Dimension dimension) {
        return new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public TileImage createEmptyImage(TileImage baseImage, Dimension dimension) {
        BufferedImage img = createCompatibleBufferedImage(baseImage, dimension);
        return new BasicTileImage(img);
    }

    private BufferedImage createCompatibleBufferedImage(TileImage baseImage, Dimension dimension) {
        BufferedImage img = (BufferedImage) baseImage.getInternalRepresentation();
        WritableRaster raster = img.getData().createCompatibleWritableRaster(dimension.width, dimension.height);
        return new BufferedImage(img.getColorModel(), raster, img.isAlphaPremultiplied(), null);
    }

    @Override
    public TileImage mosaic(Set<TileImage> images, PixelRange imgBounds) {
        return null;
    }

    @Override
    public TileImage crop(TileImage source, PixelRange cropBnds) {
        BufferedImage srcImg = (BufferedImage) source.getInternalRepresentation();
        return new BasicTileImage(srcImg.getSubimage(cropBnds.getMinX(),
                cropBnds.getMinY(),
                cropBnds.getWidth(),
                cropBnds.getHeight())
        );
    }

    @Override
    public TileImage scale(TileImage source, Dimension dimension) {

        float xScale = (float) (dimension.getWidth() / source.getWidth());
        float yScale = (float) (dimension.getHeight() / source.getHeight());
        if (xScale == 1.0f && yScale == 1.0f) return source;
        logger.debug(String.format("Source dimenseion: %s; target dimension: %s", source.getDimension(), dimension));
        logger.debug(String.format("needed to rescale image in x-dim: %f, y-dim: %f", xScale, yScale));
        BufferedImage target = createCompatibleBufferedImage(source, dimension);
        Graphics2D graphics2D = (Graphics2D) target.getGraphics();
        graphics2D.scale(xScale, yScale);
        graphics2D.drawImage((BufferedImage) source.getInternalRepresentation(), 0, 0, null);
        graphics2D.dispose();;
        return new BasicTileImage(target);
    }

    @Override
    public TileImage overlay(TileImage empty, TileImage result) {
        return null;
    }

    @Override
    public TileImage affineTransform(TileImage result, AffineTransform atf) {
        return null;
    }

    @Override
    public TileImage read(InputStream is, int minX, int minY, boolean forceArgb) {
        return null;
    }

    @Override
    public TileImage reprojectByWarping(TileImage srcImg, MapUnitToPixelTransform mupTransform, CrsId srs, CoordinateReferenceSystem<?> requestedSRS, MapUnitToPixelTransform targetMupTransform, double v) {
        throw new UnsupportedOperationException("Warping is not supported");
    }
}
