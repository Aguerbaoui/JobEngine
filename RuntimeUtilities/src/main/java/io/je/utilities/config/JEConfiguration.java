package io.je.utilities.config;


import io.siothconfig.SIOTHConfigUtility;
import utils.log.LoggerUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.je.utilities.config.ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE;

/*
 * Singleton class handling JEBuilder Configuration
 */
public class JEConfiguration {

    public static final String APPLICATION_PROPERTIES_PATH = System.getenv(SIOTH_ENVIRONMENT_VARIABLE) + "/JobEngine/jobengine.properties";
    private static Properties jobEngineProperties = null;

    public static void loadProperties() {
        try (InputStream input = new FileInputStream(APPLICATION_PROPERTIES_PATH)) {
            jobEngineProperties = new Properties();
            // load a properties file
            jobEngineProperties.load(input);
            SIOTHConfigUtility.setSiothId(JEConfiguration.getSiothId());
        } catch (IOException ex) {
            LoggerUtils.logException(ex);
        }
    }

    public static String getRunnerLogPath() {
        if (jobEngineProperties == null) {
            loadProperties();
        }
        return jobEngineProperties.getProperty("jobenginerunner.log.path");
    }

    public static String getRunnerLogLevel() {
        if (jobEngineProperties == null) {
            loadProperties();
        }
        return jobEngineProperties.getProperty("jobenginerunner.log.level");
    }

    public static String getZmqSecurity() {
        if (jobEngineProperties == null) {
            loadProperties();
        }
        return jobEngineProperties.getProperty("use.ZMQ.Security");
    }

    public static String getMonitorPort() {
        if (jobEngineProperties == null) {
            loadProperties();
        }
        return jobEngineProperties.getProperty("monitoring.port");
    }

    public static String getSiothId() {
        if (jobEngineProperties == null) {
            loadProperties();
        }
        return jobEngineProperties.getProperty("sioth.id");
    }

    public static String getDevEnvironment() {
        if (jobEngineProperties == null) {
            loadProperties();
        }
        return jobEngineProperties.getProperty("dev.environment");
    }

    public static String getDumpJavaProcess() {
        if (jobEngineProperties == null) {
            loadProperties();
        }
        return jobEngineProperties.getProperty("java.processdump");
    }

    public static String getJavaDumpPath() {
        if (jobEngineProperties == null) {
            loadProperties();
        }
        return jobEngineProperties.getProperty("jobengine.processesdump.path");
    }

    public static String getMonitorLogLevel() {
        if (jobEngineProperties == null) {
            loadProperties();
        }
        return jobEngineProperties.getProperty("jobenginemonitor.log.level");
    }

    public static String getMonitorLogPath() {
        if (jobEngineProperties == null) {
            loadProperties();
        }
        return jobEngineProperties.getProperty("jobenginemonitor.log.path");
    }

    public static String getBuilderLogPath() {
        if (jobEngineProperties == null) {
            loadProperties();
        }
        return jobEngineProperties.getProperty("jobenginebuilder.log.path");
    }

    public static String getBuilderLogLevel() {
        if (jobEngineProperties == null) {
            loadProperties();
        }
        return jobEngineProperties.getProperty("jobenginebuilder.log.level");
    }

    public static String getIdentityUrl() {
        if (jobEngineProperties == null) {
            loadProperties();
        }
        return jobEngineProperties.getProperty("ids4.issuer");
    }


    public static Properties getJobEngineProperties() {
        if (jobEngineProperties == null) loadProperties();
        return jobEngineProperties;
    }

}


