package org.geolatte.mapserver.tilemap;

import org.geolatte.mapserver.TMSTestSupport;
import org.geolatte.mapserver.boot.ServiceRegistry;
import org.geolatte.mapserver.spi.Imaging;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class TestTile {

    private Imaging imaging = ServiceRegistry.getDefault().getImaging();

    @Test
    public void test_loading_images() throws IOException {
        Set<Tile> tiles = TMSTestSupport.getTestTiles();
        for (Tile tile : tiles) {
            TileImage img = tile.getImage(imaging, false);
            assertTrue(img.getInternalRepresentation() instanceof BufferedImage);
        }
    }
}
