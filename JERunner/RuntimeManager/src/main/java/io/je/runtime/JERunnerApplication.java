package io.je.runtime;

import java.util.Collections;

import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.logger.JELogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import io.je.utilities.logger.JELogger;

import javax.annotation.PreDestroy;


@SpringBootApplication
public class JERunnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JERunnerApplication.class, args);
    }

    @PreDestroy
    public void onDestroy() throws Exception {
        JERunnerAPIHandler.requestUpdateFromBuilder();
    }
}



