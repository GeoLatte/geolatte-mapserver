package org.geolatte.mapserver.img;

import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.image.ImageFormat;
import org.geolatte.mapserver.image.Imaging;
import org.geolatte.mapserver.tilemap.MapUnitToPixelTransform;
import org.geolatte.mapserver.util.PixelRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.awt.image.AffineTransformOp.TYPE_BICUBIC;
import static java.lang.String.format;

/**
 * Created by Karel Maesen, Geovise BVBA on 06/07/2018.
 */
class BasicImaging implements Imaging {

    private static final Logger logger = LoggerFactory.getLogger(BasicImaging.class);

    private final static RenderingHints colorConvertOpHints = new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);


    @Override
    public Image createEmptyImage(Dimension dimension, ImageFormat tileImageFormat) {
        BufferedImage img = createEmptyImage(dimension);
        return new BasicImage(img, 0, 0);
    }

    private BufferedImage createEmptyImage(Dimension dimension) {
        return new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public Image createEmptyImage(Image baseImage, Dimension dimension) {
        BufferedImage img = createCompatibleBufferedImage(baseImage, dimension);
        return new BasicImage(img, 0, 0);
    }

    private BufferedImage createCompatibleBufferedImage(Image baseImage, Dimension dimension) {
        BufferedImage img = (BufferedImage) baseImage.getInternalRepresentation();
        WritableRaster raster = img.getData().createCompatibleWritableRaster(dimension.width, dimension.height);
        return new BufferedImage(img.getColorModel(), raster, img.isAlphaPremultiplied(), null);
    }

    @Override
    public Image mosaic(java.util.List<Image> images, PixelRange imgBounds) {
        if (images.isEmpty()) throw new IllegalArgumentException("Require at least one image");
        BasicImage baseTile = (BasicImage) images.get(0);
        if (imgBounds.getWidth() == 0 || imgBounds.getHeight() == 0) {
            return createEmptyImage(baseTile, atLeastOnePixel(imgBounds));
        }
        BufferedImage baseImage = getIndexedColorsConverted(baseTile);
        Dimension dimension = imgBounds.getDimension();
        WritableRaster raster = baseImage.getData().createCompatibleWritableRaster(dimension.width, dimension.height);
        BufferedImage res = new BufferedImage(baseImage.getColorModel(), raster, baseImage.isAlphaPremultiplied(), null);
        mosaic(images, imgBounds, res);
        return new BasicImage(res, imgBounds.getMinX(), imgBounds.getMinY());
    }

    private Dimension atLeastOnePixel(PixelRange imgBounds) {
        return new Dimension(Math.max(imgBounds.getWidth(), 1), Math.max(imgBounds.getHeight(), 1));
    }

    private void mosaic(List<Image> images, PixelRange imgBounds, BufferedImage target) {
        //Note -- this was first implemented with a setRect() directly on the image raster, but this
        //    failed on jpeg images (pixel values at source and target were different after setRect).
        Graphics2D g2 = (Graphics2D) target.getGraphics();
        for (Image ti : images) {
            BufferedImage current = getIndexedColorsConverted(ti);
            int tx = ti.getMinX() - imgBounds.getMinX();
            int ty = ti.getMinY() - imgBounds.getMinY();
            g2.drawImage(current, tx, ty, null);
        }
        g2.dispose();
    }

    /**
     * In the case of an IndexColorModel, we immediately transform to explicit RGB values, otherwise image colors won't
     * match when copying data elements from source to destination raster.
     *
     * @param image
     * @return
     */
    private BufferedImage getIndexedColorsConverted(Image image) {
        BufferedImage bi = image.getInternalRepresentation(BufferedImage.class);
        if (IndexColorModel.class.isAssignableFrom(bi.getColorModel().getClass())) {
            bi = ((IndexColorModel) bi.getColorModel()).convertToIntDiscrete(bi.getData(), false);
        }
        return bi;
    }

    @Override
    public Image crop(Image source, PixelRange cropBnds) {
        BufferedImage srcImg = (BufferedImage) source.getInternalRepresentation();
        int minX = cropBnds.getMinX() - source.getMinX();
        int minY = cropBnds.getMinY() - source.getMinY();
        BufferedImage cropped = srcImg.getSubimage(minX,
                minY,
                cropBnds.getWidth(),
                cropBnds.getHeight());
        return new BasicImage(cropped, cropBnds.getMinX(), cropBnds.getMinY());
    }

    @Override
    public Image scale(Image source, Dimension dimension) {
        float xScale = (float) (dimension.getWidth() / source.getWidth());
        float yScale = (float) (dimension.getHeight() / source.getHeight());
        if (xScale == 1.0f && yScale == 1.0f) return source;
        logger.debug(format("Source dimenseion: %s; target dimension: %s", source.getDimension(), dimension));
        logger.debug(format("needed to rescale image in x-dim: %f, y-dim: %f", xScale, yScale));
        BufferedImage target = createCompatibleBufferedImage(source, dimension);
        Graphics2D graphics2D = (Graphics2D) target.getGraphics();
        graphics2D.scale(xScale, yScale);
        graphics2D.drawImage((BufferedImage) source.getInternalRepresentation(), 0, 0, null);
        graphics2D.dispose();
        return new BasicImage(target, source.getMinX(), source.getMinY());
    }

    @Override
    public Image overlay(Image src1, Image src2) {
        BufferedImage img1 = get(src1);
        BufferedImage img2 = get(src2);

        BufferedImage target = clone(img1);
        try {
            overlayByGraphicsDrawImage(img2, target);
        } catch (RuntimeException e) {
            logger.warn("Failure on overlay Operation", e);
            logger.debug("Attempting Overlay by pixel copy");
            overlayByPixelCopy(img1, img2, target);
        }
        return new BasicImage(target, src1.getMinX(), src1.getMinY());
    }

    private void overlayByGraphicsDrawImage(BufferedImage img2, BufferedImage target) {
        Graphics2D g = (Graphics2D) target.getGraphics();
        try {
            g.drawImage(img2, 0, 0, null);
        } finally {
            g.dispose();
        }
    }

    /**
     * Overlay pixel-by-pixel which is necessary if the ColorModels of the source images are incompatible
     *
     * @param src1   background image
     * @param src2   foreground image
     * @param target overlay result
     */
    private void overlayByPixelCopy(BufferedImage src1, BufferedImage src2, BufferedImage target) {
        int width = src2.getWidth();
        int height = src2.getHeight();
        int startX = src2.getMinX();
        int startY = src2.getMinY();

        Object tdata = target.getRaster().getDataElements(0, 0, null);
        Raster source = src2.getRaster();
        WritableRaster raster = target.getRaster();
        Object srcBuffer = source.getDataElements(0, 0, null);
        ColorModel srcModel = src1.getColorModel();
        ColorModel trgtModel = target.getColorModel();
        for (int i = startX; i < startX + width; i++) {
            for (int j = startY; j < startY + height; j++) {
                source.getDataElements(i, j, srcBuffer);
                if (srcModel.getAlpha(srcBuffer) == 0) continue;
                int rgb = srcModel.getRGB(srcBuffer);
                trgtModel.getDataElements(rgb, tdata);
                raster.setDataElements(i, j, tdata);
            }
        }
    }

    private BufferedImage clone(BufferedImage source) {
        WritableRaster raster = (WritableRaster) source.getData();
        return new BufferedImage(source.getColorModel(), raster, source.isAlphaPremultiplied(), null);
    }

    @Override
    public Image affineTransform(Image image, AffineTransform atf) {
        BufferedImage src = get(image);
        AffineTransformOp op = new AffineTransformOp(atf, TYPE_BICUBIC);
        double[] box = new double[4];
        image.toArray(box);
        atf.transform(box, 0, box, 0, 2);
        PixelRange trPixelRange = PixelRange.fromArray(box);
        BufferedImage dst = createCompatibleBufferedImage(image, trPixelRange.getDimension());
        op.filter(src, dst);
        return new BasicImage(dst, trPixelRange.getMinX(), trPixelRange.getMinY());
    }

    @Override
    public Image affineTransform(Image image, int tx, int ty, double sx, double sy) {
        BufferedImage src = get(image);
        BufferedImage scaled;
        if (sx == 1.0d && sy == 1.0d) {
            scaled = src;
        } else {
            AffineTransform atf = new AffineTransform(sx, 0, 0, sy, 0, 0);
            AffineTransformOp op = new AffineTransformOp(atf, TYPE_BICUBIC);
            scaled = op.filter(src, null);
        }
        return new BasicImage(scaled, image.getMinX() + tx, image.getMinY() + ty);
    }

    @Override
    public Image read(InputStream is, int minX, int minY, boolean forceArgb) throws IOException {
        BufferedImage bi = ImageIO.read(is);

        if (forceArgb) {
            BufferedImage rgbImage = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
            rgbImage.createGraphics().drawImage(bi, 0, 0, null, null);
            bi = rgbImage;
        }

        return new BasicImage(bi, minX, minY);
    }

    @Override
    public Image fromRenderedImage(RenderedImage image) {
        if(image instanceof BufferedImage) {
            return new BasicImage((BufferedImage) image, 0, 0);
        } else {
            throw new UnsupportedOperationException("Not supported conversion");
        }
    }

    @Override
    public Image reprojectByWarping(Image srcImg, MapUnitToPixelTransform mupTransform, CrsId srs, CoordinateReferenceSystem<?> requestedSRS, MapUnitToPixelTransform targetMupTransform, double v) {
        throw new UnsupportedOperationException("Warping is not supported");
    }

    private BufferedImage get(Image tile) {
        return tile.getInternalRepresentation(BufferedImage.class);
    }
}
