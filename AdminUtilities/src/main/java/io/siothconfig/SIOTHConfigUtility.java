package io.siothconfig;

import com.fasterxml.jackson.databind.DeserializationFeature;

import com.fasterxml.jackson.databind.ObjectMapper;

import utils.date.DateUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SIOTHConfigUtility {

	private static SIOTHConfig siothConfig;

	private SIOTHConfigUtility() {
		
	}

	public static void init() {
		ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String jsonString = loadSIOTHConfig();
		if (jsonString != null) {
			try {
				siothConfig =  objectMapper.readValue(jsonString, SIOTHConfig.class);
				DateUtils.setFormatter(siothConfig.getDateFormat());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	


		}
	}

	private static String loadSIOTHConfig() {
		String configPath = SIOTHConfigurationConstants.SIOTH_JSON_CONFIG;
		try {
			String file = configPath;
			String json = new String(Files.readAllBytes(Paths.get(file)));
			return json;

		} catch (Exception e) {
			System.out.println("Failed to start application. [SIOTHConfig.json] was not found in : " + configPath);
			return null;
		}

	}



	public static SIOTHConfig getSiothConfig() {
		return siothConfig;
	}

	public static void setSiothConfig(SIOTHConfig siothConfig) {
		SIOTHConfigUtility.siothConfig = siothConfig;
	}

	
			

}
