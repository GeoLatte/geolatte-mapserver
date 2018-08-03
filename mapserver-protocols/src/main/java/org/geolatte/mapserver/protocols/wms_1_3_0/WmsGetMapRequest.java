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

import org.geolatte.geom.crs.CrsId;
import org.geolatte.mapserver.ows.MapServerRequest;
import org.geolatte.mapserver.protocols.OsmGetMapRequest;

import java.awt.*;

public class WmsGetMapRequest extends WmsRequest {

    @WmsParameter(required = false, param = WmsParam.VERSION)
    private String version;
    @WmsParameter(required = true, param = WmsParam.REQUEST)
    private String request;
    @WmsParameter(required = true, param = WmsParam.LAYERS)
    private String[] layers;
    @WmsParameter(required = false, param = WmsParam.STYLES)
    // making this optional is not in conformance with WMS spec!
    private String[] styles;
    @WmsParameter(required = true, param = WmsParam.CRS)
    private String srs;
    @WmsParameter(required = true, param = WmsParam.BBOX)
    private WmsBbox bbox;
    @WmsParameter(required = true, param = WmsParam.WIDTH)
    private Integer width;
    @WmsParameter(required = true, param = WmsParam.HEIGHT)
    private Integer height;
    @WmsParameter(required = true, param = WmsParam.FORMAT)
    private String format;
    @WmsParameter(required = false, param = WmsParam.TRANSPARENT)
    private String transparent;
    @WmsParameter(required = false, param = WmsParam.EXCEPTIONS)
    private String exceptions = OgcMimeTypes.SERVICE_EXCEPTION_XML;
    @WmsParameter(required = false, param = WmsParam.BGCOLOR)
    private String bgcolor;

    WmsGetMapRequest() {
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

    public String getSrs() {
        return srs;
    }


    public WmsBbox getBbox() {
        return bbox;
    }

    public Color getBgcolor() {
        if(bgcolor == null) {
            return Color.WHITE; //TODO -- configurable background color?
        }
        return Color.decode(bgcolor);
    }


    public String[] getLayers() {
        return layers;
    }

    public String[] getStyles() {
        return styles;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (WmsParam p : WmsParam.values()) {
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
            throw new UnsupportedOperationException();
//            return WMSCapabilities.getSupportedFormat("WMS", "GetMap")[0];
        } else {
            return format;
        }
    }

    @Override
    MapServerRequest toMapServerRequest() {
        return new OsmGetMapRequest(
                this.bbox.toEnvelope(this.srs),
                this.layers[0], // todo -- needs improvement -- support for more than one layer!!
                CrsId.parse(this.srs),
                this.getDimension(),
                "true".equalsIgnoreCase(this.transparent),
                this.getBgcolor(),
                this.styles == null  || this.styles.length == 0 ? "" : this.styles[0],
                format
        );
    }




}
