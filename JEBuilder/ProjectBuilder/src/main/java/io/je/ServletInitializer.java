package io.je;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import io.je.utilities.config.SIOTHConfiguration;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		SIOTHConfiguration.init();
		return application.sources(ProjectBuilderApplication.class);
	}

}
