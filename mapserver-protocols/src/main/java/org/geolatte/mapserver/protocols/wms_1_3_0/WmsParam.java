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

package org.geolatte.mapserver.protocols.wms_1_3_0;

import java.util.Arrays;

/**
 * Enumeration of all possible WMS Request Parameters.
 *
 * @author Karel Maesen, Geovise BVBA
 */
public enum WmsParam {
    REQUEST,
    VERSION("WMTVER"),
    FORMAT,
    TRANSPARENT,
    WIDTH,
    HEIGHT,
    CRS("SRS"),
    SERVICE,
    BBOX,
    LAYERS,
    EXCEPTIONS,
    BGCOLOR,
    SLD,
    STYLES,
    UPDATESEQUENCE;


    private String[] names = new String[]{};

    WmsParam(String... altNames) {
        this.names = new String[altNames.length + 1];
        this.names[0] = this.toString();
        System.arraycopy(altNames, 0, this.names, 1, altNames.length);
    }

    public String[] getNames() {
        return Arrays.copyOf(names, names.length);
    }

}