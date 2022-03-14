package io.je.runtime;

import io.je.utilities.apis.JEBuilderApiHandler;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;
import java.util.HashMap;

import javax.annotation.PreDestroy;

@SpringBootApplication
public class JERunnerApplication {

	
    public static void main(String[] args) {
    	//SIOTHConfigUtility.init();
    	//System.setProperty("drools.dateformat", SIOTHConfigUtility.getSiothConfig().getDateFormat());
        SpringApplication app = new SpringApplication(JERunnerApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("server.port", "13021");
        properties.put("server.servlet.context-path", "/RuntimeManager");
        app.setDefaultProperties(properties);
        app.run(args);
        JELogger.debug(JEMessages.RUNNER_STARTED,  LogCategory.RUNTIME,
                null, LogSubModule.JERUNNER, null);
        JEClassLoader.getDataModelInstance();
    }

    @PreDestroy
    public void onDestroy() throws Exception {

    	JEBuilderApiHandler.requestUpdateFromBuilder();
    }
}



