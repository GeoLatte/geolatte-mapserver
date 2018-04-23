package org.geolatte.mapserver.img;

import org.geolatte.mapserver.spi.Imaging;
import org.geolatte.mapserver.tilemap.TileImage;
import org.geolatte.mapserver.util.PixelRange;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        TileImage received = testImageAfterIO(cropped);
        assertEquals(100, cropped.getWidth());
        assertEquals(80, cropped.getHeight());
        TileImage expected = readTileImage("test1-cropped.jpg", true);
        assertTileImageEquals(expected, received);
    }

    public void testScale(double factor, String expectedImageFile) throws IOException {
        Dimension dim = img.getDimension();
        Dimension newDim = new Dimension((int)(dim.width * factor), (int)(dim.height * factor));
        TileImage result = imaging.scale(img, newDim);
        TileImage received = testImageAfterIO(result);
        assertEquals(newDim, result.getDimension());
        TileImage expected = readTileImage(expectedImageFile, true);
        assertTileImageEquals(expected, received);
    }


    //writing an image to file, and reading it back in doesn't result in exactly the same image. Therefore, first write
    //to disk, read result, and then compare to the expected file.
    private TileImage testImageAfterIO(TileImage result) throws IOException {
        File out = File.createTempFile("test-", ".jpg");
        logger.info("Writing outfile: " + out.getAbsolutePath());
        ImageIO.write(result.getInternalRepresentation(BufferedImage.class), "JPEG", out);
        return readTileImage(out);
    }

    private TileImage readTileImage(String filename, boolean isExpected) throws IOException {

        Path root = Paths.get("mapserver-imageops", "src", "test", "resources", "img");
        if (isExpected) {
            root = root.resolve("expected");
        }
        File in = root.resolve(filename).toFile();
        logger.info("Reading file: " + in.getAbsolutePath());
        return readTileImage(in);
    }

    private TileImage readTileImage(File in) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(in);
        return new BasicTileImage(bufferedImage);
    }
}
