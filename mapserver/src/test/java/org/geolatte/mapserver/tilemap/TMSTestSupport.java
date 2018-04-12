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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TMSTestSupport {

    static public final String URL = "http://localhost/cgi-bin/tilecache.cgi/1.0.0/basic";

    public static Document makeOSMResource() {
        return makeResource("tiles/tilemapresource.xml");
    }

    public static Document makeTileCacheResource() {
        return makeResource("testTileMapMetadata.xml");
    }

    public static Document makeOrthoResource() {
        return makeResource("tiles/orthos/tilemapresource.xml");
    }

    public static Document makeResource(String fileName) {
        InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        if (inStream == null) throw new RuntimeException("Can't find xml document");
        SAXReader reader = new SAXReader();
        try {
            return reader.read(inStream);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inStream.close();
            } catch (IOException e) {
                // nothing can't done.
            }
        }
    }

    public static Set<Tile> getTestTiles() {
        Set<Tile> tiles = new HashSet<Tile>();
        TileMap tileMap = makeOSMTileMap();
        TileSet tileSet = tileMap.getTileSets().get(1);
        tiles.add(tileMap.makeTile(tileSet, TileCoordinate.valueOf(0, 0)));
        tiles.add(tileMap.makeTile(tileSet, TileCoordinate.valueOf(1, 0)));
        return tiles;
    }

    public static Set<Tile> getTestTiles(int level, int min_i, int min_j, int max_i, int max_j) {
        Set<Tile> tiles = new HashSet<Tile>();
        TileMap tileMap = makeOSMTileMap();
        TileSet tileSet = tileMap.getTileSets().get(level);
        TileCoordinate start = TileCoordinate.valueOf(min_i, min_j);
        TileCoordinate stop = TileCoordinate.valueOf(max_i, max_j);
        List<TileCoordinate> coords = TileCoordinate.range(start, stop);
        for (TileCoordinate t : coords) {
            tiles.add(tileMap.makeTile(tileSet, t));
        }
        return tiles;
    }

    public static TileMap makeOSMTileMap() {
        TileMapBuilder builder = new TileMapBuilder(TMSTestSupport.makeOSMResource());
        return builder.buildTileMap(new FileTileImageSourceFactory(), false);
    }

    public static TileMap makeOrthoTileMap() {
        TileMapBuilder builder = new TileMapBuilder(TMSTestSupport.makeOrthoResource());
        return builder.buildTileMap(new FileTileImageSourceFactory(), false);
    }


    public static Object accessField(Object object, String fieldName) {
        try {
            Field fld = object.getClass().getDeclaredField(fieldName);
            fld.setAccessible(true);
            return fld.get(object);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object runMethod(Object object, String methodName, Object... args) {
        List<Class<?>> argList = new ArrayList<Class<?>>();
        for (Object o : args) {
            argList.add(o.getClass());
        }

        try {
            Method method = object.getClass().getDeclaredMethod(methodName, argList.toArray(new Class<?>[argList.size()]));
            method.setAccessible(true);
            return method.invoke(object, args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


}
