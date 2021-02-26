package io.je.utilities.config;

import io.je.utilities.apis.DataDefinitionApiHandler;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.models.ConfigModel;

/*
 * Singleton class handling JEBuilder Configuration
 */
public class JEConfiguration {

	static JEConfiguration instance;
	static String dataDefinitionURL;
	static String dataManagerURL;
	static String runtimeManagerURL;
	//TODO: remove harcoded config
	static String projectBuilderURL ="http://127.0.0.1:8484";
	static int subscriberPort;
	static int requestPort;

	private JEConfiguration() {

	}

	public static String getDataDefinitionURL() {
		return dataDefinitionURL;
	}

	public static void setDataDefinitionURL(String dataDefinitionURL) {
		if (dataDefinitionURL != null)
		{
			JEConfiguration.dataDefinitionURL = dataDefinitionURL;

		}
	}

	public static String getDataManagerURL() {
		return dataManagerURL;
	}

	public static void setDataManagerURL(String dataManagerURL) {
		if (dataManagerURL != null)
		{
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
		return configModel;
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

	public static void updateConfig(ConfigModel configModel) {
		setDataDefinitionURL(configModel.getDataDefinitionURL());
		setDataManagerURL(configModel.getDataManagerURL());
		setRequestPort(configModel.getRequestPort());
		setSubscriberPort(configModel.getSubscriberPort());
		setRuntimeManagerURL(configModel.getRuntimeManagerURL());


	}

}
