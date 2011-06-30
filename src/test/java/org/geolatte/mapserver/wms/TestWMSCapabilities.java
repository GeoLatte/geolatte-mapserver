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

import static org.junit.Assert.assertArrayEquals;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: Jul 14, 2010
 */
public class TestWMSCapabilities {

    @Test
    public void test_WMS_service_is_supported() {
        assertArrayEquals(new String[]{"WMS"}, WMSCapabilities.getSupportedServices());
    }

    @Test
    public void test_only_version_1_1_1_is_supported() {
        assertArrayEquals(new String[]{"1.1.1"}, WMSCapabilities.getSupportedVersions("WMS"));
    }


    @Test
    public void test_only_capabilities_and_getmap_are_supportd() {
        assertArrayEquals(new String[]{"GetCapabilities", "GetMap"}, WMSCapabilities.getSupportedRequests("WMS"));
    }

    @Test
    public void test_png_and_jpeg_output_supported_in_wms_getmap() {
        assertArrayEquals(new String[]{"image/png", "image/jpeg"}, WMSCapabilities.getSupportedFormat("WMS", "GetMap"));
    }

    @Test
    public void test_wms_xml_supported_in_wms_getcapabilities() {
        assertArrayEquals(new String[]{"application/vnd.ogc.wms_xml"}, WMSCapabilities.getSupportedFormat("WMS", "GetCapabilities"));
    }

    @Test
    public void test_wms_xml_service_exception_supported() {
        assertArrayEquals(new String[]{"application/vnd.ogc.se_xml"}, WMSCapabilities.getSupportedExceptionFormat("WMS"));
    }


}
