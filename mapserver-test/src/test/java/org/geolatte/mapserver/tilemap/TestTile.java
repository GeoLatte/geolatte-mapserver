package org.geolatte.mapserver.tilemap;

import org.geolatte.mapserver.TMSTestSupport;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.image.Imaging;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class TestTile {

    private Imaging imaging = ServiceLocator.defaultInstance().imaging();

    @Test
    public void test_loading_images() throws IOException {
        Set<Tile> tiles = TMSTestSupport.getTestTiles();
        for (Tile tile : tiles) {
            Image img = tile.getImage(imaging, false).get();
            assertTrue(img.getInternalRepresentation() instanceof BufferedImage);
        }
    }
}
