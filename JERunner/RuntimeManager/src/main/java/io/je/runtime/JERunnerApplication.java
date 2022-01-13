package io.je.runtime;

import io.je.utilities.apis.JEBuilderApiHandler;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

import javax.annotation.PreDestroy;

@SpringBootApplication
public class JERunnerApplication {

	
    public static void main(String[] args) {
    	//SIOTHConfigUtility.init();
    	//System.setProperty("drools.dateformat", SIOTHConfigUtility.getSiothConfig().getDateFormat());
        SpringApplication app = new SpringApplication(JERunnerApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "8081"));
        app.run(args);
        JELogger.debug(JEMessages.RUNNER_STARTED,  LogCategory.RUNTIME,
                null, LogSubModule.JERUNNER, null);
    }

    @PreDestroy
    public void onDestroy() throws Exception {

    	JEBuilderApiHandler.requestUpdateFromBuilder();
    }
}



