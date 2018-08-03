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
import org.geolatte.geom.Point;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.mapserver.image.ImageFormat;
import org.geolatte.mapserver.util.PixelRange;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Comparator.comparingInt;
import static org.geolatte.geom.builder.DSL.point;


/**
 * Represents a TileMap in a TMS Service.
 *
 * @author Karel Maesen
 */
public class TileMap {

    private final String serviceUrl;
    private final CoordinateReferenceSystem<C2D> crs;
    private final String title;
    private final Envelope<C2D> maxBoundingBox;
    private final Point origin;
    private final TileMetadata tileMetadata;
    private final List<TileSet> tileSets;

    private TileImageSourceFactory tileImageSourceFactory;

    private boolean forceArgb;

    TileMap(String serviceURL, String title, CoordinateReferenceSystem<C2D> crs, Envelope<C2D> bbox,
            Point origin, TileMetadata tileMetadata, boolean forceArgb,
            List<TileSet> tileSets) throws IllegalArgumentException {
        this.serviceUrl = serviceURL;
        tileImageSourceFactory = makeTileImageSourceFactory(this.serviceUrl);
        this.title = title;
        this.maxBoundingBox = bbox;
        this.tileMetadata = tileMetadata;
        this.crs = crs;
        this.origin = origin;
        //ensure that tileSets are sorted by order
        tileSets.sort(comparingInt(TileSet::getOrder));
        this.tileSets = tileSets;
        this.forceArgb = forceArgb;
    }

    /**
     * Returns the {@link Tile}s in the {@link TileSet} that overlap the <code>BoundingBox</code>.
     *
     * @param order the order of the <code>TileSet</code>
     * @param bbox  the <code>BoundingBox</code>
     * @return the <code>Tile</code>s in the <code>TileSet</code> specified by the set argument that overlap the <code>BoundingBox</code> specified by the bbox argument
     */
    public List<Tile> getTilesFor(int order, Envelope<C2D> bbox) {
        return getTilesFor(tileSets.get(order), bbox);
    }


    public String getServiceUrl() {
        return serviceUrl;
    }

    public Envelope<C2D> getMaxBoundingBox() {
        return maxBoundingBox;
    }

    public Point getOrigin() {
        return origin;
    }

    public TileMetadata getTileMetadata() {
        return tileMetadata;
    }

    /**
     * Returns the {@link TileSet}s in this <code>TileMap</code>.
     *
     * @return an unmodifiable list of the <code>TileSet</code>s in this </code>TileMap</code>.
     */
    public List<TileSet> getTileSets() {
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

    public CoordinateReferenceSystem<C2D> getCoordinateReferenceSystem() {
        return crs;
    }

    /**
     * Returns the coordinate reference system of this <code>TileMap</code>
     *
     * @return the coordinate reference system
     * @deprecated
     */
    public CrsId getSRS() {
        return this.crs.getCrsId();
    }

    /**
     * Returns the extent of this <code>TileMap</code>
     *
     * @return a <code>BoundingBox</code> specifying the extent of this <code>TileMap</code>.
     */
    public Envelope<C2D> getBoundingBox() {
        return this.maxBoundingBox;
    }

    /**
     * Returns the <code>ImageFormat</code> in which the <code>Tile</code>s of this <code>TileMap</code>
     * are stored.
     *
     * @return the <code>ImageFormat</code> of this <code>TileMap</code>'s tiles
     */
    public ImageFormat getTileImageFormat() {
        if ("image/jpeg".equalsIgnoreCase(tileMetadata.mimeType))
            return ImageFormat.JPEG;
        else
            return ImageFormat.PNG;
    }

    private TileImageSourceFactory makeTileImageSourceFactory(String serviceUrl) {
        if (isUrl(serviceUrl)) {
            return new URLTileImageSourceFactory();
        } else {
            return new FileTileImageSourceFactory();
        }
    }

    private List<Tile> getTilesFor(TileSet set, Envelope<C2D> bbox) {
        if (outsideMaxBoundingBox(bbox))
            throw new IllegalArgumentException(String.format("Request BoundingBox: %s exceeds maximum bounding box: %s", bbox.toString(), getBoundingBox().toString()));
        List<Tile> result = new ArrayList<>();
        TileCoordinate llIdx = lowerLeftTileCoordinate(bbox, set);
        TileCoordinate urIdx = upperRightTileCoordinate(bbox, set);
        List<TileCoordinate> coordinateBlock = TileCoordinate.range(llIdx, urIdx);
        for (TileCoordinate tileCoordinate : coordinateBlock) {
            result.add(makeTile(set, tileCoordinate));
        }
        return result;
    }

    PixelRange pixelBounds(int tileSetOrder, Envelope<C2D> bbox){
        return getTileSets().get(tileSetOrder).pixelBounds(bbox);
    }

    /**
     * Determines whether the given <code>BoundingBox</code> falls outside the extent
     * of the extent of this <code>TileMap</code>.
     *
     * @param bbox the <code>BoundingBox</code> to test
     * @return true if the <code>BoundingBox</code> specified by the bbox argument falls at least partly wihtin
     * the extent of this <code>TileMap</code>, and false otherwise.
     */
    boolean outsideMaxBoundingBox(Envelope<C2D> bbox) {
        return (bbox.lowerLeft().getX() < getBoundingBox().lowerLeft().getX()
                || bbox.lowerLeft().getY() < getBoundingBox().lowerLeft().getY()
                || bbox.upperRight().getX() > getBoundingBox().upperRight().getX()
                || bbox.upperRight().getY() > getBoundingBox().upperRight().getY());
    }

    /**
     * Creates a {@link Tile} for the specified {@link TileCoordinate} in the specified {@link TileSet}.
     *
     * @param order          the <code>TileSet</code>.
     * @param tileCoordinate the <code>TileCoordinate</code>
     * @return a <code>Tile</code> in the <code>TileSet</code> specified by the set argument for the coordinate specified by tileCoordinate argument
     */
    public Tile makeTile(int order, TileCoordinate tileCoordinate) {
        return makeTile(tileSets.get(order), tileCoordinate);
    }

    private Tile makeTile(TileSet set, TileCoordinate tileCoordinate) {
        TileImageSource source = tileImageSourceFactory.create(set, tileCoordinate, tileMetadata.extension);
        return new Tile(source, tileCoordinate, set.getTileCoordinateSpace(), getTileImageFormat());
    }


    private TileCoordinate lowerLeftTileCoordinate(Envelope<C2D> bbox, TileSet set) {
        Point<C2D> ll = point(bbox.getCoordinateReferenceSystem(), bbox.lowerLeft());
        return set.pointIndex(ll, true);
    }

    private TileCoordinate upperRightTileCoordinate(Envelope<C2D> bbox, TileSet set) {
        Point<C2D> ur = point(bbox.getCoordinateReferenceSystem(), bbox.upperRight());
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
    public Envelope<C2D> clipToMaxBoundingBox(Envelope<C2D> bbox) {
        return getBoundingBox().intersect(bbox);
    }

    public boolean isForceArgb() {
        return forceArgb;
    }

    public TileSetCoordinateSpace getTileCoordinateSpace(int tileSet) {
        return getTileSets().get(tileSet).getTileCoordinateSpace();
    }

    private boolean isUrl(String maybeUrl){
        try {
            URL url = new URL(serviceUrl);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
