package org.geolatte.mapserver.img;

import com.sun.javafx.collections.FloatArraySyncer;
import org.geolatte.mapserver.core.ImageFormat;
import org.geolatte.mapserver.spi.Imaging;
import org.geolatte.mapserver.tilemap.TileImage;
import org.geolatte.mapserver.util.PixelRange;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.geolatte.mapserver.img.ImageComparator.assertTileImageEquals;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/04/2018.
 */
public class BasicImagingTest {

    private static final Logger logger = LoggerFactory.getLogger(BasicImagingTest.class);

    Imaging imaging = new BasicImaging();
    TileImage img;

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
        TileImage cropped = imaging.crop(img, range);
        TileImage received = testImageAfterIO(cropped, ImageFormat.JPEG);
        assertEquals(100, cropped.getWidth());
        assertEquals(80, cropped.getHeight());
        TileImage expected = readTileImage("test1-cropped.jpg", true);
        assertTileImageEquals(expected, received);
    }

    @Test
    public void testOverlay() throws IOException{
        TileImage img1 = readTileImage("test1.png", false);
        TileImage img2 = readTileImage("test2.png", false);
        TileImage result = imaging.overlay(img1, img2);
        TileImage received = testImageAfterIO(result, ImageFormat.PNG);
        TileImage expected = readTileImage("test1-test2-overlay.png", true);
        assertTileImageEquals(expected, received);
    }

    @Test
    public void testAffineTransformTranslateAndScale() throws IOException {
        TileImage img1 = readTileImage("test3.png", false);
        AffineTransform atf = new AffineTransform(0.5, 0, 0, 1.0, 100, 50);
        TileImage result = imaging.affineTransform(img1, 100, 50, 0.5, 1.0);
        assertEquals(100, result.getMinX());
        assertEquals(50, result.getMinY());
        assertEquals(256, result.getHeight());
        assertEquals(128, result.getWidth(), 0.00001);
        TileImage received = testImageAfterIO(result, ImageFormat.PNG);
        TileImage expected = readTileImage("test3-transform-scale-translate.png", true);
        assertTileImageEquals(expected, received);
    }

    @Test
    public void testMosaicNoCrop() throws IOException {
        TileImage img00 = readTileImage("00.png", false, 0,256);
        TileImage img01 = readTileImage("01.png", false, 0, 0);
        TileImage img10 = readTileImage("10.png", false, 256, 256);
        TileImage img11 = readTileImage("11.png", false, 256, 0);
        List<TileImage> images = Arrays.asList(img00, img01, img10 , img11);
        TileImage result = imaging.mosaic(images, new PixelRange(0, 0, 512, 512));
        assertEquals(0, result.getMinX());
        assertEquals(0, result.getMinY());
        assertEquals(512,result.getWidth());
        assertEquals(512, result.getHeight());
        TileImage received = testImageAfterIO(result, ImageFormat.PNG);
        TileImage expected = readTileImage("mosaic-no-crop.png", true);
        assertTileImageEquals(expected, received);
    }

    @Test
    public void testMosaicCrop() throws IOException {
        TileImage img00 = readTileImage("00.png", false, 0,256);
        TileImage img01 = readTileImage("01.png", false, 0, 0);
        TileImage img10 = readTileImage("10.png", false, 256, 256);
        TileImage img11 = readTileImage("11.png", false, 256, 0);
        List<TileImage> images = Arrays.asList(img00, img01, img10 , img11);
        TileImage result = imaging.mosaic(images, new PixelRange(128, 128, 256,  256));
        assertEquals(128, result.getMinX());
        assertEquals(128, result.getMinY());
        assertEquals(256,result.getWidth());
        assertEquals(256, result.getHeight());
        TileImage received = testImageAfterIO(result, ImageFormat.PNG);
        TileImage expected = readTileImage("mosaic-crop.png", true);
        assertTileImageEquals(expected, received);
    }

    private void testScale(double factor, String expectedImageFile) throws IOException {
        Dimension dim = img.getDimension();
        Dimension newDim = new Dimension((int)(dim.width * factor), (int)(dim.height * factor));
        TileImage result = imaging.scale(img, newDim);
        TileImage received = testImageAfterIO(result, ImageFormat.JPEG);
        assertEquals(newDim, result.getDimension());
        TileImage expected = readTileImage(expectedImageFile, true);
        assertTileImageEquals(expected, received);
    }




    //writing an image to file, and reading it back in doesn't result in exactly the same image. Therefore, first write
    //to disk, read result, and then compare to the expected file.
    private TileImage testImageAfterIO(TileImage result, ImageFormat fmt) throws IOException {
        File out = File.createTempFile("test-", "." + fmt.getExt());
        logger.info("Writing outfile: " + out.getAbsolutePath());
        ImageIO.write(result.getInternalRepresentation(BufferedImage.class), fmt.name(), out);
        return readTileImage(out, 0, 0);
    }


    private TileImage readTileImage(String filename, boolean isExpected) throws IOException {
        return readTileImage(filename, isExpected, 0, 0);
    }
    private TileImage readTileImage(String filename, boolean isExpected, int minX, int minY) throws IOException {

        Path root = Paths.get("mapserver-imageops", "src", "test", "resources", "img");
        if (isExpected) {
            root = root.resolve("expected");
        }
        File in = root.resolve(filename).toFile();
        logger.info("Reading file: " + in.getAbsolutePath());
        return readTileImage(in, minX, minY);
    }

    private TileImage readTileImage(File in, int minX, int minY) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(in);
        return new BasicTileImage(bufferedImage, minX,minY);
    }
}
