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

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Tests whether all OGC-Specific MIME Tyes are defined.
 *
 * @author Karel Maesen, Geovise BVBA
 * creation-date: Jul 14, 2010
 */
public class TestOgcMimeTypes {

    @Test
    public void test_available_mime_types() {
        assertEquals("application/vnd.ogc.wms_xml", OgcMimeTypes.CAPABILITIES);
        assertEquals("application/vnd.ogc.gml", OgcMimeTypes.GML);
        assertEquals("application/vnd.ogc.se_xml", OgcMimeTypes.SERVICE_EXCEPTION_XML);
        assertEquals("application/vnd.ogc.se_inimage", OgcMimeTypes.SERVICE_EXCEPTION_INIMAGE);
        assertEquals("application/vnd.ogc.se_blank", OgcMimeTypes.SERVICE_EXCEPTION_BLANK);

    }
}
