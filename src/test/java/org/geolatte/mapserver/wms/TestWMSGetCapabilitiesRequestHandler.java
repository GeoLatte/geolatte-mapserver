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

import org.geolatte.mapserver.config.Configuration;
import org.geolatte.mapserver.config.ConfigurationException;
import org.geolatte.mapserver.tms.TileMapRegistry;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: Jul 22, 2010
 */
public class TestWMSGetCapabilitiesRequestHandler {

    static WMSGetCapabilitiesResponse response;
    static WMSRequestHandler handler;

    @BeforeClass
    public static void setUp() throws ConfigurationException {
        Configuration config = Configuration.load("test-config.xml");
        try {
            TileMapRegistry registry = TileMapRegistry.configure(config);
            response = WMSGetCapabilitiesResponse.build(config, registry);
            handler = new WMSGetCapabilitiesRequestHandler(response);
        } catch (IllegalStateException e) {
            //swallow this exception, because it is expected
        }

    }

    @Test
    public void test_handle_valid_request() throws WMSServiceException, IOException {
        WMSRequest request = new WMSGetCapabilitiesRequest();
        request.setRequestURL("http://localhost:8090/wms");
        request.set(WMSParam.REQUEST, "GetCapabilities");
        request.set(WMSParam.SERVICE, "WMS");
        OutputStream mock = mock(OutputStream.class);
        handler.executeAndWriteTo(request, mock);
    }

    @Test
    public void test_invalid_request_throws_service_exception() throws InvalidWMSRequestException {
        WMSRequest request = new WMSGetCapabilitiesRequest();
        request.setRequestURL("http://localhost:8090/wms");
        request.set(WMSParam.REQUEST, "GetCapabilities");
        request.set(WMSParam.SERVICE, "WFS");
        OutputStream mock = mock(OutputStream.class);
        try {
            handler.executeAndWriteTo(request, mock);
            fail();
        } catch (WMSServiceException e) {
        } // OK

    }

}
