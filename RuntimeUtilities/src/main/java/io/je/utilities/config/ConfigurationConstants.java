package io.je.utilities.config;

public class ConfigurationConstants {
 
    /*
     * Config for testing on the IDE
     */
    
	 //path where .java files are generated : 
 /*   public 	static String  classGenerationPath	= "D:\\myproject2" ;
	
    // path where builder loads classes 
    public static String builderClassLoadPath = System.getProperty("java.class.path").split(";")[0]; 
    
    // path where runner loads classes
    public static String runnerClassLoadPath = System.getProperty("java.class.path").split(";")[0]; 
    

    */
	
    
    /*
     * Config for tomcat
     */
    
    //path where .java files are generated :
    public static String classGenerationPath = System.getProperty("catalina.base") + "\\webapps\\ProjectBuilder\\WEB-INF\\classes\\io\\je\\";

    // path where builder loads classes
    public static String builderClassLoadPath = System.getProperty("catalina.base") + "\\webapps\\ProjectBuilder\\WEB-INF\\classes\\";

    // path where runner loads classes
    public static String runnerClassLoadPath = System.getProperty("catalina.base") + "\\webapps\\RuntimeManager\\WEB-INF\\classes\\";
 
    /*
     * SIOTH Config
     */
    public static String mongoConfiguration = "MongoConfiguration";
    public static String mongoServerHostName = "MongoServerHostName";
    public static String mongoServerPort = "MongoServerPort";
    public static String mongoUserName = "MongoUserName";
    public static String mongoPassword = "MongoPassword";
    public static final String SIOTH_ENVIRONMENT_VARIABLE = "SIOTHJobEngine";
    public static final String APPLICATION_PROPERTIES_PATH = "file:${"+SIOTH_ENVIRONMENT_VARIABLE+"}/JobEngine/jobengine.properties";
	public final static String RULE_TEMPLATE_PATH = System.getenv(SIOTH_ENVIRONMENT_VARIABLE)+"\\JobEngine\\RuleTemplate.drl";
	public static final String SIOTH_JSON_CONFIG = System.getenv(ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE) + "\\SIOTHConfig.json";

   
    
    
    

    
}
