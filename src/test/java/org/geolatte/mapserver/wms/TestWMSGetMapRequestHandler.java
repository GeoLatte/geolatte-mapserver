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

import java.io.OutputStream;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: Jul 22, 2010
 */
public class TestWMSGetMapRequestHandler {

    static WMSRequestHandler handler;

    @BeforeClass
    public static void setUp() throws ConfigurationException {
        Configuration config = Configuration.load("test-config.xml");
        try {
            TileMapRegistry registry = TileMapRegistry.configure(config);
            handler = new WMSGetMapRequestHandler(registry);
        } catch (IllegalStateException e) {
            //swallow this exception, because it is expected
        }
    }

    private WMSRequest makeTestRequest() throws InvalidWMSRequestException {
        WMSRequest request = new WMSGetMapRequest();
        request.set(WMSParam.REQUEST, "GetMap");
        request.set(WMSParam.BBOX, "0,0,90,90");
        request.set(WMSParam.FORMAT, "image/png");
        request.set(WMSParam.WIDTH, "512");
        request.set(WMSParam.HEIGHT, "256");
        request.set(WMSParam.LAYERS, "basic");
        request.set(WMSParam.SRS, "4326");
        request.set(WMSParam.SRS, "4326");
        request.set(WMSParam.VERSION, "1.1.1");
        request.set(WMSParam.STYLES, "");
        return request;
    }

    @Test
    public void test_normal_execution() throws WMSServiceException {
        WMSRequest request = makeTestRequest();
        request.verify();
        OutputStream mock = mock(OutputStream.class);
        handler.executeAndWriteTo(request, mock);
    }

    @Test
    public void test_invalid_format() throws InvalidWMSRequestException {
        WMSRequest request = makeTestRequest();
        request.set(WMSParam.FORMAT, "image/bla");
        request.verify();
        OutputStream mock = mock(OutputStream.class);
        try {
            handler.executeAndWriteTo(request, mock);
            fail();
        } catch (WMSServiceException se) {
            assertTrue(se.getCodes().contains("InvalidFormat"));
        }
    }

    @Test
    public void test_invalid_srs() throws InvalidWMSRequestException {
        WMSRequest request = makeTestRequest();
        request.set(WMSParam.SRS, "37300");
        request.verify();
        OutputStream mock = mock(OutputStream.class);
        try {
            handler.executeAndWriteTo(request, mock);
            fail();
        } catch (WMSServiceException se) {
            assertTrue(se.getCodes().contains("InvalidSRS"));
        }
    }

    @Test
    public void test_invalid_layer() throws InvalidWMSRequestException {
        WMSRequest request = makeTestRequest();
        request.set(WMSParam.LAYERS, "test");
        request.verify();
        OutputStream mock = mock(OutputStream.class);
        try {
            handler.executeAndWriteTo(request, mock);
            fail();
        } catch (WMSServiceException se) {
            assertTrue(se.getCodes().contains("LayerNotDefined"));
        }
    }

    @Test
    public void test_too_many_layer() throws InvalidWMSRequestException {
        WMSRequest request = makeTestRequest();
        request.set(WMSParam.LAYERS, "test,test1,test3");
        request.verify();
        OutputStream mock = mock(OutputStream.class);
        try {
            handler.executeAndWriteTo(request, mock);
            fail();
        } catch (WMSServiceException se) {
            assertTrue(se.getCodes().contains("LayerNotDefined"));
        }
    }


}
