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

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestWmsGetMapRequest {

    WmsGetMapRequest getMapRequest;
    Map<String, String> baseRequestParameters;

    @Before
    public void setUp() {
        getMapRequest = new WmsGetMapRequest();
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
    public void test_setting_params() throws InvalidWmsRequestException {
        getMapRequest.set(WmsParam.VERSION, "1.0.0");
        assertEquals("1.0.0", getMapRequest.get(WmsParam.VERSION));
        getMapRequest.set(WmsParam.VERSION, "1.1.1");
        assertEquals("1.1.1", getMapRequest.get(WmsParam.VERSION));
    }


    @Test
    public void test_adapt() throws InvalidWmsRequestException {
        baseRequestParameters.put("OPTIONAL", "TEST");
        HttpServletRequest request = makeRequest(baseRequestParameters);
        WmsGetMapRequest wmsGetMap = (WmsGetMapRequest) WmsRequest.adapt(request);
        assertEquals(new WmsBbox(-130, 24, -66, 50), wmsGetMap.getBbox());
        assertEquals("image/png", wmsGetMap.getFormat());
        assertEquals(Integer.valueOf(550), wmsGetMap.getWidth());
        assertArrayEquals(new String[]{"basic"}, wmsGetMap.getLayers());
        assertEquals("1.1.1", wmsGetMap.getVersion());
        assertEquals("EPSG:4326", wmsGetMap.getSrs());
        assertEquals(Integer.valueOf(250), wmsGetMap.getHeight());


    }


    @Test
    public void test_version_under_alternative_name() throws InvalidWmsRequestException {
        baseRequestParameters.remove("VERSION");
        baseRequestParameters.put("WMTVER", "1.1.1");
        HttpServletRequest request = makeRequest(baseRequestParameters);

        WmsGetMapRequest wmsGetMap = (WmsGetMapRequest) WmsRequest.adapt(request);
        assertEquals("EPSG:4326", wmsGetMap.getSrs());
        assertEquals("1.1.1", wmsGetMap.getVersion());

    }

    @Test
    public void test_multiple_layers_and_styles_name() throws InvalidWmsRequestException {
        baseRequestParameters.put("LAYERS", "basic,osm");
        baseRequestParameters.put("STYLES", "basic_style,osm_style");
        HttpServletRequest request = makeRequest(baseRequestParameters);
        WmsGetMapRequest wmsGetMap = (WmsGetMapRequest) WmsRequest.adapt(request);

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
            WmsGetMapRequest wmsGetMap = (WmsGetMapRequest) WmsRequest.adapt(request);
            fail();
        } catch (InvalidWmsRequestException e1) {
            //OK
        }

    }

    @Test
    public void test_response_content_equals_request_format() throws InvalidWmsRequestException {
        HttpServletRequest request = makeRequest(baseRequestParameters);
        WmsRequest wmsReq = WmsRequest.adapt(request);
        assertEquals("image/png", wmsReq.getResponseContentType());

        baseRequestParameters.put("FORMAT", "image/jpeg");
        HttpServletRequest requestJpeg = makeRequest(baseRequestParameters);
        wmsReq = WmsRequest.adapt(requestJpeg);
        assertEquals("image/jpeg", wmsReq.getResponseContentType());

    }

    private HttpServletRequest makeRequest(Map<String, String> parameters) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String parameterNames = extractParameterNameTokens(parameters);
        when(request.getParameterNames()).thenReturn((Enumeration)new StringTokenizer(parameterNames, ","), (Enumeration)new StringTokenizer(parameterNames, ","));
        for (String param : parameters.keySet()) {
            when(request.getParameter(param)).thenReturn(parameters.get(param), parameters.get(param));
        }
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8090/wms_1_3_0"));
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
