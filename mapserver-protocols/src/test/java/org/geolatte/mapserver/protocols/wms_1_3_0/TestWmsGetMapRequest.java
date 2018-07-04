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

import org.geolatte.mapserver.http.HttpQueryParams;
import org.geolatte.mapserver.http.HttpRequest;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

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
        baseRequestParameters.put("CRS", "EPSG:4326");
        baseRequestParameters.put("VERSION", "1.1.1");
        baseRequestParameters.put("STYLES", "");
    }


    @Test
    public void test_setting_params() throws InvalidWmsRequestException {
        getMapRequest.set(WmsParam.VERSION, Optional.of("1.0.0"));
        assertEquals("1.0.0", getMapRequest.get(WmsParam.VERSION));
        getMapRequest.set(WmsParam.VERSION, Optional.of("1.1.1"));
        assertEquals("1.1.1", getMapRequest.get(WmsParam.VERSION));
    }


    @Test
    public void test_adapt() throws InvalidWmsRequestException {
        baseRequestParameters.put("OPTIONAL", "TEST");
        HttpRequest request = makeRequest(baseRequestParameters);
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
        HttpRequest request = makeRequest(baseRequestParameters);

        WmsGetMapRequest wmsGetMap = (WmsGetMapRequest) WmsRequest.adapt(request);
        assertEquals("EPSG:4326", wmsGetMap.getSrs());
        assertEquals("1.1.1", wmsGetMap.getVersion());

    }

    @Test
    public void test_multiple_layers_and_styles_name() throws InvalidWmsRequestException {
        baseRequestParameters.put("LAYERS", "basic,osm");
        baseRequestParameters.put("STYLES", "basic_style,osm_style");
        HttpRequest request = makeRequest(baseRequestParameters);
        WmsGetMapRequest wmsGetMap = (WmsGetMapRequest) WmsRequest.adapt(request);

        assertEquals("basic", wmsGetMap.getLayers()[0]);
        assertEquals("osm", wmsGetMap.getLayers()[1]);
        assertEquals("basic_style", wmsGetMap.getStyles()[0]);
        assertEquals("osm_style", wmsGetMap.getStyles()[1]);

    }

    @Test
    public void test_required_param_missing_throws_InvalidWMSRequestException() {
        baseRequestParameters.remove("LAYERS");
        HttpRequest request = makeRequest(baseRequestParameters);

        try {
            WmsGetMapRequest wmsGetMap = (WmsGetMapRequest) WmsRequest.adapt(request);
            fail();
        } catch (InvalidWmsRequestException e1) {
            //OK
        }

    }

    @Test
    public void test_response_content_equals_request_format() throws InvalidWmsRequestException {
        HttpRequest request = makeRequest(baseRequestParameters);
        WmsRequest wmsReq = WmsRequest.adapt(request);
        assertEquals("image/png", wmsReq.getResponseContentType());

        baseRequestParameters.put("FORMAT", "image/jpeg");
        HttpRequest requestJpeg = makeRequest(baseRequestParameters);
        wmsReq = WmsRequest.adapt(requestJpeg);
        assertEquals("image/jpeg", wmsReq.getResponseContentType());

    }

    private HttpRequest makeRequest(Map<String, String> parameters)  {
        HttpQueryParams qparams = mock(HttpQueryParams.class);
        HttpRequest request = mock(HttpRequest.class);

        when(request.parseQuery()).thenReturn(qparams);
        for (String param : parameters.keySet()) {
            when(qparams.firstValue(param)).thenReturn(Optional.ofNullable(parameters.get(param)), Optional.ofNullable(parameters.get(param)));
        }
        when(qparams.allParams()).thenReturn(parameters.keySet());

        try {
            when(request.uri()).thenReturn(new URI("http://localhost:8090/wms_1_3_0?REQUEST=GETMAP&VERSION=1.3.0&"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return request;
    }


}
