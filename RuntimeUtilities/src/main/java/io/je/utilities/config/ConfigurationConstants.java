package io.je.utilities.config;

import utils.string.StringUtilities;

import static io.je.utilities.constants.ClassBuilderConfig.CLASS_PACKAGE;

public class ConfigurationConstants {

    /*
     * SIOTH Config
     */

    public static final String SIOTH_ENVIRONMENT_VARIABLE = "SIOTHJobEngine";

    // FIXME remove if not needed
    //public static final String APPLICATION_PROPERTIES_PATH = "file:${" + SIOTH_ENVIRONMENT_VARIABLE + "}/JobEngine/jobengine.properties";
    public static final String DROOLS_DATE_FORMAT = "MM/dd/yyyy HH:mm:ss.SSS";
    public static String SIOTHID = "";
    public static boolean dev = false;
    //path where .java files are generated :
    public static String JAVA_GENERATION_PATH = System.getenv(SIOTH_ENVIRONMENT_VARIABLE) + "\\..\\Job Engine\\JEFiles\\";

    // path for imported libraries
    public static String EXTERNAL_LIB_PATH = System.getenv(SIOTH_ENVIRONMENT_VARIABLE) + "\\..\\Job Engine\\libs\\";

    // generated bpmn path
    public static String BPMN_PATH;

    //projects path
    public static String PROJECTS_PATH;

    public static void initConstants(String siothId, boolean isDev) {
        ConfigurationConstants.dev = isDev;
        ConfigurationConstants.SIOTHID = siothId;
        if (isDev) {
            PROJECTS_PATH = "C:\\JobEngine\\projects\\";
            BPMN_PATH = "C:\\JobEngine\\projects\\";
        } else {
            BPMN_PATH = System.getenv(ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE) + "\\JobEngine\\projects\\";
            PROJECTS_PATH = System.getenv(ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE) + "\\JobEngine\\projects\\";
        }
    }

    public static boolean isDev() {
        return dev;
    }

    public static void setDev(boolean dev) {
        ConfigurationConstants.dev = dev;
    }

    public static String getJobEngineCustomImport() {
        String imp = ConfigurationConstants.JAVA_GENERATION_PATH.replace(ConfigurationConstants.JAVA_GENERATION_PATH, "");
        imp = imp.replace("\\", ".");
        imp = imp.replace("//", ".");
        imp = imp.replace("/", ".");
        if (StringUtilities.isEmpty(imp)) {
            imp = CLASS_PACKAGE;
        } else {
            imp = imp + "." + CLASS_PACKAGE;
        }
        return "jeclasses" + ".*";
    }

    public static String getJavaGenerationPath() {
        return JAVA_GENERATION_PATH;
    }


}
