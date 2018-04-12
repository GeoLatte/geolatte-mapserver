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

/**
 * Specifies common file image formats.
 */
public enum ImageFormat {

    PNG("image/png"),
    JPEG("image/jpeg");

    private String mimeType;


    ImageFormat(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Returns the <code>ImageFormat</code> associated with the
     * specified MIME-type.
     *
     * @param mime MIME-type of the <code>ImageFormat</code> as string
     * @return the <code>ImageFormat</code> having the MIME-type specified by the <code>String</code> argument.
     */
    public static ImageFormat forMimeType(String mime) {
        for (ImageFormat f : values()) {
            if (f.getMimeType().equalsIgnoreCase(mime)) return f;
        }
        throw new IllegalArgumentException(String.format("Can't map %s to MIME type of a known image format", mime));
    }

    /**
     * Returns the MIME-type for this <code>ImageFormat</code>.
     *
     * @return the MIME-type
     */
    public String getMimeType() {
        return this.mimeType;
    }

}
