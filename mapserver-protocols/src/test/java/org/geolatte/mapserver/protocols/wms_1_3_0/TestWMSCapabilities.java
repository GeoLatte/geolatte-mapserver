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

import net.opengis.wms.v_1_3_0.Request;
import net.opengis.wms.v_1_3_0.Service;
import net.opengis.wms.v_1_3_0.WMSCapabilities;
import org.geolatte.mapserver.image.ImageFormat;
import org.geolatte.mapserver.ServiceMetadata;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * <p/>
 * Date: Nov 27, 2009
 */
public class TestWMSCapabilities {


    private final static Logger logger = LoggerFactory.getLogger(TestWMSCapabilities.class);
    private WmsJaxb jaxb = WmsJaxb.instance();
    private ServiceMetadata serviceMetadata;


    @Before
    public void setUp() {
        serviceMetadata = new ServiceMetadata(
                new ServiceMetadata.ServiceIdentification(
                        "urn:ogc:service:wms_1_3_0", "1.3.0", "Test WMS", "This is an abstract", asList("keyword1", "keyword2")),
                new ServiceMetadata.ServiceProvider("test")
                , new ServiceMetadata.OperationsMetadata(
                asList(
                        new ServiceMetadata.GetMapOperation("http://maps.example.com/wms/map?", asList(ImageFormat.PNG, ImageFormat.JPEG)),
                        new ServiceMetadata.GetCapabilitiesOperation("http://maps.example.com/cap?")
                )
        ),
            "1.3.0",
            "http://maps.example.com/wms"
    );
    }


    @Test
    @Ignore // ignore because this is not really a test
    public void testMarshalling() throws IOException, JAXBException {
        WMSCapabilities wmsCapabilities = jaxb.createWMSCapabilities(serviceMetadata);
        File testFile = File.createTempFile("capabilities", ".xml");
        Marshaller m = jaxb.createMarshaller();
        try (FileWriter writer = new FileWriter(testFile)) {
            m.marshal(wmsCapabilities, writer);
        }
        logger.info(format("Writing WMSCapabilities to %s", testFile.getAbsolutePath()));
    }


    @Test
    public void testServiceIdentification() throws IOException, JAXBException {
        WMSCapabilities wmsCapabilities = jaxb.createWMSCapabilities(serviceMetadata);
        Service service = wmsCapabilities.getService();
        assertEquals("Test WMS", service.getTitle());
        assertEquals("WMS", service.getName());
        assertThat(
                service.getKeywordList().getKeyword().stream().map(kw -> kw.getValue()).collect(toList()),
                hasItems("keyword1", "keyword2")
        );
        assertThat(service.getOnlineResource().getHref(), is("http://maps.example.com/wms"));
    }

    @Test
    public void testOperations() throws IOException, JAXBException {
        WMSCapabilities wmsCapabilities = jaxb.createWMSCapabilities(serviceMetadata);
        Request request = wmsCapabilities.getCapability().getRequest();
        String urlGetCap = request.getGetCapabilities().getDCPType().get(0).getHTTP().getGet().getOnlineResource().getHref();
        assertThat(urlGetCap, is("http://maps.example.com/cap?"));
        String urlGetMap = request.getGetMap().getDCPType().get(0).getHTTP().getGet().getOnlineResource().getHref();
        assertThat(urlGetMap, is("http://maps.example.com/wms/map?"));
        assertThat(request.getGetMap().getFormat(), hasItems("image/jpeg", "image/png"));
        assertThat(urlGetMap, is("http://maps.example.com/wms/map?"));
    }


}
