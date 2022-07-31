package io.siothconfig;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.log.LoggerUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SIOTHConfigUtility {

	public static final String JSON = ".json";
	private static SIOTHConfig siothConfig;

	private static String siothId;

	private SIOTHConfigUtility() {
		
	}

	public static void init() {
		ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		String jsonString = loadSIOTHConfig();

		if (jsonString != null) {
			try {
				siothConfig =  objectMapper.readValue(jsonString, SIOTHConfig.class);
			} catch (IOException e) {
				LoggerUtils.logException(e);
			}
		}
	}

	private static String loadSIOTHConfig() {
		String configPath = SIOTHConfigurationConstants.SIOTH_JSON_CONFIG + siothId + JSON;
		try {

			String json = new String(Files.readAllBytes(Paths.get(configPath)));
			return json;

		} catch (Exception e) {
			System.out.println("Failed to start application. [SIOTHConfig.json] was not found in : " + configPath);
			return null;
		}

	}


	public static SIOTHConfig getSiothConfig() {
		if(siothConfig == null) {
			init();
		}
		return siothConfig;
	}

	public static void setSiothConfig(SIOTHConfig siothConfig) {
		SIOTHConfigUtility.siothConfig = siothConfig;
	}


	public static void setSiothId(String id) {
		siothId = id;
	}

}
