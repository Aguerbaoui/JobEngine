package io.je;

import io.je.project.config.ConfigurationConstants;
import io.je.utilities.logger.JELogger;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableAutoConfiguration
@PropertySource(ConfigurationConstants.APPLICATION_PROPERTIES_PATH)
public class ProjectBuilderApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ProjectBuilderApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
        JELogger.info(" Builder started successfully");
//C:\Program Files\Integration Objects\Integration Objects' SmartIoT Highway\JobEngine\Builder\properties
    }


}
