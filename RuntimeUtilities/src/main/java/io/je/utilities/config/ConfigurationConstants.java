package io.je.utilities.config;

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
    public 	static String JAVA_GENERATION_PATH = "C:\\" ;

    // path for imported libraries
    public static String EXTERNAL_LIB_PATH = System.getenv(SIOTH_ENVIRONMENT_VARIABLE) + "\\..\\Job Engine\\libs\\";

    // path where builder loads classes
    //public static String BUILDER_CLASS_LOAD_PATH;

    // path where runner loads classes
    //public static String RUNNER_CLASS_LOAD_PATH;

    // generated bpmn path
 	public static String BPMN_PATH;

    //projects path
    public static String PROJECTS_PATH;

    public static void initConstants(String siothId, boolean isDev) {
        ConfigurationConstants.dev = isDev;
        ConfigurationConstants.SIOTHID = siothId;
        if(isDev) {
            //BUILDER_CLASS_LOAD_PATH = System.getProperty("java.class.path").split(";")[0];
            //RUNNER_CLASS_LOAD_PATH = System.getProperty("java.class.path").split(";")[0];
            PROJECTS_PATH = "D:\\JobEngine\\projects\\";
            BPMN_PATH = "D:\\JobEngine\\projects\\";
        }
        else {
            //BUILDER_CLASS_LOAD_PATH = System.getProperty("catalina.base") + "\\webapps\\ProjectBuilder\\WEB-INF\\classes\\";
            //RUNNER_CLASS_LOAD_PATH = System.getProperty("catalina.base") + "\\webapps\\RuntimeManager\\WEB-INF\\classes\\";
            BPMN_PATH = System.getenv(ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE)+ "\\JobEngine\\projects\\";
            PROJECTS_PATH = System.getenv(ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE)+ "\\JobEngine\\projects\\";
        }
    }


    public static boolean isDev() {
        return dev;
    }

    
    
    

    
}
