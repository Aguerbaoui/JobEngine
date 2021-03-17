package io.je.runtime;

import io.je.utilities.apis.JEBuilderApiHandler;
import io.je.utilities.logger.JELogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.annotation.PreDestroy;

@SpringBootApplication
public class JERunnerApplication {

	
    public static void main(String[] args) {
        SpringApplication.run(JERunnerApplication.class, args);
        JELogger.info(" Runner started successfully");


    }

    @PreDestroy
    public void onDestroy() throws Exception {

    	JEBuilderApiHandler.requestUpdateFromBuilder();
    }
}



