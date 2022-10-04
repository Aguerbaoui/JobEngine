package io.je.Monitor;

import io.je.Monitor.zmq.JEMonitorSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PreDestroy;


/*
 * ServletInitializer
 * */
@Configuration
@ComponentScan
@EnableAsync(proxyTargetClass=true)
@EnableAutoConfiguration
public class ServletInitializer extends SpringBootServletInitializer {

    @Autowired
    JEMonitorSubscriber jeMonitorSubscriber;


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

        SpringApplicationBuilder applicationBuilder = application.sources(JEMonitorApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(true)
                .web(WebApplicationType.SERVLET);

        // applicationBuilder.context().registerShutdownHook();

        return applicationBuilder;
    }


    @PreDestroy
    public void destroy() {
        System.err.println(
                "ServletInitializer Callback triggered - @PreDestroy");

        jeMonitorSubscriber.close();

    }

}
