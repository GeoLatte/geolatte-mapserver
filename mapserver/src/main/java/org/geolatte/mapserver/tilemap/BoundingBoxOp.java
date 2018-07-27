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
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.image.Imaging;
import org.geolatte.mapserver.util.Chrono;
import org.geolatte.mapserver.util.PixelRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.geolatte.mapserver.util.EnvelopUtils.height;
import static org.geolatte.mapserver.util.EnvelopUtils.width;

/**
 * Creates the image from a <code>TileMap</code>that best fits a <code>BoundingBox</code>.
 *
 * <p>This operation proceeds as follows: <ol>
 * <li>the requested map units per pixel (upp) are calculated from the specified bounding box and image dimensions</li>
 * <li>the <code>TileSet</code> of the specified <code>TileMap</code> is determined whose upp is closest to the requested upp</li>
 * <li>the <code>Tile</code>s are determined that overlap the requested <code>BoundingBox</code> in this <code>TileSet</code></li>
 * <li>the images for these <code>Tile</code>s are retrieved (from disc or a network connection)</li>
 * <li>the images are mosaiced to a single image and cropped on the requested bounding box</li>
 * <li>the result is rescaled to fit in the requested bounding box</li>
 *
 * </p>
 * <!-- TODO: explain behavior when bbox exceeds tilemap -->
 *
 * @author Karel Maesen, Geovise BVBA
 */
public class BoundingBoxOp implements TileMapOperation<Image> {

    private final static Logger LOGGER = LoggerFactory.getLogger(BoundingBoxOp.class);

    final private Envelope<C2D> requestedBbox;
    final private Dimension dimension;
    private final Imaging imaging;
    final private TileMap tileMap;
    private Envelope<C2D> tileSetClippedBbox;
    private int tileSet;
    private java.util.List<Tile> tiles;
    private java.util.List<Image> images = new ArrayList<>();
    private Image result;
    private Chrono chrono;
    private PixelRange imgBounds;

    /**
     * Constructs this operation
     *
     * @param tileMap     the <code>TileMap</code> on which to operate
     * @param boundingBox the <code>BoundingBox</code> for the result
     * @param dimension   the image dimensions of the result
     *
     */
    public BoundingBoxOp(TileMap tileMap, Envelope<C2D> boundingBox, Dimension dimension, ServiceLocator locator) {
        this.tileMap = tileMap;
        this.dimension = dimension;
        this.requestedBbox = boundingBox;
        this.imaging = locator.imaging();
        this.tileSetClippedBbox = this.tileMap.clipToMaxBoundingBox(boundingBox);
    }

    public BoundingBoxOp(TileMap tileMap, Envelope<C2D> boundingBox, Dimension dimension) {
        this(tileMap, boundingBox, dimension, ServiceLocator.defaultInstance());
    }

    /**
     * Executes this operation
     *
     * @return the <code>TileImage</code> showing the part of the tile map within the bounding box and having
     * the specified image dimensions.
     */
    @Override
    public Image execute() {
        if (tileSetClippedBbox.isEmpty() || width(tileSetClippedBbox) < 1 || height(tileSetClippedBbox) < 1) {
            return imaging.createEmptyImage(this.dimension, this.tileMap.getTileImageFormat());
        }
        chrono = new Chrono();
        chooseTileSet();
        getTiles();
        if (tiles.isEmpty()) return imaging.createEmptyImage(dimension, this.tileMap.getTileImageFormat());
        chrono.reset();
        getTileImages();
        mosaic();
        scale();
        LOGGER.debug("Image processing took " + chrono.stop() + " ms.");
        LOGGER.debug("Total execution is: " + chrono.total() + " ms.");
        return result;
    }

    private void chooseTileSet() {
        TileSetChooser tsc = new TileSetChooser(tileMap, requestedBbox, dimension);
        tileSet = tsc.chooseTileSet();
        LOGGER.debug("TileSet chosen has order = " + tileSet);
    }

    protected List<Tile> getTiles() {
        tiles = tileMap.getTilesFor(tileSet, tileSetClippedBbox);
        return tiles;
    }

    protected boolean getIsForceArgb(){
        return this.tileMap.isForceArgb();
    }

    protected void getTileImages() {
        TileImageLoadOp loadOp = new TileImageLoadOp(this.tiles, this.imaging, tileMap.isForceArgb());
        setImages(loadOp.execute());
        LOGGER.debug("Image loading took " + chrono.stop() + " ms.");
    }

    private void mosaic() {
        imgBounds = tileMap.pixelBounds(tileSet, tileSetClippedBbox);
        result = imaging.mosaic(images, imgBounds);
    }


    private void scale() {
        if (!tileSetClippedBbox.equals(requestedBbox)) {
            //if the ows bbox is extends beyond the
            // bbox of the TileSet, then we must embed
            // the result in a larger, empty image
            embedInEmptyImage();
        } else {
            result = imaging.scale(result, dimension);
        }
    }

    private void embedInEmptyImage() {
        Image empty = createEmptyBackgroundImage();
        applyEmbeddingTransform();
        result = imaging.overlay(empty, result);
    }

    private void applyEmbeddingTransform() {
        MapUnitToPixelTransform mupTransform = new MapUnitToPixelTransform(requestedBbox, new PixelRange(0, 0, (int) dimension.getWidth(), (int) dimension.getHeight()));
        PixelRange destRange = mupTransform.toPixelRange(tileSetClippedBbox);
        double sx = (double) destRange.getWidth() / (double) result.getWidth();
        int tx = destRange.getMinX() - (int)Math.floor(result.getMinX() * sx);
        double sy = (double) destRange.getHeight() / (double) result.getHeight();
        int ty = destRange.getMinY() - (int)Math.floor(result.getMinY() * sy);
        result = imaging.affineTransform(result, tx, ty, sx, sy);
    }

    private Image createEmptyBackgroundImage() {
        Image empty = imaging.createEmptyImage(result, dimension);
        return empty;
    }

    protected void setImages(List<Image> images){
        this.images = images;
    }

}
