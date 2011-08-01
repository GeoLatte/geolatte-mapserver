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

import org.geolatte.mapserver.tms.MapUnitToPixelTransform;
import org.geolatte.mapserver.tms.TileImage;
import org.geolatte.mapserver.util.PixelRange;
import org.geolatte.mapserver.util.SRS;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * An API for the creation and manipulation of {@link TileImage}s.
 * <p/>
 * <p>Implementations are required to be thread-safe.
 * </p>
 *
 * @author Karel Maesen, Geovise BVBA
 */
public interface Imaging {

    /**
     * Scales a <code>TileImage</code> to the specified <code>Dimension</code>s
     *
     * @param image     the <code>TileImage</code> to scale
     * @param dimension the dimensions to which to scale
     * @return the scaled <code>TileImage</code>
     */
    public abstract TileImage scale(TileImage image, Dimension dimension);

    /**
     * Scales a <code>TileImage</code> with the specified scale-factors.
     *
     * @param image  the <code>TileImage</code> to scale
     * @param xScale the scale-factor along the X-axis
     * @param yScale the scale-factor along the Y-axis
     * @return the scaled <code>TileImage</code>
     */
    public abstract TileImage scale(TileImage image, float xScale, float yScale);

    /**
     * Reads a <code>TileImage</code> from an <code>InputStream</code>.
     *
     * @param inStream <code>InputStream</code> from which the <code>TileImage</code> is read
     * @param x        the minimum X-coordinate for the <code>TileImage</code>
     * @param y        the minimum Y-coordinate for the <code>TileImage</code>
     * @return the <code>TileImage</code> provided by the input stream
     * @throws IOException
     */
    public abstract TileImage read(InputStream inStream, int x, int y) throws IOException;

    /**
     * Mosaics a set of <code>TileImage</code>s.
     * <p/>
     * <p>The resulting <code>TileImage</code> has the specified <code>PixelRange</code>.
     * </p>
     *
     * @param images <code>TileImages</code> to mosaic.
     * @param bounds the <code>PixelRange</code> for the mosaic
     * @return the mosaic of the input <code>TileImages</code>
     */
    public abstract TileImage mosaic(Set<TileImage> images, PixelRange bounds);

    /**
     * Crops a <code>TileImage</code> to the specified <code>PixelRange</code>.
     *
     * @param image      the <code>TileImge</code> to crop
     * @param cropBounds the <code>PixelRange</code> to which to crop
     * @return the cropped <code>TileImage</code>
     */
    public abstract TileImage crop(TileImage image, PixelRange cropBounds);

    /**
     * Creates an empty image with the same characteristics as the specified
     * template <code>TileImage</code>.
     * <p/>
     * <p>If the template <code>TileImage</code> supports transparency, then the
     * created <code>TileImage</code> must be transparent. If not, then it
     * should be completely white.
     *
     * @param template  <code>TileImage</code> from which to copy the image characteristics.
     * @param dimension the dimensions of the empty <code>TileImage</code>
     * @return a fully transparent, or completely white <code>TileImage</code>
     */
    public abstract TileImage createEmptyImage(TileImage template, Dimension dimension);

    /**
     * Creates an empty image of the specified <code>ImageFormat</code>.
     * <p/>
     * <p>If the specified format supports transparency, then the
     * created <code>TileImage</code> must be transparent. If not, then it
     * should be completely white.
     *
     * @param dimension the dimensions of the empty <code>TileImage</code>
     * @param format    the <code>ImageFormat</code> to which the result can be written
     * @return a fully transparent, or completely white <code>TileImage</code>, depending
     *         on whether the format supports transparency or not
     */
    public abstract TileImage createEmptyImage(Dimension dimension, ImageFormat format);

    /**
     * Applies an <code>AffineTransform</code> to the specified <code>TileImage</code>.
     *
     * @param source the <code>TileImage</code> to be transformed
     * @param atf    the transform to be applied
     * @return the result of applying the transform to the
     *         source <code>TileImage</code>
     */
    public abstract TileImage affineTransform(TileImage source, AffineTransform atf);

    /**
     * Overlays to <code>TileImages</code>.
     *
     * @param source1 the first <code>TileImage</code>
     * @param source2 the <code>TileImage</code> which is to be overlayed on top of the first.
     * @return the overlay of the source <code>TileImage</code>s and having the same <code>PixelRange</code>
     *         as the first source.
     */
    public TileImage overlay(TileImage source1, TileImage source2);


    /**
     * Approximates a reprojection of the source <code>TileImage</code> from source to target <code>SRS</code>
     * by warping (rubber-sheeting).
     *
     * @param source the source image
     * @param sourceSRS the source <code>SRS</code>
     * @param mupSrcTransform
     * @param targetSRS the target <code>SRS</code>
     * @param mupTargetTransform
     * @param tolerance the tolerance for the approximation
     * @return
     */
    public TileImage reprojectByWarping(TileImage source, MapUnitToPixelTransform mupSrcTransform, SRS sourceSRS, SRS targetSRS,  MapUnitToPixelTransform mupTargetTransform, double tolerance);
}
