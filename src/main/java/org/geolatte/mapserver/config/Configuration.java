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

import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.currentThread;

/**
 * The configuration information.
 * <p/>
 * <p>By default the <code>Configuration</code> object is created either:
 * <ul>
 * <li>from the file "mapserver-config.xml" on the classpath, or</li>
 * <li>from the file mentioned in the Java system-property "mapserver-configuration".</li>
 * </ul>
 */
public class Configuration {

    private final static String TYPE = "type";

    private final static String PATH = "path";

    private final static String SOURCE_FACTORY = "TileImageSourceFactory";
    private static final String DEFAULT_CONFIG_FILENAME
            = "mapserver-config.xml";
    private static final String CONFIG_PATH_PROPERTY_NAME = "mapserver-configuration";


    private final Document configDoc;

    /**
     * the type of a resource. Either <code>URL</code> or
     * <code>FILE</code>.
     */
    public enum RESOURCE_TYPE {
        URL,
        FILE;
    }

    private static Configuration buildConfiguration(InputStream is) throws ConfigurationException {
        assert is != null : "buildConfiguration() method should not be invoked with null argument";
        try {
            SAXReader reader = new SAXReader();
            Document configDoc = reader.read(is);
            return new Configuration(configDoc);
        } catch (DocumentException e) {
            throw new ConfigurationException("Building configuration throw Exception", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // nothing to do
            }
        }
    }

    /**
     * Loads configuration from file on the path specified in
     * the system Property "mapserver-configuration". If no such property
     * exists, a file with name "mapserver-config.xml" is
     * searched on the classpath.
     *
     * @return {@link Configuration} object
     */
    public static Configuration load() throws ConfigurationException {
        String path = System.getProperty(CONFIG_PATH_PROPERTY_NAME);
        if (path == null) {
            return load(DEFAULT_CONFIG_FILENAME);
        }
        File configFile = new File(path);
        try {
            InputStream is = new FileInputStream(configFile);
            return buildConfiguration(is);
        } catch (FileNotFoundException e) {
            throw new ConfigurationException(String.format("Configuration file %s not found. ", configFile));
        }
    }


    /**
     * Loads configuration from the specified file name
     *
     * @param filename the configuration file
     * @return {@link Configuration} object
     * @throws ConfigurationException
     */
    public static Configuration load(String filename) throws ConfigurationException {
        InputStream is = currentThread().getContextClassLoader().getResourceAsStream(filename);
        if (is == null)
            throw new ConfigurationException(String.format("Configuration file %s not found on the classpath", filename));
        return buildConfiguration(is);
    }


    private Configuration(Document xml) {
        this.configDoc = xml;
    }

    /**
     * Lists the titles of all <code>TileMap</code>s in the
     * configuration file.
     *
     * @return list of <code>TileMap</code> titles.
     */
    public List<String> getTileMaps() {
        List titles = configDoc.selectNodes("//TileMap/@title");
        List<String> result = new ArrayList<String>();
        for (Object o : titles) {
            Attribute title = (Attribute) o;
            result.add(title.getValue());
        }
        return result;
    }

    /**
     * Returns the {@linkplain RESOURCE_TYPE} of the <code>TileMap</code> with the
     * given title
     *
     * @param tileMap Title of the <code>TileMap</code>
     * @return
     * @throws ConfigurationException
     */
    public RESOURCE_TYPE getType(String tileMap) throws ConfigurationException {
        String typeval = getAttribute(tileMap, TYPE);
        String typeStr = typeval.toUpperCase();
        try {
            return RESOURCE_TYPE.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException("TileMap type " + typeStr + " not recognized for TileMap " + tileMap);
        }
    }

    /**
     * Returns the path to the <code>TileMap</code> resource XML representation,
     * or null if this is not configured.
     *
     * @param tileMap title of the <code>TileMap</code>
     * @return the path to the resource representation
     * @throws ConfigurationException
     */
    public String getPath(String tileMap) throws ConfigurationException {
        return getAttribute(tileMap, PATH);
    }

    /**
     * Returns the <code>TileImageSourceFactory</code> to be
     * used for this </code>TileMap</code>.
     *
     * @param tileMapName
     * @return the fully-qualified class name of the </code>TileImageSourceFactory</code>
     * @throws ConfigurationException
     */
    public String getTileImageSourceFactoryClass(String tileMapName) throws ConfigurationException {
        return getAttribute(tileMapName, SOURCE_FACTORY);
    }

    /**
     * Returns the title to be used in the Capabilities
     * document for this WMS Service.
     *
     * @return WMS Title String
     */
    public String getWMSServiceTitle() {
        Element service = (Element) configDoc.selectSingleNode("//WMS/Service/Title");
        if (service == null) return "";
        return service.getText();
    }

    /**
     * Returns the abstract to be used in the Capabilities
     * document for this WMS Service.
     *
     * @return WMS abstract text
     */
    public String getWMSServiceAbstract() {
        Element serviceAbstract = (Element) configDoc.selectSingleNode("//WMS/Service/Abstract");
        if (serviceAbstract == null) return "";
        return serviceAbstract.getText();
    }

    /**
     * Returns the keywords  to be used in the Capabilities
     * document for this WMS Service.
     *
     * @return Array of keywords
     */
    public String[] getWMSServiceKeywords() {
        Element serviceKeywordList = (Element) configDoc.selectSingleNode("//WMS/Service/KeywordList");
        if (serviceKeywordList == null) return new String[0];
        List<Element> keywordNodes = serviceKeywordList.selectNodes("Keyword");
        String[] keywords = new String[keywordNodes.size()];
        int i = 0;
        for (Node keywordNode : keywordNodes) {
            keywords[i++] = ((Element) keywordNode).getText();
        }
        return keywords;
    }

    /**
     * Returns the URL to be used in the Capabilities
     * document for this WMS Service.
     *
     * @return
     */
    public String getWMSServiceOnlineResource() {
        Element onlineResource = (Element) configDoc.selectSingleNode("//WMS/Service/OnlineResource");
        if (onlineResource == null) throw new IllegalStateException("WMS/Service Online Resource required");
        Attribute link = (Attribute) onlineResource.selectSingleNode("@xlink:href");
        if (link == null) return "";
        return link.getText();
    }


    private String getAttribute(String tileMapName, String attribute) throws ConfigurationException {
        Attribute attr = (Attribute) configDoc.selectSingleNode("//TileMap[@title='" + tileMapName + "']/@" + attribute);
        if (attr == null) {
            throw new ConfigurationException(String.format("Configuration fo TileMap \"%s\" has no %s attribute.", tileMapName, attribute));
        }
        return attr.getValue();
    }

}
