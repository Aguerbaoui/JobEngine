package io.je.utilities.config;

import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEMessages;
import io.je.utilities.logger.JELogger;
import io.je.utilities.models.ConfigModel;

/*
 * Singleton class handling JEBuilder Configuration
 */
public class JEConfiguration {

	static JEConfiguration instance;
	
	//datamodel
	static String dataDefinitionURL;
	static String dataManagerURL;
	static int subscriberPort;
	static int requestPort;
	static String dataModelDateFormat;

	//je
	static String runtimeManagerURL;
	static String projectBuilderURL;
	static String droolsDateFormat;

	//email
	static String emailApiUrl;

	//logging system
	static String loggingSystemURL;
	static int loggingSystemZmqPublishPort;
	
	private JEConfiguration() {

	}
	
	

	public static ConfigModel getInstance() {
		if (instance == null) {
			instance = new JEConfiguration();
		}
		ConfigModel configModel = new ConfigModel();
		configModel.setDataDefinitionURL(dataDefinitionURL);
		configModel.setDataManagerURL(dataManagerURL);
		configModel.setRequestPort(requestPort);
		configModel.setRuntimeManagerURL(runtimeManagerURL);
		configModel.setSubscriberPort(subscriberPort);
		configModel.setProjectBuilderURL(projectBuilderURL);
		configModel.setDroolsDateFormat(droolsDateFormat);
		configModel.setEmailApiUrl(emailApiUrl);
		configModel.setDataModelDateFormat(dataModelDateFormat);
		configModel.setLoggingSystemURL(loggingSystemURL);
		configModel.setLoggingSystemZmqPublishPort(loggingSystemZmqPublishPort);
		return configModel;
	}
	
	public static void updateConfig(ConfigModel configModel) {
		JELogger.trace(JEMessages.UPDATING_CONFIGURATION + " = " + configModel.toString());
		setDataDefinitionURL(configModel.getDataDefinitionURL());
		setDataManagerURL(configModel.getDataManagerURL());
		setRequestPort(configModel.getRequestPort());
		setSubscriberPort(configModel.getSubscriberPort());
		setRuntimeManagerURL(configModel.getRuntimeManagerURL());
		setProjectBuilderURL(configModel.getProjectBuilderURL());
		setDroolsDateFormat(configModel.getDroolsDateFormat());
		setEmailApiURL(configModel.getEmailApiUrl());
		setDataModelDateFormat(configModel.getDataModelDateFormat());
		setLoggingSystemURL(configModel.getLoggingSystemURL());
		setLoggingSystemZmqPublishPort(configModel.getLoggingSystemZmqPublishPort());
	}
	
	
	




	public static String getDroolsDateFormat() {
		return droolsDateFormat;
	}

	public static String getEmailApiUrl() {
		return emailApiUrl;
	}

	
	public static void setEmailApiUrl(String emailApiUrl) {
		if(emailApiUrl!=null)
		{
			JEConfiguration.emailApiUrl = emailApiUrl;
		}
	}



	public static void setDroolsDateFormat(String droolsDateFormat) {
		if(droolsDateFormat!=null)
			JEConfiguration.droolsDateFormat = droolsDateFormat;
	}





	public static String getDataDefinitionURL() {
		return dataDefinitionURL;
	}

	public static void setDataDefinitionURL(String dataDefinitionURL) {
		if (dataDefinitionURL != null)
		{
			JELogger.info("updating data defintion url to : " + dataDefinitionURL);
			JEConfiguration.dataDefinitionURL = dataDefinitionURL;

		}
	}

	public static String getDataManagerURL() {
		return dataManagerURL;
	}

	public static void setDataManagerURL(String dataManagerURL) {
		if (dataManagerURL != null)
		{
			JELogger.info("updating data Manager url to : " + dataManagerURL);
			JEConfiguration.dataManagerURL = dataManagerURL;
		}
	}

	public static String getRuntimeManagerURL() {
		return runtimeManagerURL;
	}

	public static void setRuntimeManagerURL(String runtimeManagerURL) {
		if (runtimeManagerURL != null) {
			{
				JEConfiguration.runtimeManagerURL = runtimeManagerURL;
				JELogger.info("updating runtime Manager url to : " + runtimeManagerURL);
				JERunnerAPIHandler.setRuntimeManagerBaseApi(runtimeManagerURL);
			}
		}
	}

	public static int getSubscriberPort() {
		return subscriberPort;
	}

	public static void setSubscriberPort(int subscriberPort) {
		if (subscriberPort != 0)
			JEConfiguration.subscriberPort = subscriberPort;
	}

	public static int getRequestPort() {
		return requestPort;
	}

	public static void setRequestPort(int requestPort) {
		if (requestPort != 0)
			JEConfiguration.requestPort = requestPort;
	}
	
	

	
	

	public static String getProjectBuilderURL() {
		return projectBuilderURL;
	}

	public static void setProjectBuilderURL(String projectBuilderURL) {
		if(projectBuilderURL!=null)
		{
			JEConfiguration.projectBuilderURL = projectBuilderURL;

		}
	}

	public static void setEmailApiURL(String url) {
		if(url!=null)
		{
			JEConfiguration.emailApiUrl = url;

		}
	}



	public static String getDataModelDateFormat() {
		return dataModelDateFormat;
	}



	public static void setDataModelDateFormat(String dataModelDateFormat) {
		if(dataModelDateFormat !=null)
			{
			JEConfiguration.dataModelDateFormat = dataModelDateFormat;
			}
	}



	public static String getLoggingSystemURL() {
		return loggingSystemURL;
	}



	public static void setLoggingSystemURL(String loggingSystemURL) {
		if(loggingSystemURL!=null)
		{
			JEConfiguration.loggingSystemURL = loggingSystemURL;
		}
	}



	public static int getLoggingSystemZmqPublishPort() {
		return loggingSystemZmqPublishPort;
	}



	public static void setLoggingSystemZmqPublishPort(int loggingSystemZmqPublishPort) {
		if(loggingSystemZmqPublishPort!=0)
		{
			JEConfiguration.loggingSystemZmqPublishPort = loggingSystemZmqPublishPort;
		}
	}

	
	

}
