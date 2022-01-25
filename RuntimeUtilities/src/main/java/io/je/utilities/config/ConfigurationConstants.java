package io.je.utilities.config;

import org.apache.commons.io.FilenameUtils;
import utils.files.FileUtilities;

import static io.je.utilities.constants.ClassBuilderConfig.CLASS_PACKAGE;

public class ConfigurationConstants {

    /*
     * SIOTH Config
     */

    public static final String SIOTH_ENVIRONMENT_VARIABLE = "SIOTHJobEngine";

    public static final String APPLICATION_PROPERTIES_PATH = "file:${"+SIOTH_ENVIRONMENT_VARIABLE+"}/JobEngine/jobengine.properties";

    public static String SIOTHID ="";

    public static boolean dev = false;

    public static final String DROOLS_DATE_FORMAT = "MM/dd/yyyy HH:mm:ss.SSS";

	 //path where .java files are generated :
    public 	static String JAVA_GENERATION_PATH = "D:\\jobengine\\" ;

    // path for imported libraries
    public static String EXTERNAL_LIB_PATH = System.getenv(SIOTH_ENVIRONMENT_VARIABLE) + "\\..\\Job Engine\\libs\\";

    // generated bpmn path
 	public static String BPMN_PATH;

    //projects path
    public static String PROJECTS_PATH;

    public static void initConstants(String siothId, boolean isDev) {
        ConfigurationConstants.dev = isDev;
        ConfigurationConstants.SIOTHID = siothId;
        if(isDev) {
            PROJECTS_PATH = "D:\\JobEngine\\projects\\";
            BPMN_PATH = "D:\\JobEngine\\projects\\";
        }
        else {
            BPMN_PATH = System.getenv(ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE)+ "\\JobEngine\\projects\\";
            PROJECTS_PATH = System.getenv(ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE)+ "\\JobEngine\\projects\\";
        }
    }

    public static boolean isDev() {
        return dev;
    }

    public static String getJobEngineCustomImport() {
        String imp = ConfigurationConstants.JAVA_GENERATION_PATH.replace(FileUtilities.getPathPrefix(ConfigurationConstants.JAVA_GENERATION_PATH), "");
        imp = imp.replace("\\", ".");
        imp =  imp + "." + CLASS_PACKAGE;
        return imp.replace("..", ".") + ".*";
    }
    

    
}
