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
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestWmsGetCapabilitiesRequest {

    @Test
    public void test_adapt_normal() throws InvalidWmsRequestException, URISyntaxException {

        HttpQueryParams qparams = mock(HttpQueryParams.class);
        HttpRequest request = mock(HttpRequest.class);

        Set<String> params = new HashSet<>(Arrays.asList("REQUEST", "VERSION", "SERVICE", "UPDATESEQUENCE"));

        when(qparams.allParams()).thenReturn(params);
        when(qparams.firstValue("REQUEST")).thenReturn(Optional.of("GetCapabilities"), Optional.of("GetCapabilities"));
        when(qparams.firstValue("VERSION")).thenReturn(Optional.of("1.3.0"));
        when(qparams.firstValue("SERVICE")).thenReturn(Optional.of("WMS"));
        when(qparams.firstValue("UPDATESEQUENCE")).thenReturn(Optional.of("0"));
        when(request.uri()).thenReturn( new URI("http://localhost:8090/wms_1_3_0"));

        when(request.parseQuery()).thenReturn(qparams);
        WmsGetCapabilitiesRequest wmsGetCapabilities = (WmsGetCapabilitiesRequest) WmsRequest.adapt(request);
        assertEquals("GetCapabilities", wmsGetCapabilities.getRequest());
        assertEquals("1.3.0", wmsGetCapabilities.getVersion());
        assertEquals("WMS", wmsGetCapabilities.getService());
        assertEquals("0", wmsGetCapabilities.getUpdateSequence());

    }

    @Test
    public void test_adapt_only_required_params() throws InvalidWmsRequestException, URISyntaxException {
        HttpQueryParams qparams = mock(HttpQueryParams.class);
        HttpRequest request = mock(HttpRequest.class);

        Set<String> params = new HashSet<>(Arrays.asList("REQUEST", "SERVICE", "VERSION"));

        when(qparams.allParams()).thenReturn(params);
        when(qparams.firstValue("REQUEST")).thenReturn(Optional.of("GetCapabilities"), Optional.of("GetCapabilities"));
        when(qparams.firstValue("VERSION")).thenReturn(Optional.of("1.3.0"));
        when(qparams.firstValue("SERVICE")).thenReturn(Optional.of("WMS"));
        when(request.parseQuery()).thenReturn(qparams);
        when(request.uri()).thenReturn( new URI("http://localhost:8090/wms_1_3_0"));

        WmsGetCapabilitiesRequest wmsGetCapabilities = (WmsGetCapabilitiesRequest) WmsRequest.adapt(request);
        assertEquals("GetCapabilities", wmsGetCapabilities.getRequest());
        assertEquals("WMS", wmsGetCapabilities.getService());

    }

    @Test(expected = IllegalArgumentException.class)
    public void test_adapt_missing_required_param() throws InvalidWmsRequestException, URISyntaxException {
        HttpQueryParams qparams = mock(HttpQueryParams.class);
        HttpRequest request = mock(HttpRequest.class);

        Set<String> params = new HashSet<>(Arrays.asList("VERSION", "SERVICE"));

        when(qparams.firstValue("REQUEST")).thenReturn(Optional.empty());
        when(qparams.firstValue("VERSION")).thenReturn(Optional.of("1.3.0"));
        when(qparams.firstValue("SERVICE")).thenReturn(Optional.of("WMS"));
        when(qparams.allParams()).thenReturn(params);
        when(request.parseQuery()).thenReturn(qparams);
        when(request.uri()).thenReturn( new URI("http://localhost:8090/wms_1_3_0"));

        WmsGetCapabilitiesRequest wmsGetCapabilities = (WmsGetCapabilitiesRequest) WmsRequest.adapt(request);


    }

}
