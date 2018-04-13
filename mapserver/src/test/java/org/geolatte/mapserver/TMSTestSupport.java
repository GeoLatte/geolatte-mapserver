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

package org.geolatte.mapserver;

import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.geolatte.geom.crs.CrsRegistry;
import org.geolatte.geom.crs.ProjectedCoordinateReferenceSystem;
import org.geolatte.mapserver.tilemap.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TMSTestSupport {

    public final static ProjectedCoordinateReferenceSystem WMERC = CoordinateReferenceSystems.WEB_MERCATOR;
    public final static ProjectedCoordinateReferenceSystem L72 = CrsRegistry.getProjectedCoordinateReferenceSystemForEPSG(31370);


    static public final String URL = "http://localhost/cgi-bin/tilecache.cgi/1.0.0/basic";


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
        TileMapBuilder builder = new TileMapBuilder();
        builder
                .name("osm")
                .root("src/test/resources/tiles/")
                .crs(WMERC)
                .envelope(-20037508.340000, -20037508.340000, 20037508.340000, 20037508.340000)
                .origin(-20037508.340000, -20037508.340000)
                .tileWidth(256).tileHeight(256)
                .addSet("src/test/resources/tiles/osm/0", 0, 156543.03390000000945292413)
                .addSet("src/test/resources/tiles/osm/1", 1, 78271.51695000000472646207)
                .addSet("src/test/resources/tiles/osm/2", 2, 39135.75847500000236323103)
                .addSet("src/test/resources/tiles/osm/3", 3, 19567.87923750000118161552);
        return builder.build();
    }

    public static TileMap makeOrthoTileMap() {
        TileMapBuilder builder = new TileMapBuilder();
        builder.name("tms-vlaanderen")
                .crs(L72)
                .envelope(1000, 152999.75, 259500.250, 244500.0)
                .origin(18000.0, 152999.75)
                .tileHeight(256).tileWidth(256)
                .tileMimeType("image/jpeg")
                .tileExtension("jpg")
                .addSet("/tmp/tiles/orthos/0", 0, 1024.00000000000000)
                .addSet("/tmp/tiles/orthos/1", 1, 512.00000000000000)
                .addSet("/tmp/tiles/orthos/2", 2, 256.00000000000000);
        return builder.build();
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
