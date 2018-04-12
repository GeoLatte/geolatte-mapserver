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

package org.geolatte.mapserver.wms;

import org.apache.log4j.Logger;
import org.geolatte.mapserver.config.Configuration;
import org.geolatte.mapserver.config.ConfigurationException;
import org.geolatte.mapserver.tms.TileMapRegistry;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Karel Maesen, Geovise BVBA
 * creation-date: Jul 17, 2010
 */
public class WMSService {

    final static Logger LOGGER = Logger.getLogger(WMSService.class);

    private final TileMapRegistry tileMapRegistry;
    private final Configuration configuration;
    private final WMSGetCapabilitiesResponse capabilitiesResponse;

    private final Map<Class<? extends WMSRequest>, WMSRequestHandler> handlers = new HashMap<Class<? extends WMSRequest>, WMSRequestHandler>();


    public WMSService() throws ConfigurationException {
        configuration = Configuration.load();
        tileMapRegistry = TileMapRegistry.configure(configuration);
        capabilitiesResponse = WMSGetCapabilitiesResponse.build(configuration, tileMapRegistry);

        handlers.put(WMSGetMapRequest.class, new WMSGetMapRequestHandler(tileMapRegistry));
        handlers.put(WMSGetCapabilitiesRequest.class, new WMSGetCapabilitiesRequestHandler(capabilitiesResponse));
    }

    public void handle(WMSRequest request, OutputStream outputStream) throws WMSServiceException {
        WMSRequestHandler handler = handlers.get(request.getClass());
        if (handler == null) throw new WMSServiceException("Request not handled: " + request.toString());
        handler.executeAndWriteTo(request, outputStream);
    }
}
