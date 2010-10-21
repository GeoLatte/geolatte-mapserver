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

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.StringTokenizer;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestWMSGetCapabilitiesRequest {

    @Test
    public void test_adapt_normal() throws InvalidWMSRequestException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        String params = "REQUEST,VERSION,SERVICE,UPDATESEQUENCE";

        when(request.getParameterNames()).thenReturn(new StringTokenizer(params, ","), new StringTokenizer(params, ","));
        when(request.getParameter("REQUEST")).thenReturn("GetCapabilities", "GetCapabilities");
        when(request.getParameter("VERSION")).thenReturn("1.1.1");
        when(request.getParameter("SERVICE")).thenReturn("WMS");
        when(request.getParameter("UPDATESEQUENCE")).thenReturn("0");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8090/wms"));


        WMSGetCapabilitiesRequest wmsGetCapabilities = (WMSGetCapabilitiesRequest) WMSRequest.adapt(request);
        assertEquals("GetCapabilities", wmsGetCapabilities.getRequest());
        assertEquals("1.1.1", wmsGetCapabilities.getVersion());
        assertEquals("WMS", wmsGetCapabilities.getService());
        assertEquals("0", wmsGetCapabilities.getUpdateSequence());

    }

    @Test
    public void test_adapt_only_required_params() throws InvalidWMSRequestException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        String params = "REQUEST,SERVICE";

        when(request.getParameterNames()).thenReturn(new StringTokenizer(params, ","), new StringTokenizer(params, ","));
        when(request.getParameter("REQUEST")).thenReturn("GetCapabilities", "GetCapabilities");
        when(request.getParameter("SERVICE")).thenReturn("WMS");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8090/wms"));

        WMSGetCapabilitiesRequest wmsGetCapabilities = (WMSGetCapabilitiesRequest) WMSRequest.adapt(request);
        assertEquals("GetCapabilities", wmsGetCapabilities.getRequest());
        assertEquals("WMS", wmsGetCapabilities.getService());


    }

    @Test
    public void test_adapt_missing_required_param() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        String params = "REQUEST";
        Enumeration e = new StringTokenizer(params, ",");

        when(request.getParameterNames()).thenReturn(new StringTokenizer(params, ","), new StringTokenizer(params, ","));
        when(request.getParameter("REQUEST")).thenReturn("GetCapabilities", "GetCapabilities");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8090/wms"));


        try {
            WMSGetCapabilitiesRequest wmsGetCapabilities = (WMSGetCapabilitiesRequest) WMSRequest.adapt(request);
            fail();
        } catch (InvalidWMSRequestException e1) {
            //OK
        }

    }

}
