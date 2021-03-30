package io.je.utilities.config;

import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;

public class SIOTHConfiguration {

	private SIOTHConfiguration instance;
	private static String mongoServerHostName;
	private static String mongoServerPort;
	private static String mongoUserName;
	private static String mongoPassword;
	private static String dataBaseName;

	private SIOTHConfiguration() {
		super();
	}

	public static void init() {
		String jsonString = loadSIOTHConfig();
		if (jsonString != null) {
			JSONObject jsonConfig = new JSONObject(jsonString);
			JSONObject mongoConfig = jsonConfig.getJSONObject(ConfigurationConstants.mongoConfiguration);
			mongoServerHostName = mongoConfig.getString(ConfigurationConstants.mongoServerHostName);
			mongoServerPort =String.valueOf( mongoConfig.getInt(ConfigurationConstants.mongoServerPort));
			mongoUserName = mongoConfig.getString(ConfigurationConstants.mongoUserName);
			mongoPassword = mongoConfig.getString(ConfigurationConstants.mongoPassword);
			dataBaseName="JobEngine";

		}
	}

	private static String loadSIOTHConfig() {
		String configPath = ConfigurationConstants.SIOTH_JSON_CONFIG;
		try {
			String file = configPath;
			String json = new String(Files.readAllBytes(Paths.get(file)));
			return json;

		} catch (Exception e) {
			System.out.println("Failed to start application. [SIOTHConfig.json] was not found in : " + configPath);
			return null;
		}

	}

	public static String getConfig(String configName) {
		return null;
	}

	public SIOTHConfiguration getInstance() {
		if (instance == null) {
			instance = new SIOTHConfiguration();
		}
		return instance;
	}

	public static String getMongoServerHostName() {
		return mongoServerHostName;
	}

	public static void setMongoServerHostName(String mongoServerHostName) {
		SIOTHConfiguration.mongoServerHostName = mongoServerHostName;
	}

	public static String getMongoServerPort() {
		return mongoServerPort;
	}

	public static void setMongoServerPort(String mongoServerPort) {
		SIOTHConfiguration.mongoServerPort = mongoServerPort;
	}

	public static String getMongoUserName() {
		return mongoUserName;
	}

	public static void setMongoUserName(String mongoUserName) {
		SIOTHConfiguration.mongoUserName = mongoUserName;
	}

	public static String getMongoPassword() {
		return mongoPassword;
	}

	public static void setMongoPassword(String mongoPassword) {
		SIOTHConfiguration.mongoPassword = mongoPassword;
	}

	public static String getDataBaseName() {
		return dataBaseName;
	}

	private static void setDataBaseName(String dataBaseName) {
		SIOTHConfiguration.dataBaseName = dataBaseName;
	}
	
			

}
