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

import org.geolatte.mapserver.core.ImageFormat;
import org.geolatte.mapserver.tilemap.TileImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

class BasicTileImage implements TileImage {
    final private BufferedImage image;

    public BasicTileImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage getInternalRepresentation() {
        return this.image;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(OutputStream os, ImageFormat format) throws IOException {
        ImageIO.write(image, format.toString(), os);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(OutputStream os, String mimetype) throws IOException {
        ImageFormat format = ImageFormat.forMimeType(mimetype);
        write(os, format);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWidth() {
        return this.image.getWidth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHeight() {
        return this.image.getHeight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMinX() {
        return this.image.getMinX();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMinY() {
        return this.image.getMinY();
    }

}
