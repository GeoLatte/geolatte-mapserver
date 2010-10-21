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

package org.geolatte.mapserver.servlet;

import org.apache.log4j.Logger;
import org.geolatte.mapserver.config.ConfigurationException;
import org.geolatte.mapserver.wms.OGCMIMETypes;
import org.geolatte.mapserver.wms.WMSRequest;
import org.geolatte.mapserver.wms.WMSService;
import org.geolatte.mapserver.wms.WMSServiceException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for WMS requests.
 *
 * @author Karel Maesen, Geovise BVBA
 */
public class WMSServlet extends HttpServlet {

    private final static Logger LOGGER = Logger.getLogger(WMSServlet.class);
    private WMSService wmsService;

    public void init() {
        try {
            wmsService = new WMSService();
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Request: " + request.toString());
        try {
            WMSRequest wmsRequest = WMSRequest.adapt(request);
            wmsService.handle(wmsRequest, response.getOutputStream());
        } catch (WMSServiceException se) {
            //Note that this ignores the EXCEPTIONS Request Parameter!!
            response.setHeader("Content-Type", OGCMIMETypes.SERVICE_EXCEPTION_XML);
            se.writeToOutputStream(response.getOutputStream());
        } finally {
            response.getOutputStream().close();
        }
    }
}
