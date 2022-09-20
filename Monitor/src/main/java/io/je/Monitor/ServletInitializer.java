package io.je.Monitor;

import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

        SpringApplicationBuilder applicationBuilder = application.sources(JEMonitorApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(true)
                .web(WebApplicationType.SERVLET);

        // applicationBuilder.context().registerShutdownHook();

        return applicationBuilder;
    }

}
