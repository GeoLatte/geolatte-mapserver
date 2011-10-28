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
import org.geolatte.geom.crs.CrsId;
import org.geolatte.mapserver.config.Configuration;
import org.geolatte.mapserver.config.ConfigurationException;

import java.util.*;

public class TileMapRegistry {

    private final static Logger LOGGER = Logger.getLogger(TileMapRegistry.class);

    private final Map<String, TileMap> tileMaps;

    private final Map<String, List<CrsId>> supportedSRSMap;

    public static TileMapRegistry configure(Configuration config) {
        Map<String, TileMap> map = new HashMap<String, TileMap>();
        Map<String, List<CrsId>> srsMap = new HashMap<String, List<CrsId>>();
        for (String tilemap : config.getTileMaps()) {
            addTilemap(map, srsMap, tilemap, config);

        }
        return new TileMapRegistry(map, srsMap);
    }

    private TileMapRegistry(Map<String, TileMap> map, Map<String,List<CrsId>> srsMap) {
        this.tileMaps = Collections.unmodifiableMap(map);
        this.supportedSRSMap = Collections.unmodifiableMap(srsMap);
    }

    private static void addTilemap(Map<String, TileMap> map, Map<String, List<CrsId>> sRSMap, String tileMapName, Configuration config) {
         try {
             TileMap tilemap = createTileMap(tileMapName, config);
             List<CrsId> supportedSRSList = createSRSList(tileMapName, config);
             map.put(tilemap.getTitle(), tilemap);
             if (!supportedSRSList.isEmpty()) {
                 sRSMap.put(tilemap.getTitle(), supportedSRSList);
             }
         } catch (TileMapCreationException e) {
             LOGGER.warn(String.format("Failed to instantiate TileMap \"%s\": %s", tileMapName, e.getMessage()));
         }
     }

     private static List<CrsId> createSRSList(String tileMapName, Configuration config) {
         List<CrsId> result = new ArrayList<CrsId>();
         try {
             String[] srsNames = config.getSupportedSRS(tileMapName);
             addSRS(tileMapName, result, srsNames);
         } catch (ConfigurationException e) {
             LOGGER.warn(String.format("Failed to register additional SRS's for TileMap \"%s\": %s", tileMapName, e.getMessage()));
         }
         return result;
     }

     private static void addSRS(String tileMapName, List<CrsId> result, String[] srsNames) {
         for (String srs : srsNames) {
             try{
                 result.add(CrsId.parse(srs));
             }catch(IllegalArgumentException e) {
                 LOGGER.warn(String.format("Failed to parse SRS string %s during configuring tilemap \"%s \": %s", srs, tileMapName, e.getMessage()));
             }
         }
     }


    private static TileMap createTileMap(String tileMapName, Configuration config) throws TileMapCreationException {
        String sourceFactoryName = null;
        try {
            sourceFactoryName = config.getTileImageSourceFactoryClass(tileMapName);
            String path = config.getPath(tileMapName);
            Configuration.RESOURCE_TYPE type = config.getType(tileMapName);
            return createTileMap(sourceFactoryName, path, type);
        } catch (ConfigurationException e) {
            throw new TileMapCreationException("Cannot create tilemap: " + tileMapName, e);
        }
    }

    private static TileMap createTileMap(String sourceFactoryName, String path, Configuration.RESOURCE_TYPE type) throws TileMapCreationException {
        try {
            Class factClass = Class.forName(sourceFactoryName);
            TileImageSourceFactory factory = (TileImageSourceFactory) factClass.newInstance();
            TileMapBuilder builder = null;
            switch (type) {
                case FILE:
                    builder = TileMapBuilder.fromPath(path);
                    break;
                case URL:
                    builder = TileMapBuilder.fromURL(path);
                    break;
                default:
                    throw new IllegalStateException();
            }
            return builder.buildTileMap(factory);
        } catch (ClassNotFoundException e) {
            throw new TileMapCreationException(String.format("Can't locate source factory %s.", sourceFactoryName), e);
        } catch (IllegalAccessException e) {
            throw new TileMapCreationException(String.format("Can't instantiate source factory %s.", sourceFactoryName), e);
        } catch (InstantiationException e) {
            throw new TileMapCreationException(String.format("Can't instantiate source factory %s.", sourceFactoryName), e);
        } catch (ClassCastException e) {
            throw new TileMapCreationException(String.format("Configured source factory %s. is not a TileImageSourceFactory implementation", sourceFactoryName), e);
        }
    }

    public List<String> getTileMapNames() {
        Set<String> set = tileMaps.keySet();
        List<String> result = new ArrayList<String>();
        result.addAll(set);
        return result;
    }

    public TileMap getTileMap(String tileMapName) {
        return this.tileMaps.get(tileMapName);
    }



    /**
     * Lists the SRS to which the images of the specified <code>TileMap</code> can be projected.
     *
     * @param tileMapName the <code>TileMap</code> name
     * @return a list of <code>SRS</code>s to which projection is supported.
     */
    //TODO Does this need to move to TileMap?
    public List<CrsId> getSupportedSRS(String tileMapName) {
        List<CrsId> result = supportedSRSMap.get(tileMapName);
        if (result == null) {
            return new ArrayList();
        } else {
            return result;
        }
    }

    /**
     *  Checks whether the <code>TileMap</code> can be
     *  projected to the target <code>SRS</code> are supported
     *
     * @param tileMapName the <code>TileMap</code> name
     * @param srs the target <code>SRS</code>
     * @return returns true if the <code>TileMap</code> supports the <code>SRS</code>.
     */
    public boolean supportsSRS(String tileMapName, CrsId srs) {
        if (getTileMap(tileMapName).getSRS().equals(srs)) {
            return true;
        }
        List<CrsId> supported = supportedSRSMap.get(tileMapName);
        return supported == null ?
                false :
                supported.contains(srs);
    }
}
