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

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Point;
import org.geolatte.geom.crs.CrsId;


import java.awt.Dimension;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TileMapBuilder {

    private final Document xmlDoc;

    public TileMapBuilder(Document xmlDoc) {
        this.xmlDoc = xmlDoc;
    }

    public static TileMapBuilder fromURL(String urlStr) throws TileMapCreationException {
        try {
            URL url = new URL(urlStr);
            SAXReader reader = new SAXReader();
            Document xmlDoc = reader.read(url);
            return new TileMapBuilder(xmlDoc);
        } catch (MalformedURLException e) {
            throw new TileMapCreationException(String.format("Tilemap resource configured with malformed url"), e);
        } catch (DocumentException e) {
            throw new TileMapCreationException(String.format("Can't read Tilemap resource from URL"), e);
        }
    }

    public static TileMapBuilder fromPath(String path) throws TileMapCreationException {
        File file = new File(path);
        SAXReader reader = new SAXReader();
        try {
            Document xmlDoc = reader.read(file);
            return new TileMapBuilder(xmlDoc);
        } catch (DocumentException e) {
            throw new TileMapCreationException("Can't read tilemap resource from href.", e);
        }
    }


    public TileMap buildTileMap() {
        return new TileMap(this.getMapServiceUrl(),
                this.getTitle(),
                this.getSRS(),
                this.getBoundingBox(),
                this.getOrigin(),
                this.getTileFormat(),
                this.getTileSets());
    }

    public TileMap buildTileMap(TileImageSourceFactory factory) {
        TileMap tileMap = buildTileMap();
        tileMap.setTileImageSourceFactory(factory);
        return tileMap;
    }

    private String extractNode(String node) {
        return xmlDoc.selectSingleNode(node).getText();
    }

    private Double extractAttributeDouble(String xpath) {
        String str = extractAttribute(xpath);
        return Double.valueOf(str);
    }

    private Integer extractAttributeInteger(String xpath) {
        String str = extractAttribute(xpath);
        return Integer.valueOf(str);
    }

    private String extractAttribute(String xpath) {
        Attribute attr = (Attribute) xmlDoc.selectSingleNode(xpath);
        String str = attr.getValue();
        return str;
    }

    protected String getMapServiceUrl() {
        return extractAttribute("//TileMap/@tilemapservice");
    }

    protected String getTitle() {
        return extractNode("//Title");
    }

    protected CrsId getSRS() {
        String valueStr = extractNode("//SRS");
        return CrsId.parse(valueStr);
    }

    protected Envelope getBoundingBox() {
        double minX = extractAttributeDouble("//BoundingBox/@minx");
        double minY = extractAttributeDouble("//BoundingBox/@miny");
        double maxX = extractAttributeDouble("//BoundingBox/@maxx");
        double maxY = extractAttributeDouble("//BoundingBox/@maxy");
        return new Envelope(minX, minY, maxX, maxY, getSRS());
    }


    protected Point getOrigin() {
        double originX = extractAttributeDouble("//Origin/@x");
        double originY = extractAttributeDouble("//Origin/@y");
        return Point.create2D(originX, originY, CrsId.UNDEFINED.getCode());
    }

    protected Dimension getTileDimension() {
        int h = extractAttributeInteger("//TileFormat/@height");
        int w = extractAttributeInteger("//TileFormat/@width");
        return new Dimension(w, h);
    }

    protected TileFormat getTileFormat() {
        Dimension dim = getTileDimension();
        String tileMimeType = extractAttribute("//TileFormat/@mime-type");
        String tileExtension = extractAttribute("//TileFormat/@extension");
        return new TileFormat(dim, tileMimeType, tileExtension);
    }

    protected List<TileSet> getTileSets() {
        List<TileSet> tileSetList = new ArrayList<TileSet>();
        Dimension pixelDim = getTileDimension();
        Point origin = getOrigin();
        Envelope bbox = getBoundingBox();
        Element tilesetsNode = (Element) xmlDoc.selectSingleNode("//TileSets");
        for (Iterator it = tilesetsNode.elementIterator("TileSet"); it.hasNext();) {
            Element tileSetEl = (Element) it.next();
            tileSetList.add(getTileSet(tileSetEl, origin, pixelDim, bbox));
        }
        return tileSetList;
    }

    private TileSet getTileSet(Element xmlEl, Point origin, Dimension pixelDim, Envelope bbox) {
        String url = xmlEl.valueOf("@href");
        String orderStr = xmlEl.valueOf("@order");
        int order = Integer.valueOf(orderStr);
        String uppStr = xmlEl.valueOf("@units-per-pixel");
        double upp = Double.valueOf(uppStr);
        TileSetCoordinateSpace cs = new TileSetCoordinateSpace(origin, pixelDim, bbox, upp);
        return new TileSet(url, order, cs);
    }

    protected Document getMetadataAsXML() {
        return xmlDoc;
    }
}
