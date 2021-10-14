package io.je.utilities.config;

public class ConfigurationConstants {
 
    /*
     * Config for testing on the IDE
     */
    
	 //path where .java files are generated : 
    public 	static String JAVA_GENERATION_PATH = "D:\\myproject2" ;
	
    // path where builder loads classes 
    public static String BUILDER_CLASS_LOAD_PATH = System.getProperty("java.class.path").split(";")[0];
    
    // path where runner loads classes
    public static String RUNNER_CLASS_LOAD_PATH = System.getProperty("java.class.path").split(";")[0];
    
    public static final String PROJECTS_PATH = "D:\\JobEngine\\projects\\";
   
    public static final String BPMN_PATH = "D:\\JobEngine\\projects\\";

    // path for imported libraries
    public static String EXTERNAL_LIB_PATH =  "D:\\myproject2\\";
    
    /*
     * Config for tomcat
     */
    
  /* //path where .java files are generated :
    public static String classGenerationPath = System.getProperty("catalina.base") + "\\webapps\\ProjectBuilder\\WEB-INF\\classes\\io\\je\\";

    // path for imported libraries
    public static String EXTERNAL_LIB_PATH = System.getProperty("catalina.base") + "\\webapps\\ProjectBuilder\\WEB-INF\\libraries\\";

    // path where builder loads classes
    public static String builderClassLoadPath = System.getProperty("catalina.base") + "\\webapps\\ProjectBuilder\\WEB-INF\\classes\\";

    // path where runner loads classes
    public static String runnerClassLoadPath = System.getProperty("catalina.base") + "\\webapps\\RuntimeManager\\WEB-INF\\classes\\";
 
 	public static final String BPMN_PATH = System.getenv(ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE)+ "\\JobEngine\\";
    public static final String PROJECTS_PATH = System.getenv(ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE)+ "\\JobEngine\\projects\\";
	
*/
 
 
    /*
     * SIOTH Config
     */

 

   
    
    
    

    
}
