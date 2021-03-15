package io.je.utilities.config;

public class ConfigurationConstants {
 
    
    /*
     * Config for testing on the IDE
     */
    
	 //path where .java files are generated : 
    public 	static String  classGenerationPath	= "D:\\myproject2" ;
	
    // path where builder loads classes 
    public static String builderClassLoadPath = System.getProperty("java.class.path").split(";")[0]; 
    
    // path where runner loads classes
    public static String runnerClassLoadPath = System.getProperty("java.class.path").split(";")[0]; 
    
  
	
	public final static String ruleTemplatePath = "D:\\ProgramData\\Integration Objects\\JobEngine\\JEBuilder\\RuleTemplate.drl";
    
    /*
     * Config for tomcat
     */
    
 /*   //path where .java files are generated :
    public static String classGenerationPath = System.getProperty("catalina.base") + "\\webapps\\ProjectBuilder\\WEB-INF\\classes\\io\\je\\";

    // path where builder loads classes
    public static String builderClassLoadPath = System.getProperty("catalina.base") + "\\webapps\\ProjectBuilder\\WEB-INF\\classes\\";

    // path where runner loads classes
    public static String runnerClassLoadPath = System.getProperty("catalina.base") + "\\webapps\\RuntimeManager\\WEB-INF\\classes\\";
 
*/
   
}
