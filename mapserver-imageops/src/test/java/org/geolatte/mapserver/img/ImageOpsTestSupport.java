package org.geolatte.mapserver.img;

import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.image.ImageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;

import static java.lang.String.format;
import static org.junit.Assert.assertTrue;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public class ImageOpsTestSupport {

    private static final Logger logger = LoggerFactory.getLogger(ImageOpsTestSupport.class);

    public static void assertImageEquals(Image expected, Image received) {
        assertImageEquals(expected.getInternalRepresentation(RenderedImage.class),
                received.getInternalRepresentation(RenderedImage.class)
        );
    }

    public static void assertImageEquals(RenderedImage expected, RenderedImage received) {
        assertTrue(ImageComparator.equals(expected, received));
    }

    public static Image readTileImage(String filename, boolean isExpected) throws IOException {
        return readTileImage(filename, isExpected, 0, 0);
    }

    public static Image readTileImage(String filename, boolean isExpected, int minX, int minY) throws IOException {

        String resourceName = format("img/%s", filename);
        if (isExpected) {
            resourceName = format("img/expected/%s", filename);
        }
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
            logger.info("Reading file: " + resourceName);
            return readTileImage(in, minX, minY);
        }
    }

    public static Image readTileImage(File file, int minX, int minY) throws IOException {
        return new BasicImage(ImageIO.read(file), minX, minY);
    }

    public static Image fromBytes(byte[] bytes) throws IOException {
        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(bytes));
        return new BasicImage(bi, 0, 0);
    }

    public static Image readTileImage(InputStream in, int minX, int minY) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(in);
        return new BasicImage(bufferedImage, minX, minY);
    }

    public static File writeImageToFile(Image result, ImageFormat fmt) throws IOException {
        File out = File.createTempFile("test-", "." + fmt.getExt());
        logger.info("Writing outfile: " + out.getAbsolutePath());
        ImageIO.write(result.getInternalRepresentation(BufferedImage.class), fmt.name(), out);
        return out;
    }

    public static File writeTextFileToTmp(byte[] bytes, String extension) throws IOException{
        File out = File.createTempFile("test-", "." + extension);
        logger.info("Writing outfile: " + out.getAbsolutePath());
        try (FileWriter writer = new FileWriter(out)){
            String text = new String(bytes, "UTF-8");
            writer.write(text);
        }
        return out;
    }

    //writing an image to file, and reading it back in doesn't result in exactly the same image. Therefore, first write
    //to disk, read result, and then compare to the expected file.
    public static Image imageAfterIO(Image result, ImageFormat fmt) throws IOException {
        File out = writeImageToFile(result, fmt);
        return readTileImage(out, 0, 0);
    }



}


