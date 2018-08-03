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

package org.geolatte.mapserver.image;

import org.geolatte.mapserver.util.PixelRange;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents the image of an tile
 *
 * @author Karel Maesen, Geovise BVBA
 */
public interface Image {

    public Object getInternalRepresentation();



    @SuppressWarnings("unchecked")
    default public <T> T getInternalRepresentation(Class<T> rep) {
        return (T) getInternalRepresentation();
    }

    public void write(OutputStream os, ImageFormat format) throws IOException;

    public int getWidth();

    public int getHeight();

    public int getMinX();

    public int getMinY();

    default byte[] toByteArray(ImageFormat format) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            write(os, format);
            return os.toByteArray();
        }
    }

    default Dimension getDimension() {
        return new Dimension(getWidth(), getHeight());
    }

    default PixelRange getPixelRange(){
        return new PixelRange(getMinX(), getMinY(), getWidth(), getHeight());
    }

    default double[] toArray(double[] arr){
        return getPixelRange().toArray(arr);
    }
}
