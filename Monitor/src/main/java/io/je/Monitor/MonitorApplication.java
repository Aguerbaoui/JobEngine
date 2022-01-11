package io.je.Monitor;

import java.util.HashMap;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.siothconfig.SIOTHConfigUtility;

@SpringBootApplication
public class MonitorApplication {

	public static void main(String[] args) {
		//SIOTHConfigUtility.init();
		SpringApplication app = new SpringApplication(MonitorApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("server.port", "8082");
		app.setDefaultProperties(properties);
		app.run(args);

	}

}
