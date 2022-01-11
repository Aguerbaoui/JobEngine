package io.je;

import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import io.siothconfig.SIOTHConfigUtility;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		//SIOTHConfigUtility.init();

		return application.sources(ProjectBuilderApplication.class).bannerMode(Banner.Mode.OFF);
	}

}
