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

package org.geolatte.mapserver.tms;

import org.geolatte.geom.Envelope;
import org.geolatte.mapserver.img.JAIImaging;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.media.jai.PlanarImage;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestBoundingBoxOp {

    TileMap tileMap;
    TileMap orthoMap = TMSTestSupport.makeOrthoTileMap();

    public TestBoundingBoxOp() throws TileMapCreationException {
        tileMap = TileMapBuilder.fromURL(TMSTestSupport.URL).buildTileMap();
    }

    @Test
    public void test_normal_execute() throws IOException {
        Envelope bbox = new Envelope(-170, -80, 170, 80, tileMap.getSRS());
        File f = new File("/tmp/normal-execute-tilecache.png");
        executeAndWriteToFile(bbox, f, tileMap);

        bbox = new Envelope(20000, 160000, 180000, 240000, orthoMap.getSRS());
        f = new File("/tmp/normal-execute-ortho.png");
        executeAndWriteToFile(bbox, f, orthoMap);

    }

    private void executeAndWriteToFile(Envelope bbox, File f, TileMap map) throws IOException {
        BoundingBoxOp boundingBoxOp = new BoundingBoxOp(map, bbox, new Dimension(512, 256), new JAIImaging());
        TileImage tileImage = boundingBoxOp.execute();
        PlanarImage received = (PlanarImage) tileImage.getInternalRepresentation();
        assertEquals(256, received.getBounds().getHeight(), 0.0000005);
        assertEquals(512, received.getBounds().getWidth(), 0.0000005);
        ImageIO.write(received, "PNG", f);
    }

    @Test
    public void test_bbox_partially_exceeds_tileset_bounds() throws IOException {

        Envelope bbox = new Envelope(0, 0, 190, 110, tileMap.getSRS());
        File f = new File("/tmp/partially-exceeds-execute-tilecache.png");
        executeAndWriteToFile(bbox, f, tileMap);

        bbox = new Envelope(10000, 140000, 190000, 230000, orthoMap.getSRS());
        f = new File("/tmp/partially-exceeds-execute-ortho.png");
        executeAndWriteToFile(bbox, f, orthoMap);

    }

    @Test
    public void test_bbox_fully_exceeds_tileset_bounds() throws IOException {

        Envelope bbox = new Envelope(-180, -100, 200, 90, tileMap.getSRS());
        File f = new File("/tmp/fully-exceeds-execute-tilecache.png");
        executeAndWriteToFile(bbox, f, tileMap);

        bbox = new Envelope(10000, 140000, 270000, 270000, orthoMap.getSRS());
        f = new File("/tmp/fully-exceeds-execute-ortho.png");
        executeAndWriteToFile(bbox, f, orthoMap);

    }

    @Test
    public void test_bbox_outside_of_tilemap_extent_returns_empty_image() throws IOException {

        Envelope bbox = new Envelope(300, 300, 400, 400, tileMap.getSRS());
        File f = new File("/tmp/empty-image-because-bbox-not-in-extent-tilecache.png");
        executeAndWriteToFile(bbox, f, tileMap);

        bbox = new Envelope(300, 300, 400, 400, orthoMap.getSRS());
        f = new File("/tmp/empty-image-because-bbox-not-in-extent-orthos.png");
        executeAndWriteToFile(bbox, f, orthoMap);

    }

    @Test
    public void test_empty_bbox_returns_empty_image() throws IOException {

        Envelope bbox = new Envelope(1, 1, -1, -1, tileMap.getSRS());
        File f = new File("/tmp/empty-image-because-empty-bbox-tilecache.png");
        executeAndWriteToFile(bbox, f, tileMap);

        bbox = new Envelope(50000, 200000, 50000, 200000, orthoMap.getSRS());
        f = new File("/tmp/empty-image-because-empty-bbox-orthos.png");
        executeAndWriteToFile(bbox, f, orthoMap);

        bbox = new Envelope(50000, 200000, 50002, 200002, orthoMap.getSRS());
        f = new File("/tmp/empty-image-because-empty-bbox-orthos.png");
        executeAndWriteToFile(bbox, f, orthoMap);

    }


}
