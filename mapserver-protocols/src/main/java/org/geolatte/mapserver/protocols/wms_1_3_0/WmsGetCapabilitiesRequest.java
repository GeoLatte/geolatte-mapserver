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

import org.geolatte.mapserver.protocols.OsmGetCapabiltiesRequest;
import org.geolatte.mapserver.ows.MapServerRequest;

public class WmsGetCapabilitiesRequest extends WmsRequest {


    @WmsParameter(required = true, param = WmsParam.REQUEST)
    private String request;

    @WmsParameter(required = false, param = WmsParam.VERSION)
    private String version;

    @WmsParameter(required = true, param = WmsParam.SERVICE)
    private String service;

    @WmsParameter(required = false, param = WmsParam.UPDATESEQUENCE)
    private String updateSeq;

    public String getRequest() {
        return request;
    }

    public String getVersion() {
        return version;
    }

    public String getService() {
        return service;
    }

    //This ows parameter is currently ignored.
    public String getUpdateSequence() {
        return updateSeq;
    }

    @Override
    public String getResponseContentType() {
        return OgcMimeTypes.CAPABILITIES;
    }

    @Override
    MapServerRequest toMapServerRequest() {
        return new OsmGetCapabiltiesRequest(getService(), getVersion(), getResponseContentType());
    }


}
