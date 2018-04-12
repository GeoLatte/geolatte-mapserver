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

import org.geolatte.geom.Envelope;
import org.geolatte.geom.Point;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.mapserver.spi.ImageFormat;
import org.geolatte.mapserver.wms.BoundingBoxOpFactory;
import org.geolatte.mapserver.wms.DefaultBoundingBoxOpFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Represents a TileMap in a TMS Service.
 *
 * @author Karel Maesen
 */
public class TileMap {

    private final String serviceUrl;
    private final CrsId srs;
    private final String title;
    private final Envelope maxBoundingBox;
    private final Point origin;
    private final TileFormat tileFormat;
    private final List<TileSet> tileSets;

    private TileImageSourceFactory tileImageSourceFactory = new URLTileImageSourceFactory();
    private BoundingBoxOpFactory boundingBoxOpFactory = new DefaultBoundingBoxOpFactory();
    private boolean forceArgb;

    protected TileMap(String serviceURL, String title, CrsId srs, Envelope bbox,
                      Point origin, TileFormat tileFormat,
                      List<TileSet> tileSets) throws IllegalArgumentException {
        this.serviceUrl = serviceURL;
        this.title = title;
        this.maxBoundingBox = bbox;
        this.tileFormat = tileFormat;
        this.srs = srs;
        this.origin = origin;
        this.tileSets = tileSets;
    }

    void setTileImageSourceFactory(TileImageSourceFactory factory) {
        this.tileImageSourceFactory = factory;
    }

    public BoundingBoxOpFactory getBoundingBoxOpFactory() {
        return boundingBoxOpFactory;
    }

    public void setBoundingBoxOpFactory(BoundingBoxOpFactory boundingBoxOpFactory) {
        this.boundingBoxOpFactory = boundingBoxOpFactory;
    }

    /**
     * Returns the {@link Tile}s in the {@link TileSet} that overlap the <code>BoundingBox</code>.
     *
     * @param set  the <code>TileSet</code>
     * @param bbox the <code>BoundingBox</code>
     * @return the <code>Tile</code>s in the <code>TileSet</code> specified by the set argument that overlap the <code>BoundingBox</code> specified by the bbox argument
     */
    Set<Tile> getTilesFor(TileSet set, Envelope bbox) {
        if (outsideMaxBoundingBox(bbox))
            throw new IllegalArgumentException(String.format("Request BoundingBox: %s exceeds maximum bounding box: %s", bbox.toString(), getBoundingBox().toString()));
        Set<Tile> result = new HashSet<Tile>();
        TileCoordinate llIdx = lowerLeftTileCoordinate(bbox, set);
        TileCoordinate urIdx = upperRightTileCoordinate(bbox, set);
        List<TileCoordinate> coordinateBlock = TileCoordinate.range(llIdx, urIdx);
        for (TileCoordinate tileCoordinate : coordinateBlock) {
            result.add(makeTile(set, tileCoordinate));
        }
        return result;
    }

    /**
     * Determines whether the given <code>BoundingBox</code> falls outside the extent
     * of the extent of this <code>TileMap</code>.
     *
     * @param bbox the <code>BoundingBox</code> to test
     * @return true if the <code>BoundingBox</code> specified by the bbox argument falls at least partly wihtin
     * the extent of this <code>TileMap</code>, and false otherwise.
     */
    public boolean outsideMaxBoundingBox(Envelope bbox) {
        return (bbox.getMinX() < getBoundingBox().getMinX()
                || bbox.getMinY() < getBoundingBox().getMinY()
                || bbox.getMaxX() > getBoundingBox().getMaxX()
                || bbox.getMaxY() > getBoundingBox().getMaxY());
    }

    /**
     * Creates a {@link Tile} for the specified {@link TileCoordinate} in the specified {@link TileSet}.
     *
     * @param set            the <code>TileSet</code>.
     * @param tileCoordinate the <code>TileCoordinate</code>
     * @return a <code>Tile</code> in the <code>TileSet</code> specified by the set argument for the coordinate specified by tileCoordinate argument
     */
    Tile makeTile(TileSet set, TileCoordinate tileCoordinate) {
        TileImageSource source = tileImageSourceFactory.create(set, tileCoordinate, tileFormat.extension);
        return new Tile(source, tileCoordinate, set.getTileCoordinateSpace());
    }

    /**
     * Returns the {@link TileSet}s in this <code>TileMap</code>.
     *
     * @return an unmodifiable list of the <code>TileSet</code>s in this </code>TileMap</code>.
     */
    List<TileSet> getTileSets() {
        return Collections.unmodifiableList(tileSets);
    }

    /**
     * Returns the title of this <code>TileMap</code>.
     *
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Returns the coordinate reference system of this <code>TileMap</code>
     *
     * @return the coordinate reference system
     */
    public CrsId getSRS() {
        return this.srs;
    }

    /**
     * Returns the extent of this <code>TileMap</code>
     *
     * @return a <code>BoundingBox</code> specifying the extent of this <code>TileMap</code>.
     */
    public Envelope getBoundingBox() {
        return this.maxBoundingBox;
    }

    /**
     * Returns the <code>ImageFormat</code> in which the <code>Tile</code>s of this <code>TileMap</code>
     * are stored.
     *
     * @return the <code>ImageFormat</code> of this <code>TileMap</code>'s tiles
     */
    public ImageFormat getTileImageFormat() {
        if ("image/jpeg".equalsIgnoreCase(tileFormat.mimeType))
            return ImageFormat.JPEG;
        else
            return ImageFormat.PNG;
    }

    private TileCoordinate lowerLeftTileCoordinate(Envelope bbox, TileSet set) {
        Point ll = bbox.lowerLeft();
        return set.pointIndex(ll, true);
    }

    private TileCoordinate upperRightTileCoordinate(Envelope bbox, TileSet set) {
        Point ur = bbox.upperRight();
        // if the upperright point falls on the lower or left border of a tile,
        // then that tile should not be returned.
        return set.pointIndex(ur, false);
    }

    /**
     * Clips the specified BoundingBox to the TileMap Boundingbox and returns the result.
     * <p/>
     * The result will fall completely within the bounds of this TileMap.
     *
     * @param bbox the BoundingBox to clip
     * @return the clipped BoundingBox.
     */
    public Envelope clipToMaxBoundingBox(Envelope bbox) {
        return getBoundingBox().intersect(bbox);
    }

    public boolean isForceArgb() {
        return forceArgb;
    }

    public void setForceArgb(boolean forceArgb) {
        this.forceArgb = forceArgb;
    }
}
