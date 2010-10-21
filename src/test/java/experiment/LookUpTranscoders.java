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

package experiment;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static javax.imageio.ImageIO.getImageReadersByFormatName;
import static javax.imageio.ImageIO.getImageWritersByFormatName;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: Jul 20, 2010
 */
public class LookUpTranscoders {

    static public void main(String[] args) throws IOException {

        //listTranscoders();

        ImageInputStream in = null;
        ImageOutputStream on = null;
        try {
            Iterator<ImageWriter> pngWriters = getImageWritersByFormatName("PNG");
            ImageWriter pngWriter = (ImageWriter) pngWriters.next();

            Iterator<ImageReader> jpegReaders = getImageReadersByFormatName("JPEG");
            ImageReader jpegReader = (ImageReader) jpegReaders.next();


            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("img/test1.jpg");
            if (is == null) throw new IllegalStateException("Can't locate test jpeg on classpath (test1.jpg)");
            in = ImageIO.createImageInputStream(is);

            jpegReader.setInput(in);
//            ImageReadParam param = new ImageReadParam();
//            int w = 100;
//            int h = 100;
//            SampleModel sm = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, w,h,4, 4*w, new int[]{0,1,2,3});
//            ColorModel cm = PlanarImage.createColorModel(sm);
//            ImageTypeSpecifier destType = new ImageTypeSpecifier(cm ,sm);
//            param.setDestinationType(destType);
//            param.setDestinationBands(new int[]{0,1,2});
            BufferedImage inImage = jpegReader.read(0, null);
            on = ImageIO.createImageOutputStream(new File("/tmp/test1-out.png"));
            pngWriter.setOutput(on);
            pngWriter.write(inImage);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) in.close();
            if (on != null) on.close();
        }

        //and reverse
        try {
            Iterator<ImageWriter> jpegWriters = getImageWritersByFormatName("JPEG");
            ImageWriter jpegWriter = (ImageWriter) jpegWriters.next();

            Iterator<ImageReader> pngReaders = getImageReadersByFormatName("PNG");
            ImageReader pngReader = (ImageReader) pngReaders.next();


            File is = new File("/tmp/partially-exceeds-execute.png");
            if (is == null) throw new IllegalStateException("Can't locate png");
            in = ImageIO.createImageInputStream(is);
            pngReader.setInput(in);

            RenderedImage inJpeg = pngReader.readAsRenderedImage(0, null);


            on = ImageIO.createImageOutputStream(new File("/tmp/test1-out.jpeg"));
            jpegWriter.setOutput(on);
            jpegWriter.write(inJpeg);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) in.close();
            if (on != null) on.close();
        }

    }

    private static void listTranscoders() {
        Iterator<ImageWriter> pngWriters = getImageWritersByFormatName("PNG");
        ImageWriter pngWriter = null;
        ImageReader jpegReader = null;
        int i, j;
        for (; pngWriters.hasNext();) {
            pngWriter = (ImageWriter) pngWriters.next();
            Iterator<ImageReader> jpegReaders = getImageReadersByFormatName("JPEG");
            System.out.println("pngWriter = " + pngWriter);
            for (; jpegReaders.hasNext();) {
                jpegReader = (ImageReader) jpegReaders.next();
                System.out.println("jpegReader = " + jpegReader);
                Iterator transcoders = ImageIO.getImageTranscoders(jpegReader, pngWriter);
                for (; transcoders.hasNext();) {
                    System.out.println("transcoder = " + transcoders.next());
                }
            }
        }
    }
}
