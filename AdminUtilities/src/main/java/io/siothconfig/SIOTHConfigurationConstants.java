package io.siothconfig;


public class SIOTHConfigurationConstants {

	
	   public static final String SIOTH_ENVIRONMENT_VARIABLE = "SIOTHJobEngine";
	    public static final String APPLICATION_PROPERTIES_PATH = "file:${"+SIOTH_ENVIRONMENT_VARIABLE+"}/JobEngine/jobengine.properties";
	    public static final String SIOTH_JSON_CONFIG = System.getenv(SIOTH_ENVIRONMENT_VARIABLE) + "\\SIOTHConfig.json";
}
