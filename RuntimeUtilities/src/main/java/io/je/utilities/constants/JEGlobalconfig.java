package io.je.utilities.constants;

public class JEGlobalconfig {
	
	//JERunner
    public  static String RUNTIME_MANAGER_BASE_API = "http://127.0.0.1:8081";
   // public  static String RUNTIME_MANAGER_BASE_API = "http://127.0.0.1:8182//RuntimeManager-0.0.1";

	
	
	//Data Definition
	
    public  static String CLASS_DEFINITION_API = "http://192.168.7.51:1515/api";

	
	
	//Data Model
    public  static String DATA_MANAGER_BASE_API = "tcp://192.168.7.51";

    public  static int SUBSCRIBER_PORT = 5554;

    public  static int REQUEST_PORT = 6638;
    
    
    //CLASSESS
    
    //path where .java files are generated : 
    public 	static String  classGenerationPath	= "D:\\myproject2" ;
    
    // path where builder loads classes 
    public static String builderClassLoadPath = System.getProperty("java.class.path").split(";")[0]; 
   // public static String builderClassLoadPath = System.getProperty("catalina.base") + "\\WEB-INF\\classes"; 
    
    // path where runner loads classes
    public static String runnerClassLoadPath = System.getProperty("java.class.path").split(";")[0]; 
    //public static String runnerClassLoadPath = System.getProperty("catalina.base") + "\\WEB-INF\\classes"; 




}
