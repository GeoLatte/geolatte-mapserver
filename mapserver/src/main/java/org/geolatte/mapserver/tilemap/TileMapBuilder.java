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
import org.geolatte.mapserver.core.ImageFormat;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.geolatte.geom.builder.DSL.point;

public class TileMapBuilder {
    private String name;
    private CoordinateReferenceSystem<C2D> crs;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    private int tileWidth;
    private int tileHeight;
    private C2D originPos;
    private List<TileSetInfo> tileSetInfo = new ArrayList<>();
    private boolean forceArgb = false;
    private String tileMimeType = ImageFormat.PNG.getMimeType();
    private String tileExtension = ImageFormat.PNG.getExt();
    private String root;

    public TileMap build() {
        verify();
        Envelope<C2D> bbox = new Envelope<>(minX, minY, maxX, maxY, crs);
        Point<C2D> origin = point(crs, originPos);
        Dimension tileDim = getTileDimension();
        TileFormat tileFormat = new TileFormat(tileDim, tileMimeType, tileExtension);
        List<TileSet> tileSets = buildTileSets(tileDim, origin, bbox);
        return new TileMap(root,
                name,
                crs,
                bbox,
                origin,
                tileFormat,
                forceArgb,
                tileSets);
    }

    private void verify() {
        if (this.root == null) throw new IllegalStateException("No root defined for TileMap");
        if (crs == null) throw new IllegalStateException("No CRS defined for TileMap");
        if (tileSetInfo.isEmpty()) throw new IllegalStateException("No TileSets defined");
        if (tileWidth <= 0 || tileHeight <= 0) throw new IllegalStateException("Invalid Tile Dimension");
        if (originPos == null) throw new IllegalStateException("No origin defined");
    }

    private Dimension getTileDimension() {
        return new Dimension(tileWidth, tileHeight);
    }

    public TileMapBuilder root(String root) {
        if (root == null) throw new IllegalArgumentException("No null argument allowed");
        this.root = root;
        return this;
    }

    public TileMapBuilder name(String name) {
        if (name == null) throw new IllegalArgumentException("No null argument allowed");
        this.name = name;
        return this;
    }

    public TileMapBuilder crs(CoordinateReferenceSystem<C2D> ci) {
        if (ci == null) throw new IllegalArgumentException("No null argument allowed");
        this.crs = ci;
        return this;
    }

    public TileMapBuilder envelope(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        return this;
    }

    public TileMapBuilder origin(double x, double y) {
        this.originPos = new C2D(x, y);
        return this;
    }

    public TileMapBuilder tileWidth(int w) {
        this.tileWidth = w;
        return this;
    }

    public TileMapBuilder tileHeight(int h) {
        this.tileHeight = h;
        return this;
    }

    public TileMapBuilder tileMimeType(String mimeType) {
        if (mimeType == null) throw new IllegalArgumentException("No null argument allowed");
        this.tileMimeType = mimeType;
        return this;
    }

    public TileMapBuilder tileExtension(String extension) {
        if (extension == null) throw new IllegalArgumentException("No null argument allowed");
        this.tileExtension = extension;
        return this;
    }

    public TileMapBuilder forceArgb(boolean v) {
        this.forceArgb = v;
        return this;
    }

    public TileMapBuilder addSet(String url, int order, double unitsPerPixel) {
        tileSetInfo.add(new TileSetInfo(url, order, unitsPerPixel));
        return this;
    }


    private List<TileSet> buildTileSets(Dimension pixelDim, Point<C2D> origin, Envelope<C2D> bbox) {
        List<TileSet> tileSetList = new ArrayList<TileSet>();
        for (TileSetInfo ti : tileSetInfo) {
            tileSetList.add(buildTileSet(ti, origin, pixelDim, bbox));
        }
        return tileSetList;
    }

    private TileSet buildTileSet(TileSetInfo ti, Point<C2D> origin, Dimension pixelDim, Envelope<C2D> bbox) {
        TileSetCoordinateSpace cs = new TileSetCoordinateSpace(origin, pixelDim, bbox, ti.unitsPerPixel);
        return new TileSet(ti.path, ti.order, cs);
    }

    static class TileSetInfo {
        String path;
        int order;
        double unitsPerPixel;

        TileSetInfo(String path, int order, double unitsPerPixel) {
            this.path = path;
            this.order = order;
            this.unitsPerPixel = unitsPerPixel;
        }
    }


}
