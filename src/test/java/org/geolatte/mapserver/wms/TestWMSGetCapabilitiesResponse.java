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

import net.opengis.wms.v_1_1_1.WMTMSCapabilities;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.geolatte.mapserver.config.Configuration;
import org.geolatte.mapserver.config.ConfigurationException;
import org.geolatte.mapserver.tms.TileMapRegistry;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static org.junit.Assert.*;

/**
 * <p/>
 * Date: Nov 27, 2009
 */
public class TestWMSGetCapabilitiesResponse {

    static JAXBContext context;
    static Unmarshaller unmarshaller;

    WMSGetCapabilitiesResponse WMSGetCapabilities;


    @BeforeClass
    public static void beforeClass() throws JAXBException, SAXException {
        context = JAXBContext.newInstance("net.opengis.wms.v_1_1_1");
        unmarshaller = context.createUnmarshaller();
    }

    @Before
    public void setUp() throws ConfigurationException {
        Configuration config = Configuration.load("test-config.xml");
        try {
            TileMapRegistry registry = TileMapRegistry.configure(config);
            WMSGetCapabilities = WMSGetCapabilitiesResponse.build(config, registry);
        } catch (IllegalStateException e) {
            //swallow this exception, because it is expected 
        }
    }

    @Test
    public void test_after_unmarshalling_we_have_content() throws Exception, IOException {
        File f = File.createTempFile("WMSGetCapabilities", "xml");
        try {
            OutputStream os = new FileOutputStream(f);
            WMSGetCapabilities.write(os, "http://www.test.com/wms");
            WMTMSCapabilities wmtmsCapabilities = (WMTMSCapabilities) unmarshaller.unmarshal(f);
            assertNotNull(wmtmsCapabilities);
        } catch (Exception e) {
            throw e;
        } finally {
            f.delete();
        }
    }

    @Test
    public void test_validity_response() throws Exception, IOException {
        File f = File.createTempFile("WMSGetCapabilities", "xml");
        try {
            OutputStream os = new FileOutputStream(f);
            WMSGetCapabilities.write(os, "http://www.geolatte.org/wms");

            SAXReader reader = new SAXReader();
            Document doc = reader.read(f);
            //check root object
            Node capabilitiesNd = doc.selectSingleNode("/WMT_MS_Capabilities");
            assertNotNull("Incorrect root node", capabilitiesNd);

            verifyServiceNode(capabilitiesNd);
            verifyCapabilityNode(capabilitiesNd);

        } catch (Exception e) {
            throw e;
        } finally {
//            f.delete();
        }

    }

    private void verifyCapabilityNode(Node capabilitiesNd) {

        Node capabilityNd = capabilitiesNd.selectSingleNode("Capability");
        assertNotNull("No Capability node", capabilityNd);
        verifyGetCapabilitiesRequestNd(capabilityNd);
        verifyGetMapRequestNode(capabilityNd);
        verifyExceptionNode(capabilityNd);
        verifyLayerNode(capabilityNd);

    }

    private void verifyLayerNode(Node capabilityNd) {
        Element rootLayerNd = (Element) capabilityNd.selectSingleNode("Layer");
        assertNotNull("Root Layer node not available", rootLayerNd);
        Element titleNd = (Element) rootLayerNd.selectSingleNode("Title");
        assertEquals("test title", titleNd.getText());
        verifyLayer(rootLayerNd, "basic");
        verifyLayer(rootLayerNd, "osm");
        verifyLayer(rootLayerNd, "tms-vlaanderen");
        Element layerNode = (Element) rootLayerNd.selectSingleNode("Layer[Name='error']");
        assertNull("Erroneous Tilemaps should not be advertised", layerNode);
    }

    //TOD) -- refactor to separate methods
    private void verifyLayer(Element rootLayerNd, String tileMap) {
        Element layerNode = (Element) rootLayerNd.selectSingleNode("Layer[Name='" + tileMap + "']");
        assertNotNull("No node for the basic layer", layerNode);
        String name = layerNode.selectSingleNode("Name").getText();
        assertEquals("incorrect name", tileMap, name);
        assertEquals("incorrect name", tileMap, layerNode.selectSingleNode("Title").getText());
        if ("basic".equals(name)) {
            assertEquals("incorrect SRS", "EPSG:4326", layerNode.selectSingleNode("SRS").getText());
            assertEquals("incorrect LatLonBB0X", "-180", layerNode.selectSingleNode("LatLonBoundingBox/@minx").getText());
            assertEquals("incorrect LatLonBB0X", "180", layerNode.selectSingleNode("LatLonBoundingBox/@maxx").getText());
            assertEquals("incorrect LatLonBB0X", "-90", layerNode.selectSingleNode("LatLonBoundingBox/@miny").getText());
            assertEquals("incorrect LatLonBB0X", "90", layerNode.selectSingleNode("LatLonBoundingBox/@maxy").getText());
            assertEquals("incorrect BB0X", "EPSG:4326", layerNode.selectSingleNode("BoundingBox/@SRS").getText());
            assertEquals("incorrect BB0X", "-180", layerNode.selectSingleNode("BoundingBox/@minx").getText());
            assertEquals("incorrect LatLonBB0X", "180", layerNode.selectSingleNode("BoundingBox/@maxx").getText());
            assertEquals("incorrect LatLonBB0X", "-90", layerNode.selectSingleNode("BoundingBox/@miny").getText());
            assertEquals("incorrect LatLonBB0X", "90", layerNode.selectSingleNode("BoundingBox/@maxy").getText());
        } else if ("osm".equals(name)) {
            assertEquals("incorrect SRS", "EPSG:900913", layerNode.selectSingleNode("SRS").getText());
            assertEquals("incorrect LatLonBB0X", "-180", layerNode.selectSingleNode("LatLonBoundingBox/@minx").getText());
            assertEquals("incorrect LatLonBB0X", "180", layerNode.selectSingleNode("LatLonBoundingBox/@maxx").getText());
            assertEquals("incorrect LatLonBB0X", "-85.051", layerNode.selectSingleNode("LatLonBoundingBox/@miny").getText());
            assertEquals("incorrect LatLonBB0X", "85.051", layerNode.selectSingleNode("LatLonBoundingBox/@maxy").getText());
            assertEquals("incorrect BB0X", "EPSG:900913", layerNode.selectSingleNode("BoundingBox/@SRS").getText());
            assertEquals("incorrect BB0X", "-20037508.34", layerNode.selectSingleNode("BoundingBox/@minx").getText());
            assertEquals("incorrect LatLonBB0X", "20037508.34", layerNode.selectSingleNode("BoundingBox/@maxx").getText());
            assertEquals("incorrect LatLonBB0X", "-20037508.34", layerNode.selectSingleNode("BoundingBox/@miny").getText());
            assertEquals("incorrect LatLonBB0X", "20037508.34", layerNode.selectSingleNode("BoundingBox/@maxy").getText());
        } else if ("tms-vlaanderen".equals(name)) {
            List list = layerNode.selectNodes("SRS");
            assertEquals("Expected two SRS elements", 3, list.size());
            assertEquals("incorrect 1e SRS", "EPSG:31370",((Node)list.get(0)).getText());
            assertEquals("incorrect 2e SRS", "EPSG:25831",((Node)list.get(1)).getText());
            assertEquals("incorrect 2e SRS", "EPSG:900913",((Node)list.get(2)).getText());
        }

    }

    private void verifyExceptionNode(Node capabilityNd) {
        Element exceptionNd = (Element) capabilityNd.selectSingleNode("Exception");
        assertNotNull("No Exception node", exceptionNd);
        Element excFormat = (Element) exceptionNd.selectSingleNode("Format");
        assertNotNull("No Exception/Format node", excFormat);
        assertEquals("Wrong format for exception specified.", "application/vnd.ogc.se_xml", excFormat.getText());
    }

    private void verifyGetMapRequestNode(Node capabilityNd) {
        Element getMapRequestNd = (Element) capabilityNd.selectSingleNode("Request/GetMap");
        assertNotNull("No Request/GetMap node", getMapRequestNd);

        Element gmFormat = (Element) getMapRequestNd.selectSingleNode("Format");
        assertNotNull("No Request/GetMap/Format node", gmFormat);
        assertEquals("Wrong format for WMSGetCapabilities document specified.", "image/png", gmFormat.getText());

        Element gmHTTPGET = (Element) getMapRequestNd.selectSingleNode("DCPType/HTTP/Get/OnlineResource");
        verifyOnlineResourceElement(gmHTTPGET, "http://www.geolatte.org/wms");

        Element gmHTTPPOST = (Element) getMapRequestNd.selectSingleNode("DCPType/HTTP/Post");
        assertNull(gmHTTPPOST);
    }

    private void verifyGetCapabilitiesRequestNd(Node capabilityNd) {
        Element getCapabilitiesRequestNd = (Element) capabilityNd.selectSingleNode("Request/GetCapabilities");
        assertNotNull("No Request/GetCapability node", getCapabilitiesRequestNd);

        Element gcFormat = (Element) getCapabilitiesRequestNd.selectSingleNode("Format");
        assertNotNull("No Request/GetCapabality/Format node", gcFormat);
        assertEquals("Wrong format for WMSGetCapabilities document specified.", "application/vnd.ogc.wms_xml", gcFormat.getText());

        Element gcHTTPGET = (Element) getCapabilitiesRequestNd.selectSingleNode("DCPType/HTTP/Get/OnlineResource");
        verifyOnlineResourceElement(gcHTTPGET, "http://www.geolatte.org/wms");

        Element gcHTTPPost = (Element) getCapabilitiesRequestNd.selectSingleNode("DCPType/HTTP/Post/OnlineResource");
        verifyOnlineResourceElement(gcHTTPPost, "http://www.geolatte.org/wms");
    }


    private void verifyServiceNode(Node capabilitiesNd) {
        Node serviceNd = capabilitiesNd.selectSingleNode("Service");
        assertNotNull("No Service element", serviceNd);

        Element serviceNameNd = (Element) serviceNd.selectSingleNode("Name");
        assertNotNull("No Service/Name element", serviceNameNd);
        String serviceName = serviceNameNd.getText();
        assertEquals("Service name is not OGC:WMS", "OGC:WMS", serviceName);

        Element serviceTitleNd = (Element) serviceNd.selectSingleNode("Title");
        assertNotNull("No Service/Title element", serviceTitleNd);
        assertEquals("Incorrect service/Title", "test title", serviceTitleNd.getText());

        Element serviceAbstractNd = (Element) serviceNd.selectSingleNode("Abstract");
        assertNotNull("No Service/Abstract element", serviceAbstractNd);
        assertEquals("Incorrect Service/Abstract", "test abstract", serviceAbstractNd.getText());

        Element serviceKeywordListNd = (Element) serviceNd.selectSingleNode("KeywordList");
        assertNotNull("No Service/KeywordList element", serviceKeywordListNd);
        List<Node> keywordNodes = serviceKeywordListNd.selectNodes("Keyword");
        assertEquals("Incorrect Service/KeywordList", "kw1", keywordNodes.get(0).getText());
        assertEquals("Incorrect Service/KeywordList", "kw2", keywordNodes.get(1).getText());
        assertEquals("Incorrect Service/KeywordList", 2, keywordNodes.size());

        Element serviceOnlineResourceNd = (Element) serviceNd.selectSingleNode("OnlineResource");
        verifyOnlineResourceElement(serviceOnlineResourceNd, "http://www.geolatte.org");
    }

    private void verifyOnlineResourceElement(Element onlineResource, String expectedHref) {
        assertNotNull("OnlineResource element is required.", onlineResource);
        Namespace xlinkNs = new Namespace("xlink", "http://www.w3.org/1999/xlink");
        assertEquals("Incorrect or missing onlineresource link attribute", "simple", onlineResource.attributeValue(new QName("type", xlinkNs)));
        Attribute olrhref = (Attribute) onlineResource.selectSingleNode("@xhref");
        assertEquals("Incorrect or missing onlineresource link attribute", expectedHref, onlineResource.attributeValue(new QName("href", xlinkNs)));
    }
}
