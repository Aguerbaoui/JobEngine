package io.je.runtime;

import io.je.utilities.apis.JEBuilderApiHandler;
import io.je.utilities.config.Utility;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.logger.JELogger;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

import javax.annotation.PreDestroy;
import java.util.Collections;

@SpringBootApplication
public class JERunnerApplication {

	
    public static void main(String[] args) {
    	Utility.init();
    	 System.setProperty("drools.dateformat", Utility.getSiothConfig().getDateFormat());
        SpringApplication app = new SpringApplication(JERunnerApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "8081"));
        app.run(args);

        JELogger.info(JEMessages.RUNNER_STARTED);

    }

    @PreDestroy
    public void onDestroy() throws Exception {

    	JEBuilderApiHandler.requestUpdateFromBuilder();
    }
}



