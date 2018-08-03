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

package org.geolatte.mapserver.tilemap;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.mapserver.TMSTestSupport;
import org.geolatte.mapserver.image.Image;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestBoundingBoxOp {


    private TileMap tileMap;
    private TileMap orthoMap;

    @Before
    public void setup(){
        tileMap = TMSTestSupport.makeOSMTileMap();
        orthoMap = TMSTestSupport.makeOrthoTileMap();
    }

    //TODO -- these tests need to be finalized:
    //  - test against reference images
    //  - ensure envelopes correspond to the TileMap CRS (this is not now the case for the tilecache test cases
    @Test
    public void test_normal_execute() throws IOException {
        Envelope<C2D> bbox = new Envelope<>(-170, -80, 170, 80, tileMap.getCoordinateReferenceSystem());
        File f = new File("/tmp/normal-execute-tilecache.png");
        executeAndWriteToFile(bbox, f, tileMap);

        bbox = new Envelope<>(20000, 160000, 180000, 240000, orthoMap.getCoordinateReferenceSystem());
//        bbox = new Envelope(40000, 180000, 80000, 200000, orthoMap.getCoordinateReferenceSystem());
        f = new File("/tmp/normal-execute-ortho.png");
        executeAndWriteToFile(bbox, f, orthoMap);

    }

    private void executeAndWriteToFile(Envelope bbox, File f, TileMap map) throws IOException {
        BoundingBoxOp boundingBoxOp = new BoundingBoxOp(map, bbox, new Dimension(512, 256));
        Image image = boundingBoxOp.execute().join();
        BufferedImage received =  image.getInternalRepresentation(BufferedImage.class);
        assertEquals(256, received.getHeight(), 0.0000005);
        assertEquals(512, received.getWidth(), 0.0000005);
        ImageIO.write(received, "PNG", f);
    }

    @Test
    public void test_bbox_partially_exceeds_tileset_bounds() throws IOException {

        Envelope<C2D> bbox = new Envelope<>(0, 0, 190, 110, tileMap.getCoordinateReferenceSystem());
        File f = new File("/tmp/partially-exceeds-execute-tilecache.png");
        executeAndWriteToFile(bbox, f, tileMap);

        bbox = new Envelope<>(10000, 140000, 190000, 230000, orthoMap.getCoordinateReferenceSystem());
        f = new File("/tmp/partially-exceeds-execute-ortho.png");
        executeAndWriteToFile(bbox, f, orthoMap);

    }

    @Test
    public void test_bbox_fully_exceeds_tileset_bounds() throws IOException {

        Envelope<C2D> bbox = new Envelope<>(-180, -100, 200, 90, tileMap.getCoordinateReferenceSystem());
        File f = new File("/tmp/fully-exceeds-execute-tilecache.png");
        executeAndWriteToFile(bbox, f, tileMap);

        bbox = new Envelope<>(10000, 140000, 270000, 270000, orthoMap.getCoordinateReferenceSystem());
        f = new File("/tmp/fully-exceeds-execute-ortho.png");
        executeAndWriteToFile(bbox, f, orthoMap);

    }

    @Test
    public void test_bbox_outside_of_tilemap_extent_returns_empty_image() throws IOException {

        Envelope<C2D> bbox = new Envelope<>(300, 300, 400, 400, tileMap.getCoordinateReferenceSystem());
        File f = new File("/tmp/empty-image-because-bbox-not-in-extent-tilecache.png");
        executeAndWriteToFile(bbox, f, tileMap);

        bbox = new Envelope<>(300, 300, 400, 400, orthoMap.getCoordinateReferenceSystem());
        f = new File("/tmp/empty-image-because-bbox-not-in-extent-orthos.png");
        executeAndWriteToFile(bbox, f, orthoMap);

    }

    @Test
    public void test_empty_bbox_returns_empty_image() throws IOException {

        Envelope<C2D> bbox = new Envelope<>(1, 1, -1, -1, tileMap.getCoordinateReferenceSystem());
        File f = new File("/tmp/empty-image-because-empty-bbox-tilecache.png");
        executeAndWriteToFile(bbox, f, tileMap);

        bbox = new Envelope<>(50000, 200000, 50000, 200000, orthoMap.getCoordinateReferenceSystem());
        f = new File("/tmp/empty-image-because-empty-bbox-orthos.png");
        executeAndWriteToFile(bbox, f, orthoMap);

        bbox = new Envelope<>(50000, 200000, 50002, 200002, orthoMap.getCoordinateReferenceSystem());
        f = new File("/tmp/empty-image-because-empty-bbox-orthos.png");
        executeAndWriteToFile(bbox, f, orthoMap);

    }


}
