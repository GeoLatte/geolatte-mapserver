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

import org.geolatte.mapserver.image.ImageFormat;
import org.geolatte.mapserver.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

class BasicImage implements Image {
    final private BufferedImage image;
    final int minX;
    final int minY;

    public BasicImage(BufferedImage image, int minX, int minY) {
        this.image = image;
        this.minX = minX;
        this.minY = minY;
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
        return this.minX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMinY() {
        return this.minY;
    }



}
