package io.je.Monitor;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import java.util.HashMap;

@SpringBootApplication
public class MonitorApplication {

	public static void main(String[] args) {
		SIOTHConfigUtility.init();
		SpringApplication app = new SpringApplication(MonitorApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("server.port", "8082");
		app.setDefaultProperties(properties);
		app.run(args);

	}

}
