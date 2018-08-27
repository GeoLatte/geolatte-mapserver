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

import org.geolatte.mapserver.ows.MapServerRequest;
import org.geolatte.mapserver.protocols.OsmGetLegendGraphicRequest;

import java.awt.*;

public class WmsGetLegendGraphicRequest extends WmsRequest {

    private final int DEFAULT_WIDTH = 100;
    private final int DEFAULT_HEIGHT = 100;

    @WmsParameter(required = false, param = WmsParam.VERSION)
    private String version;

    @WmsParameter(required = true, param = WmsParam.REQUEST)
    private String request;

    @WmsParameter(required = true, param = WmsParam.LAYER)
    private String layer;

    @WmsParameter(required = false, param = WmsParam.WIDTH)
    private Integer width;

    @WmsParameter(required = false, param = WmsParam.HEIGHT)
    private Integer height;

    @WmsParameter(required = true, param = WmsParam.FORMAT)
    private String format;

    @WmsParameter(required = false, param = WmsParam.EXCEPTIONS)
    private String exceptions = OgcMimeTypes.SERVICE_EXCEPTION_XML;

    WmsGetLegendGraphicRequest() {
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

    public String getExceptions() {
        return exceptions;
    }

    public Integer getHeight() {
        return height != null ? height : DEFAULT_HEIGHT;
    }

    public Integer getWidth() {
        return width != null ? width : DEFAULT_WIDTH;
    }

    public String getLayer() {
        return layer;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (WmsParam p : WmsParam.values()) {
            if (builder.length() == 0) {
                builder.append("GetLegendGraphic[");
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
        return new OsmGetLegendGraphicRequest(
                this.layer,
                this.getDimension(),
                format
        );
    }




}
