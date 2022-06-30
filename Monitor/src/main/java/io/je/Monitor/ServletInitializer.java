package io.je.Monitor;

import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		//SIOTHConfigUtility.init();
		return application.sources(MonitorApplication.class).bannerMode(Banner.Mode.OFF);
	}

}
