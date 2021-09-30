package io.je.runtime;


import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import io.je.utilities.config.Utility;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		Utility.init();
		System.setProperty("drools.dateformat", Utility.getSiothConfig().getDateFormat());

		return application.sources(JERunnerApplication.class).bannerMode(Banner.Mode.OFF);
	}

}
