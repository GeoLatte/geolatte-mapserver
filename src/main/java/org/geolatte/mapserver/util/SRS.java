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

package org.geolatte.mapserver.util;

public class SRS {
    final String authority;
    final int code;

    static public SRS parse(String srsString) {
        if (srsString == null || srsString.isEmpty())
            throw new IllegalArgumentException("Require input of form '<authority>:<code>");
        String[] tokens = srsString.split(":");
        String authority;
        String codeStr = tokens[tokens.length - 1];
        codeStr = codeStr.trim();
        int code = Integer.valueOf(codeStr);
        authority = "EPSG";
        if (tokens.length == 2) {
            authority = tokens[0];
        }
        return new SRS(authority, code);
    }

    public SRS(String authority, int code) {
        this.authority = authority;
        this.code = code;
    }

    public String getAuthority() {
        return authority;
    }

    public int getCode() {
        return code;
    }

    public String toString() {
        return authority + ":" + code;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SRS srs = (SRS) o;

        if (code != srs.code) return false;
        if (authority != null ? !authority.equals(srs.authority) : srs.authority != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = authority != null ? authority.hashCode() : 0;
        result = 31 * result + code;
        return result;
    }
}
