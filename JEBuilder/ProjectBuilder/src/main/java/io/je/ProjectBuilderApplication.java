package io.je;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import io.je.project.config.ConfigurationConstants;

@SpringBootApplication
@EnableAutoConfiguration
@PropertySource(ConfigurationConstants.APPLICATION_PROPERTIES_PATH)
public class ProjectBuilderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectBuilderApplication.class, args);
    }

}
