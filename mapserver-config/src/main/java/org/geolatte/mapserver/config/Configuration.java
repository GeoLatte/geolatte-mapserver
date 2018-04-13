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

package org.geolatte.mapserver.config;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


/**
 * The configuration information.
 * <p/>
 * <p>By default the {@code Configuration} object is created either:
 * <ul>
 * <li>from the file "mapserver-config.xml" on the classpath, or</li>
 * <li>from the file mentioned in the Java system-property "mapserver-configuration".</li>
 * </ul>
 */
public class Configuration {


    private List<TileMapConfig> tileMaps;


    //required for Jsonb
    public Configuration(){}

    /**
     * Lists the names of all tilemaps in the configuration
     *
     * @return list of {@code TileMap} titles.
     */
    public List<TileMapConfig> getTileMaps() {
        return tileMaps;
    }

    public void setTileMaps(List<TileMapConfig> tileMaps) {
        this.tileMaps = tileMaps;
    }

    public Optional<TileMapConfig> getTileMapConfig(String tileMapName) {
        if(tileMapName == null) throw new NoSuchElementException("No tilemap with name " + tileMapName + " configured");
        for(TileMapConfig tmc : tileMaps){
            if(tileMapName.equalsIgnoreCase(tmc.getName())) return Optional.of(tmc);
        }
        return Optional.empty();
    }

    //TODO the commented-out code needs to find a place for capabilities
//    /**
//     * Returns the title to be used in the Capabilities document for this WMS Service.
//     *
//     * @return WMS Title String
//     */
//    public String getWMSServiceTitle() {
//        Element service = (Element) configDoc.selectSingleNode("//WMS/Service/Title");
//        if (service == null) return "";
//        return service.getText();
//    }
//
//    /**
//     * Returns the abstract to be used in the Capabilities document for this WMS Service.
//     *
//     * @return WMS abstract text
//     */
//    public String getWMSServiceAbstract() {
//        Element serviceAbstract = (Element) configDoc.selectSingleNode("//WMS/Service/Abstract");
//        if (serviceAbstract == null) return "";
//        return serviceAbstract.getText();
//    }
//
//    /**
//     * Returns the keywords  to be used in the Capabilities document for this WMS Service.
//     *
//     * @return Array of keywords
//     */
//    public String[] getWMSServiceKeywords() {
//        Element serviceKeywordList = (Element) configDoc.selectSingleNode("//WMS/Service/KeywordList");
//        if (serviceKeywordList == null) return new String[0];
//        List<Element> keywordNodes = serviceKeywordList.selectNodes("Keyword");
//        String[] keywords = new String[keywordNodes.size()];
//        int i = 0;
//        for (Node keywordNode : keywordNodes) {
//            keywords[i++] = ((Element) keywordNode).getText();
//        }
//        return keywords;
//    }
//
//    /**
//     * Returns the URL to be used in the Capabilities document for this WMS Service.
//     *
//     * @return URL of the WMS capabilities document.
//     */
//    public String getWMSServiceOnlineResource() {
//        Element onlineResource = (Element) configDoc.selectSingleNode("//WMS/Service/OnlineResource");
//        if (onlineResource == null) throw new IllegalStateException("WMS/Service Online Resource required");
//        Attribute link = (Attribute) onlineResource.selectSingleNode("@xlink:href");
//        if (link == null) return "";
//        return link.getText();
//    }
//
//    private String getAttribute(String tileMapName, String attribute) throws ConfigurationException {
//        Attribute attr = (Attribute) configDoc.selectSingleNode("//TileMap[@title='" + tileMapName + "']/@" + attribute);
//        if (attr == null) {
//            throw new ConfigurationException(String.format("Configuration for TileMap \"%s\" has no %s attribute.", tileMapName, attribute));
//        }
//        return attr.getValue();
//    }

}
