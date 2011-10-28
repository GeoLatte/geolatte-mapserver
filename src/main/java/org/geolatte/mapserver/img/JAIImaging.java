/*
 * Copyright 2009-2010  Geovise BVBA, QMINO BVBA
 *
 * This file is part of GeoLatte Mapserver.
 *
 * GeoLatte Mapserver is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GeoLatte Mapserver is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GeoLatte Mapserver.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.geolatte.mapserver.img;

import org.apache.log4j.Logger;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.mapserver.referencing.Referencing;
import org.geolatte.mapserver.referencing.ReferencingException;
import org.geolatte.mapserver.tms.MapUnitToPixelTransform;
import org.geolatte.mapserver.tms.TileImage;
import org.geolatte.mapserver.util.PixelRange;

import javax.imageio.ImageIO;
import javax.media.jai.*;
import javax.media.jai.operator.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JAIImaging implements Imaging {

    private final static Logger LOGGER = Logger.getLogger(JAIImaging.class);


    /**
     * {@inheritDoc}
     */
    @Override
    public TileImage affineTransform(TileImage source, AffineTransform atf) {
        PlanarImage img = (PlanarImage) source.getInternalRepresentation();
        Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        RenderedOp transformed = AffineDescriptor.create(img, atf, interp, null, null);
        return new JAITileImage(transformed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TileImage overlay(TileImage source1, TileImage source2) {
        PlanarImage s1 = (PlanarImage) source1.getInternalRepresentation();
        PlanarImage s2 = (PlanarImage) source2.getInternalRepresentation();
        RenderedOp overlayOp = OverlayDescriptor.create(s1, s2, null);
        return new JAITileImage(overlayOp);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TileImage reprojectByWarping(TileImage source, MapUnitToPixelTransform mupSrcTransform, CrsId sourceSRS, CrsId targetSRS, MapUnitToPixelTransform mupTargetTransform, double tolerance) {
        try {
            //Derive the target-to-source transform
            Warp warp = Referencing.createWarpApproximation(mupSrcTransform, sourceSRS, targetSRS, mupTargetTransform, tolerance);

            //do the warp
            PlanarImage img = (PlanarImage)source.getInternalRepresentation();
            Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_BILINEAR);
            RenderedOp warped = WarpDescriptor.create(img, warp, interp, null, null);
            return new JAITileImage(warped);

        } catch (ReferencingException e) {
            throw new RuntimeException(e);
        }
    }

    private PlanarImage createEmptyImage(Dimension dimension, SampleModel sm, ColorModel cm) {
        if (cm.getTransferType() != DataBuffer.TYPE_BYTE)
            throw new UnsupportedOperationException("Only byte transfer type allowed in Color/Sample models");
        PlanarImage emptyImage = new TiledImage(0, 0, (int) dimension.getWidth(), (int) dimension.getHeight(), 0, 0, sm, cm);
        if (!cm.hasAlpha()) { //create a completely white image
            ParameterBlock pb = new ParameterBlock();
            pb.addSource(emptyImage);
            double[] constants = createWhiteConstantsArray(sm, cm);
            pb.add(constants);
            return JAI.create("addconst", pb, null);
        }
        return emptyImage;
    }

    /**
     * Creates a double-array that can be used to set the background color of an empty image to white.
     *
     * @param sm
     * @param cm
     * @return
     */
    private double[] createWhiteConstantsArray(SampleModel sm, ColorModel cm) {
        double[] constants = new double[sm.getNumBands()];
        // DataElements are byte-arrays because transfertype must be byte.
        byte[] bytes = (byte[]) cm.getDataElements(cm.getColorSpace().fromRGB(new float[]{1f, 1f, 1f}), 0, null);
        int i = 0;
        for (byte b : bytes) {
            //bytes are signed (two-complement), but values in Raster are treated as unsigned.
            //We resolve this by shifting values right in an int, and cast to double.
            //(if we didn't do this, we'd be adding -1 rather than 255 in the case of RGB colormodels.)
            int v = b >>> 8;
            constants[i++] = v;
        }
        return constants;
    }

    /**
     * {@inheritDoc}
     */
    public TileImage createEmptyImage(TileImage template, Dimension dimension) {
        PlanarImage img = (PlanarImage) template.getInternalRepresentation();
        SampleModel sm = img.getSampleModel();
        ColorModel cm = img.getColorModel();
        PlanarImage emptyImage = createEmptyImage(dimension, sm, cm);
        return new JAITileImage(emptyImage);
    }

    /**
     * {@inheritDoc}
     */
    public TileImage createEmptyImage(Dimension dimension, ImageFormat format) {
        SampleModel sm;
        ColorModel cm;
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        if (ImageFormat.JPEG.equals(format)) {
            sm = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, (int) dimension.getWidth(), (int) dimension.getHeight(), 3, 3 * (int) dimension.getWidth(), new int[]{0, 1, 2});
            cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        } else if (ImageFormat.PNG.equals(format)) {
            sm = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, (int) dimension.getWidth(), (int) dimension.getHeight(), 4, 4 * (int) dimension.getWidth(), new int[]{0, 1, 2, 3});
            cm = new ComponentColorModel(cs, true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
        } else {
            throw new IllegalArgumentException(String.format("ImageFormat null or Format not supported: %s", format));
        }
        PlanarImage img = createEmptyImage(dimension, sm, cm);
        return new JAITileImage(img);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TileImage read(InputStream is, int x, int y) throws IOException {
        BufferedImage bi = ImageIO.read(is);
        RenderedOp op = TranslateDescriptor.create(bi, (float) x, (float) y, null, null);
        PlanarImage image = op.createInstance();
        return new JAITileImage(image);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TileImage mosaic(Set<TileImage> images, PixelRange bounds) {
        ImageLayout layout = toImageLayout(bounds);
        RenderedImage[] sources = new RenderedImage[images.size()];
        int i = 0;
        for (TileImage ti : images) {
            sources[i++] = (RenderedImage) ti.getInternalRepresentation();
        }
        RenderingHints hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);
        RenderedOp mosaicOp = MosaicDescriptor.create(sources, MosaicDescriptor.MOSAIC_TYPE_OVERLAY, null, null, null, null, hints);
        return new JAITileImage(mosaicOp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TileImage crop(TileImage image, PixelRange cropBounds) {
        PlanarImage source = (PlanarImage) image.getInternalRepresentation();
        RenderedOp cropOp = CropDescriptor.create(source,
                (float) cropBounds.getMinX(),
                (float) cropBounds.getMinY(),
                (float) cropBounds.getWidth(),
                (float) cropBounds.getHeight(), null);
        return new JAITileImage(cropOp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TileImage scale(TileImage image, Dimension dimension) {
        float xScale = (float) (dimension.getWidth() / image.getWidth());
        float yScale = (float) (dimension.getHeight() / image.getHeight());
        if (xScale == 1.0f && yScale == 1.0f) return image;
        LOGGER.debug(String.format("needed to rescale image in x-dim: %f, y-dim: %f", xScale, yScale));
        return scale(image, xScale, yScale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TileImage scale(TileImage image, float xScale, float yScale) {
        PlanarImage source = (PlanarImage) image.getInternalRepresentation();
        Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_BICUBIC_2);
        Map<String, String> map = new HashMap<String, String>();
        RenderingHints hints = new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));

        RenderedOp scaleOp = ScaleDescriptor.create(source, xScale, yScale, 0.0f, 0.0f, interp, hints);
        TileImage result = new JAITileImage(scaleOp);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    private ImageLayout toImageLayout(PixelRange boundsTile) {
        return new ImageLayout(boundsTile.getMinX(),
                boundsTile.getMinY(),
                boundsTile.getWidth(),
                boundsTile.getHeight());
    }
}
