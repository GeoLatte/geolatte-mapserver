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

package org.geolatte.mapserver.tilemap;

import java.awt.*;

public class TileMetadata {
    final Dimension dimension;
    final String mimeType;
    final String extension;

    public TileMetadata(Dimension dimension, String mimeType, String extension) {
        this.dimension = new Dimension(dimension); //copy to prevent changes to dimension
        this.mimeType = mimeType;

        this.extension = normalise(extension);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TileMetadata that = (TileMetadata) o;

        if (dimension != null ? !dimension.equals(that.dimension) : that.dimension != null) return false;
        if (extension != null ? !extension.equals(that.extension) : that.extension != null) return false;
        if (mimeType != null ? !mimeType.equals(that.mimeType) : that.mimeType != null) return false;

        return true;
    }

    @Override
    public String toString() {
        return "TileMetadata{" +
                "dimension=" + dimension +
                ", mimeType='" + mimeType + '\'' +
                ", extension='" + extension + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        int result = dimension != null ? dimension.hashCode() : 0;
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        result = 31 * result + (extension != null ? extension.hashCode() : 0);
        return result;
    }

    private String normalise(String extension) {
        return (extension.startsWith(".")) ?
                extension.substring(1)
                : extension;
    }
}
