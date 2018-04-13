package org.geolatte.mapserver.config;

import org.apache.johnzon.jsonb.JohnzonJsonb;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.io.*;

import static java.lang.Thread.currentThread;

/**
 * Loads the static, immutable Configuration from a file
 *
 * Created by Karel Maesen, Geovise BVBA on 12/04/2018.
 */
public class StaticConfigurationLoader {

    public static final String CONFIG_PATH_PROPERTY_NAME = "mapserver-configuration";
    private static final String DEFAULT_CONFIG_FILENAME = "mapserver-config.json";


    /**
     * Loads configuration from file on the path specified in the system Property "mapserver-configuration". If no such
     * property exists, a file with name "mapserver-config.xml" is searched on the classpath.
     *
     * @return {@link Configuration} object
     */
    public static Configuration load() throws ConfigurationException {
        String path = System.getProperty(CONFIG_PATH_PROPERTY_NAME);
        if (path == null) {
            return load(DEFAULT_CONFIG_FILENAME);
        }
        File configFile = new File(path);
        try (InputStream is = new FileInputStream(configFile)){
            return buildConfiguration(is);
        } catch (IOException e) {
            throw new ConfigurationException(String.format("Configuration file %s not found. ", configFile));
        }
    }


    /**
     * Loads configuration from the specified file name.
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

    private static Configuration buildConfiguration(InputStream is) throws ConfigurationException {
        assert is != null : "buildConfiguration() method should not be invoked with null argument";
        try (Jsonb jsonb = JsonbBuilder.create(new JsonbConfig()) ) {
            return jsonb.fromJson(is, Configuration.class);
        } catch (Exception e) {
            throw new ConfigurationException("Building configuration throw Exception", e);
        }
    }



}
