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

import org.apache.log4j.Logger;
import org.geolatte.geom.Envelope;

import java.awt.*;
import java.util.List;

public class TileSetChooser {

    private final static Logger LOGGER = Logger.getLogger(TileSetChooser.class);


    final private TileMap tileMap;
    final private Envelope bbox;
    final private Dimension imageDimension;
    final private double requestUnitsPerPixel;

    public TileSetChooser(TileMap tileMap, Envelope bbox, Dimension imageDimension) {
        this.tileMap = tileMap;
        this.bbox = bbox;
        this.imageDimension = imageDimension;
        requestUnitsPerPixel = determineRequestUnitsPerPixel();
        LOGGER.debug("Request units per pixel is: " + requestUnitsPerPixel);
    }


    TileSet chooseTileSet() {
        List<TileSet> tileSets = tileMap.getTileSets();
        if (tileSets.isEmpty()) throw new RuntimeException("No tilesets.");
        TileSet candidate = tileSets.get(0);
        for (TileSet tileSet : tileSets) {
            candidate = chooseCandidate(candidate, tileSet);
        }
        return candidate;
    }


    protected TileSet chooseCandidate(TileSet candidate, TileSet tileSet) {
        return (Math.abs(candidate.unitsPerPixel() - requestUnitsPerPixel) >
                Math.abs(tileSet.unitsPerPixel() - requestUnitsPerPixel))
                ? tileSet : candidate;
    }

    protected double determineRequestUnitsPerPixel() {

        double boxH = bbox.getHeight();
        double boxW = bbox.getWidth();
        double yRes = boxH / imageDimension.getHeight();
        double xRes = boxW / imageDimension.getWidth();
        //The WMS spec specifies that the resulting image
        // should be stretched to fill the aspect ratio of
        // the BBox. So return the max of yRes and xRes
        return Math.max(xRes, yRes);
    }
}