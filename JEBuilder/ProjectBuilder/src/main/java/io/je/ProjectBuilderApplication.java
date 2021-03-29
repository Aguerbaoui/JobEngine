package io.je;

import io.je.utilities.config.SIOTHConfiguration;
import io.je.utilities.logger.JELogger;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
@EnableAutoConfiguration
public class ProjectBuilderApplication {

    public static void main(String[] args) {
    	SIOTHConfiguration.init();
        SpringApplication app = new SpringApplication(ProjectBuilderApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "8080"));
        app.run(args);
        JELogger.info(" Builder started successfully");
//C:\Program Files\Integration Objects\Integration Objects' SmartIoT Highway\JobEngine\Builder\properties
    }


}
