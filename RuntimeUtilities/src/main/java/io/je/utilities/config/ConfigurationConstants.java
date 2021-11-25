package io.je.utilities.config;

public class ConfigurationConstants {

    /*
     * SIOTH Config
     */



    public static final String SIOTH_ENVIRONMENT_VARIABLE = "SIOTHJobEngine";

    public static final String APPLICATION_PROPERTIES_PATH = "file:${"+SIOTH_ENVIRONMENT_VARIABLE+"}/JobEngine/jobengine.properties";

    public static final String SIOTH_JSON_CONFIG = System.getenv(ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE) + "\\SIOTHConfig.json";

    /*
     * Config for testing on the IDE
     */
   /*
	 //path where .java files are generated : 
    public 	static String JAVA_GENERATION_PATH = "D:\\myproject2" ;
	
    // path where builder loads classes 
    public static String BUILDER_CLASS_LOAD_PATH = System.getProperty("java.class.path").split(";")[0];
    
    // path where runner loads classes
    public static String RUNNER_CLASS_LOAD_PATH = System.getProperty("java.class.path").split(";")[0];
    
    public static final String PROJECTS_PATH = "D:\\JobEngine\\projects\\";
   
    public static final String BPMN_PATH = "D:\\JobEngine\\projects\\";

    // path for imported libraries
    public static String EXTERNAL_LIB_PATH =  "D:\\myproject2\\";*/
    
    /*
     * Config for tomcat
     */
    
   //path where .java files are generated :
    public static String JAVA_GENERATION_PATH = System.getProperty("catalina.base") + "\\webapps\\ProjectBuilder\\WEB-INF\\classes\\io\\je\\";

    // path for imported libraries
    public static String EXTERNAL_LIB_PATH = System.getProperty("catalina.base") + "\\webapps\\ProjectBuilder\\WEB-INF\\libraries\\";

    // path where builder loads classes
    public static String BUILDER_CLASS_LOAD_PATH = System.getProperty("catalina.base") + "\\webapps\\ProjectBuilder\\WEB-INF\\classes\\";

    // path where runner loads classes
    public static String RUNNER_CLASS_LOAD_PATH = System.getProperty("catalina.base") + "\\webapps\\RuntimeManager\\WEB-INF\\classes\\";

 	public static final String BPMN_PATH = System.getenv(ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE)+ "\\JobEngine\\projects\\";

    public static final String PROJECTS_PATH = System.getenv(ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE)+ "\\JobEngine\\projects\\";


 
 

    
    
    

    
}
