package io.je.utilities.config;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.je.utilities.config.ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE;

/*
 * Singleton class handling JEBuilder Configuration
 */
public class JEConfiguration {

    private static Properties jobEngineProperties = null;

    public static final String APPLICATION_PROPERTIES_PATH = System.getenv(SIOTH_ENVIRONMENT_VARIABLE) + "/JobEngine/jobengine.properties";

    public static void loadProperties() {
        try (InputStream input = new FileInputStream(APPLICATION_PROPERTIES_PATH)) {
            jobEngineProperties = new Properties();
            // load a properties file
            jobEngineProperties.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getProjectBuilderUrl() {
        if(jobEngineProperties == null) {
            loadProperties();
        }
        return jobEngineProperties.getProperty("jobenginebuilder.url");
    }

}


