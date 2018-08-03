package org.geolatte.mapserver.img;

import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.image.ImageFormat;
import org.geolatte.mapserver.image.Imaging;
import org.geolatte.mapserver.util.PixelRange;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.geolatte.mapserver.img.ImageOpsTestSupport.*;
import static org.junit.Assert.assertEquals;


/**
 * Created by Karel Maesen, Geovise BVBA on 19/04/2018.
 */
public class BasicImagingTest {

    private static final Logger logger = LoggerFactory.getLogger(BasicImagingTest.class);

    private Imaging imaging = new BasicImaging();
    private Image img;

    @Before
    public void setUp() throws IOException {
        img = readTileImage("test1.jpg", false);
    }

    @Test
    public void testScaleUp() throws IOException {
        testScale(1.5, "test1-scaled-up-50perc.jpg");
    }

    @Test
    public void testScaleDown() throws IOException {
        testScale(0.8, "test1-scaled-down-20perc.jpg");
    }

    @Test
    public void testCrop() throws IOException {
        PixelRange range = new PixelRange(50, 50, 100, 80);
        Image cropped = imaging.crop(img, range);
        Image received = imageAfterIO(cropped, ImageFormat.JPEG);
        assertEquals(100, cropped.getWidth());
        assertEquals(80, cropped.getHeight());
        Image expected = readTileImage("test1-cropped.jpg", true);
        assertImageEquals(expected, received);
    }

    @Test
    public void testOverlay() throws IOException{
        Image img1 = readTileImage("test1.png", false);
        Image img2 = readTileImage("test2.png", false);
        Image result = imaging.overlay(img1, img2);
        Image received = imageAfterIO(result, ImageFormat.PNG);
        Image expected = readTileImage("test1-test2-overlay.png", true);
        assertImageEquals(expected, received);
    }

    @Test
    public void testAffineTransformTranslateAndScale() throws IOException {
        Image img1 = readTileImage("test3.png", false);
        Image result = imaging.affineTransform(img1, 100, 50, 0.5, 1.0);
        assertEquals(100, result.getMinX());
        assertEquals(50, result.getMinY());
        assertEquals(256, result.getHeight());
        assertEquals(128, result.getWidth(), 0.00001);
        Image received = imageAfterIO(result, ImageFormat.PNG);
        Image expected = readTileImage("test3-transform-scale-translate.png", true);
        assertImageEquals(expected, received);
    }

    @Test
    public void testMosaicNoCrop() throws IOException {
        Image img00 = readTileImage("00.png", false, 0,256);
        Image img01 = readTileImage("01.png", false, 0, 0);
        Image img10 = readTileImage("10.png", false, 256, 256);
        Image img11 = readTileImage("11.png", false, 256, 0);
        List<Image> images = Arrays.asList(img00, img01, img10 , img11);
        Image result = imaging.mosaic(images, new PixelRange(0, 0, 512, 512));
        assertEquals(0, result.getMinX());
        assertEquals(0, result.getMinY());
        assertEquals(512,result.getWidth());
        assertEquals(512, result.getHeight());
        Image received = imageAfterIO(result, ImageFormat.PNG);
        Image expected = readTileImage("mosaic-no-crop.png", true);
        assertImageEquals(expected, received);
    }

    @Test
    public void testMosaicCrop() throws IOException {
        Image img00 = readTileImage("00.png", false, 0,256);
        Image img01 = readTileImage("01.png", false, 0, 0);
        Image img10 = readTileImage("10.png", false, 256, 256);
        Image img11 = readTileImage("11.png", false, 256, 0);
        List<Image> images = Arrays.asList(img00, img01, img10 , img11);
        Image result = imaging.mosaic(images, new PixelRange(128, 128, 256,  256));
        assertEquals(128, result.getMinX());
        assertEquals(128, result.getMinY());
        assertEquals(256,result.getWidth());
        assertEquals(256, result.getHeight());
        Image received = imageAfterIO(result, ImageFormat.PNG);
        Image expected = readTileImage("mosaic-crop.png", true);
        assertImageEquals(expected, received);
    }

    private void testScale(double factor, String expectedImageFile) throws IOException {
        Dimension dim = img.getDimension();
        Dimension newDim = new Dimension((int)(dim.width * factor), (int)(dim.height * factor));
        Image result = imaging.scale(img, newDim);
        Image received = imageAfterIO(result, ImageFormat.JPEG);
        assertEquals(newDim, result.getDimension());
        Image expected = readTileImage(expectedImageFile, true);
        assertImageEquals(expected, received);
    }

}
