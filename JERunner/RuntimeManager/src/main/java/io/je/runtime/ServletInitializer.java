package io.je.runtime;


import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		//SIOTHConfigUtility.init();
		//System.setProperty("drools.dateformat", SIOTHConfigUtility.getSiothConfig().getDateFormat());

		return application.sources(JERunnerApplication.class).bannerMode(Banner.Mode.OFF);
	}

}
