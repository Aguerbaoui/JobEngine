package io.je.runtime;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class JERunnerApplication {
	
	 public static void main(String[] args) {
	        SpringApplication app = new SpringApplication(JERunnerApplication.class);
	        app.setDefaultProperties(Collections
	                .singletonMap("server.port", "8081"));
	        app.run(args);
	    }

/*    public static void main(String[] args) {
        SpringApplication.run(JERunnerApplication.class, args);
    }
*/
}



