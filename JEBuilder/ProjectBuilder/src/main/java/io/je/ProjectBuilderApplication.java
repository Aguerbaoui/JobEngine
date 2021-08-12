package io.je;

import io.je.utilities.config.Utility;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
@EnableAutoConfiguration
public class ProjectBuilderApplication {

    public static void main(String[] args) {
    	Utility.init();
        SpringApplication app = new SpringApplication(ProjectBuilderApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "8080"));
        app.run(args);
        JELogger.debug(JEMessages.BUILDER_STARTED,  LogCategory.DESIGN_MODE,
                null, LogSubModule.JEBUILDER, null);
//C:\Program Files\Integration Objects\Integration Objects' SmartIoT Highway\JobEngine\Builder\properties
    }


}
