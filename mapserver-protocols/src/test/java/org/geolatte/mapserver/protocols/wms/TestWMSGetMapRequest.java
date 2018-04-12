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

import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CrsId;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestWMSGetMapRequest {

    WMSGetMapRequest getMapRequest;
    Map<String, String> baseRequestParameters;

    @Before
    public void setUp() {
        getMapRequest = new WMSGetMapRequest();
        baseRequestParameters = new HashMap<String, String>();
        baseRequestParameters.put("REQUEST", "GetMap");
        baseRequestParameters.put("BBOX", "-130,24,-66,50");
        baseRequestParameters.put("FORMAT", "image/png");
        baseRequestParameters.put("LAYERS", "basic");
        baseRequestParameters.put("WIDTH", "550");
        baseRequestParameters.put("HEIGHT", "250");
        baseRequestParameters.put("SRS", "EPSG:4326");
        baseRequestParameters.put("VERSION", "1.1.1");
        baseRequestParameters.put("STYLES", "");
    }


    @Test
    public void test_setting_params() throws InvalidWMSRequestException {
        getMapRequest.set(WMSParam.VERSION, "1.0.0");
        assertEquals("1.0.0", getMapRequest.get(WMSParam.VERSION));
        getMapRequest.set(WMSParam.VERSION, "1.1.1");
        assertEquals("1.1.1", getMapRequest.get(WMSParam.VERSION));
    }


    @Test
    public void test_adapt() throws InvalidWMSRequestException {
        baseRequestParameters.put("OPTIONAL", "TEST");
        HttpServletRequest request = makeRequest(baseRequestParameters);
        WMSGetMapRequest wmsGetMap = (WMSGetMapRequest) WMSRequest.adapt(request);

        assertEquals(new Envelope(-130, 24, -66, 50, CrsId.parse("EPSG:4326")), wmsGetMap.getBbox());
        assertEquals("image/png", wmsGetMap.getFormat());
        assertEquals(Integer.valueOf(550), wmsGetMap.getWidth());
        assertArrayEquals(new String[]{"basic"}, wmsGetMap.getLayers());
        assertEquals("1.1.1", wmsGetMap.getVersion());
        assertEquals(CrsId.parse("EPSG:4326"), wmsGetMap.getSrs());
        assertEquals(Integer.valueOf(250), wmsGetMap.getHeight());


    }


    @Test
    public void test_version_under_alternative_name() throws InvalidWMSRequestException {
        baseRequestParameters.remove("VERSION");
        baseRequestParameters.put("WMTVER", "1.1.1");
        HttpServletRequest request = makeRequest(baseRequestParameters);

        WMSGetMapRequest wmsGetMap = (WMSGetMapRequest) WMSRequest.adapt(request);
        assertEquals(CrsId.parse("EPSG:4326"), wmsGetMap.getSrs());
        assertEquals("1.1.1", wmsGetMap.getVersion());

    }

    @Test
    public void test_multiple_layers_and_styles_name() throws InvalidWMSRequestException {
        baseRequestParameters.put("LAYERS", "basic,osm");
        baseRequestParameters.put("STYLES", "basic_style,osm_style");
        HttpServletRequest request = makeRequest(baseRequestParameters);
        WMSGetMapRequest wmsGetMap = (WMSGetMapRequest) WMSRequest.adapt(request);

        assertEquals("basic", wmsGetMap.getLayers()[0]);
        assertEquals("osm", wmsGetMap.getLayers()[1]);
        assertEquals("basic_style", wmsGetMap.getStyles()[0]);
        assertEquals("osm_style", wmsGetMap.getStyles()[1]);

    }

    @Test
    public void test_required_param_missing_throws_InvalidWMSRequestException() {
        baseRequestParameters.remove("LAYERS");
        HttpServletRequest request = makeRequest(baseRequestParameters);

        try {
            WMSGetMapRequest wmsGetMap = (WMSGetMapRequest) WMSRequest.adapt(request);
            fail();
        } catch (InvalidWMSRequestException e1) {
            //OK
        }

    }

    @Test
    public void test_response_content_equals_request_format() throws InvalidWMSRequestException {
        HttpServletRequest request = makeRequest(baseRequestParameters);
        WMSRequest wmsReq = WMSRequest.adapt(request);
        assertEquals("image/png", wmsReq.getResponseContentType());

        baseRequestParameters.put("FORMAT", "image/jpeg");
        HttpServletRequest requestJpeg = makeRequest(baseRequestParameters);
        wmsReq = WMSRequest.adapt(requestJpeg);
        assertEquals("image/jpeg", wmsReq.getResponseContentType());

    }


    @Test
    public void test_invalid_bounding_box() {
        baseRequestParameters.put("BBOX", "-66,50,-130,24");
        HttpServletRequest request = makeRequest(baseRequestParameters);
        WMSGetMapRequest wmsGetMap = null;
        try {
            wmsGetMap = (WMSGetMapRequest) WMSRequest.adapt(request);
            fail();
        } catch (InvalidWMSRequestException e1) {
            //OK
        }

    }

    @Test
    public void test_invalid_SRS() {
        baseRequestParameters.put("SRS", "BLALBA");
        HttpServletRequest request = makeRequest(baseRequestParameters);
        WMSGetMapRequest wmsGetMap = null;
        try {
            wmsGetMap = (WMSGetMapRequest) WMSRequest.adapt(request);
            fail();
        } catch (InvalidWMSRequestException e1) {
            //OK
        }

    }

    private HttpServletRequest makeRequest(Map<String, String> parameters) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String parameterNames = extractParameterNameTokens(parameters);
        when(request.getParameterNames()).thenReturn(new StringTokenizer(parameterNames, ","), new StringTokenizer(parameterNames, ","));
        for (String param : parameters.keySet()) {
            when(request.getParameter(param)).thenReturn(parameters.get(param), parameters.get(param));
        }
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8090/wms"));
        return request;
    }

    private String extractParameterNameTokens(Map<String, String> parameters) {
        StringBuilder stb = null;
        for (String parameterName : parameters.keySet()) {
            if (stb == null) {
                stb = new StringBuilder(parameterName);
            } else {
                stb.append(",").append(parameterName);
            }
        }
        return stb.toString();
    }


}
