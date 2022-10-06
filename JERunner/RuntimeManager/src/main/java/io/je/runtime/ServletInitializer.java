package io.je.runtime;


import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;


/*
 * ServletInitializer
 * */
@Configuration
@ComponentScan
@EnableAsync(proxyTargetClass=true)
@EnableAutoConfiguration
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

        SpringApplicationBuilder applicationBuilder = application.sources(RuntimeManagerApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(true)
                .web(WebApplicationType.SERVLET);

        // applicationBuilder.context().registerShutdownHook();

        return applicationBuilder;
    }

}
