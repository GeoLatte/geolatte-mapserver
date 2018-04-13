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

package org.geolatte.mapserver.protocols.wms;

import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CrsId;

import java.awt.*;

public class WMSGetMapRequest extends WMSRequest {

    @WMSParameter(required = false, param = WMSParam.VERSION)
    private String version;
    @WMSParameter(required = true, param = WMSParam.REQUEST)
    private String request;
    @WMSParameter(required = true, param = WMSParam.LAYERS)
    private String[] layers;
    @WMSParameter(required = false, param = WMSParam.STYLES)
    // making this optional is not in conformance with WMS spec!
    private String[] styles;
    @WMSParameter(required = true, param = WMSParam.SRS)
    private CrsId srs;
    @WMSParameter(required = true, param = WMSParam.BBOX)
    private Envelope bbox;
    @WMSParameter(required = true, param = WMSParam.WIDTH)
    private Integer width;
    @WMSParameter(required = true, param = WMSParam.HEIGHT)
    private Integer height;
    @WMSParameter(required = true, param = WMSParam.FORMAT)
    private String format;
    @WMSParameter(required = false, param = WMSParam.TRANSPARENT)
    private String transparent;
    @WMSParameter(required = false, param = WMSParam.EXCEPTIONS)
    private String exceptions = OGCMIMETypes.SERVICE_EXCEPTION_XML;
    @WMSParameter(required = false, param = WMSParam.BGCOLOR)
    private String bgcolor;

    WMSGetMapRequest() {
    }

    public Dimension getDimension() {
        return new Dimension(getWidth(), getHeight());
    }

    public String getRequest() {
        return request;
    }

    public String getFormat() {
        return format;
    }

    public String getVersion() {
        return version;
    }

    public String getTransparent() {
        return transparent;
    }

    public String getExceptions() {
        return exceptions;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWidth() {
        return width;
    }

    public CrsId getSrs() {
        return srs;
    }

    public Envelope getBbox() {
        return new Envelope(bbox.getMinX(), bbox.getMinY(), bbox.getMaxX(), bbox.getMaxY(), this.getSrs());
    }

    public String getBgcolor() {
        return bgcolor;
    }


    public String[] getLayers() {
        return layers;
    }

    public String[] getStyles() {
        return styles;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (WMSParam p : WMSParam.values()) {
            if (builder.length() == 0) {
                builder.append("GetMap[");
            } else {
                builder.append(", ");
            }
            builder.append(p.toString())
                    .append("=")
                    .append(get(p));
        }
        builder.append("]");
        return builder.toString();

    }

    @Override
    public String getResponseContentType() {
        if (this.format == null) {
            return WMSCapabilities.getSupportedFormat("WMS", "GetMap")[0];
        } else {
            return format;
        }
    }
}
