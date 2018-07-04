package org.geolatte.mapserver.img;

import org.geolatte.mapserver.image.Image;

import java.awt.*;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;

import static org.junit.Assert.assertTrue;

/**
 * @author Karel Maesen, Geovise BVBA
 * creation-date: 9/10/11
 */
public class ImageComparator {

    public static boolean equals(RenderedImage img1, RenderedImage img2) {
        if (img1 == img2) return true;
        if (img1 == null || img2 == null) return false;
        Raster r1 = img1.getData();
        Raster r2 = img2.getData();

        if (!sameBounds(r1, r2)) return false;
        if (!sameBands(r1, r2)) return false;

        if (!sameRasterData(r1, r2)) return false;

        return true;
    }

    private static boolean sameBands(Raster r1, Raster r2) {
        return r1.getNumBands() == r2.getNumBands();
    }

    private static boolean sameBounds(Raster r1, Raster r2) {
        Rectangle r1Bounds = r1.getBounds();
        Rectangle r2Bounds = r2.getBounds();
        return (r1Bounds.equals(r2Bounds));
    }

    private static boolean sameRasterData(Raster r1, Raster r2) {
        for (int i = 0; i < r1.getNumBands(); i++) {
            if (!sameRasterBand(r1, r2, i)) return false;
        }
        return true;
    }

    private static boolean sameRasterBand(Raster r1, Raster r2, int band) {
        for (int xIdx = 0; xIdx < r1.getWidth(); xIdx++) {
            for (int yIdx = 0; yIdx < r1.getHeight(); yIdx++) {
                if (r1.getSampleDouble(xIdx + r1.getMinX(), yIdx + r1.getMinY(), band) !=
                        r2.getSampleDouble(xIdx + r2.getMinX(), yIdx + r2.getMinY(), band)) {
                    return false;
                }
            }
        }
        return true;
    }
}