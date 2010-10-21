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

package org.geolatte.mapserver.img;

import org.geolatte.mapserver.tms.TileImage;
import org.junit.Test;

import java.awt.*;
import java.io.*;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: Jul 21, 2010
 */
public class TestJAITileImage {

    @Test
    public void test_create_empty_transparent_png_image() {
        Dimension dim = new Dimension(512, 256);
        Imaging imaging = new JAIImaging();
        TileImage img = imaging.createEmptyImage(dim, ImageFormat.PNG);
        File f = new File("/tmp/empty-img.png");
        write(img, f, ImageFormat.PNG);
    }

    @Test
    public void test_create_empty_transparent_jpeg_image() {
        Dimension dim = new Dimension(512, 256);
        Imaging imaging = new JAIImaging();
        TileImage img = imaging.createEmptyImage(dim, ImageFormat.JPEG);
        File f = new File("/tmp/empty-img.jpeg");
        write(img, f, ImageFormat.JPEG);
    }


    private void write(TileImage img, File f, ImageFormat format) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(f);
            img.write(os, format);
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

//    PlanarImage received = (PlanarImage) tileImage.getInternalRepresentation();
//        assertEquals(256, received.getBounds().getHeight(), 0.0000005);
//        assertEquals(512, received.getBounds().getWidth(), 0.0000005);
//        ImageIO.write(received, "PNG", f);
}
