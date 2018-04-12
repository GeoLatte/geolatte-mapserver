package org.geolatte.mapserver.wms;

/**
 * <p/>
 * <p>
 * <i>Creation-Date</i>: 19/10/11<br>
 * <i>Creation-Time</i>:  16:09<br>
 * </p>
 *
 * @author Jeroen
 * @author <a href="http://www.qmino.com">Qmino bvba</a>
 */

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.parsing.Parser;
import org.geolatte.mapserver.ImageComparator;
import org.geolatte.mapserver.app.JettyServer;
import org.geolatte.mapserver.config.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.*;

public class TestWmsUsingRestAssured {

    boolean firstStart = true;
    String BASE_URL = "http://localhost:8090";
    String configFile = "src/test/resources/test-config.xml";

    /**
     * In the setUp, the Jetty server is configured with the configFile and started.
     * Also the default RestAssured XML parser is registered for the OGC XML MIME types.
     */
    @Before
    public void setUp() throws Exception {
        if (firstStart) {
            RestAssured.registerParser(OGCMIMETypes.CAPABILITIES, Parser.XML);
            RestAssured.registerParser(OGCMIMETypes.SERVICE_EXCEPTION_XML, Parser.XML);

            System.setProperty(Configuration.CONFIG_PATH_PROPERTY_NAME, configFile);

            JettyServer.start();

            firstStart = false;
        }
    }

    @After
    public void tearDown() throws Exception {
        JettyServer.stop();
    }

    /**
     * Default request values for a getMap request based on the current values in configFile.
     */
    private Map<String, String> getDefaultGetMapParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("REQUEST", "GetMap");
        params.put("VERSION", "1.1.1");
        params.put("SERVICE", "WMS");
        params.put("LAYERS", "osm");
        params.put("FORMAT", "image/png");
        params.put("STYLES", "");
        params.put("SRS", "EPSG:900913");
        params.put("BBOX", "-10018754.17,-10018754.17,0,0");
        params.put("WIDTH", "256");
        params.put("HEIGHT", "256");
        params.put("TRANSPARENT", "TRUE");
        return params;
    }

    /**
     * This test makes a getMap request to the mapserver. The boundingBox, width and height are chosen so that the
     * returned image should map exactly onto one tile. That tile is read from disk and compared with the returned
     * image.
     */
    @Test
    public void testGetMapWithBoundingBoxOfTileReturnsTile() throws Exception {
        Map<String, String> params = getDefaultGetMapParams();
        double origin = -20037508.34;
        params.put("BBOX", origin / 2 + "," + origin / 2 + "," + 0 + "," + 0);

        byte[] result = given().parameters(params).get(BASE_URL + "/wms").asByteArray();

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(result));
        BufferedImage expected = ImageIO.read(new FileInputStream("src/test/resources/tiles/osm/2/1/1.png"));

        assertTrue(ImageComparator.equals(flatten(expected), flatten(image)));
    }

    /**
     * Helper method to draw a BufferedImage onto a new 8-bit RGB image.
     *
     * @param image The image to be flattened.
     * @return a new 8-bit RGB BufferedImage with the given image drawn in it.
     */
    private BufferedImage flatten(BufferedImage image) {
        BufferedImage flattenedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        flattenedImage.createGraphics().drawImage(image, 0, 0, Color.BLACK, null);
        return flattenedImage;
    }

    /**
     * Checking the service exception report when requesting an undefined layer.
     */
    @Test
    public void testServiceException_UndefinedLayer() {
        Map<String, String> params = getDefaultGetMapParams();
        String someUndefinedLayerName = "someUndefinedLayerName";
        params.put("LAYERS", someUndefinedLayerName);
        expect().
                body("ServiceExceptionReport.ServiceException",
                        equalTo("Layer " + someUndefinedLayerName + " not defined")).
                given().parameters(params).
                when().get(BASE_URL + "/wms");
    }

    /**
     * Checking the service exception report when a request with an invalid SRS is done.
     */
    @Test
    public void testServiceException_InvalidSRS() {
        Map<String, String> params = getDefaultGetMapParams();
        String invalidEpsg = "EPSG:INVALID";
        params.put("SRS", invalidEpsg);
        expect().
                body("ServiceExceptionReport.ServiceException",
                        equalTo("Can't set parameter SRS to value " + invalidEpsg)).
                given().parameters(params).
                when().get(BASE_URL + "/wms");
    }

    /**
     * Default request values for a getCapabilities request based on the current values in configFile.
     */
    private Map<String, String> getDefaultGetCapabilitiesParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("REQUEST", "GetCapabilities");
        params.put("VERSION", "1.1.1");
        params.put("SERVICE", "WMS");
        params.put("UPDATESEQUENCE", "0");
        return params;
    }

    /**
     * Some values in the getCapabilities response are checked. This test is based on the current values in configFile.
     */
    @Test
    public void testGetCapabilities() {
        Map<String, String> params = getDefaultGetCapabilitiesParams();
        expect().
                rootPath("WMT_MS_Capabilities").
                body("Service.Name",
                        equalTo("OGC:WMS")).and().
                body("Capability.Request.GetMap.Format",
                        hasItem("image/png")).and().
                body("Capability.Layer.Layer.SRS",
                        hasItem("EPSG:900913")).and().
                given().parameters(params).get(BASE_URL + "/wms").asString();
    }

    /**
     * Currently a java.lang.IllegalArgumentException (Can't find the request parameter ...) is thrown
     * as a result of this invocation.
     */
    @Test
    public void testEmptyRequest() {
        expect().
                body(nullValue()).
                when().
                get(BASE_URL + "/wms");
    }
}