package io.je;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.siothconfig.SIOTHConfigUtility;

import java.util.Collections;

@SpringBootApplication
//@EnableAutoConfiguration
public class ProjectBuilderApplication {

    public static void main(String[] args)  {
        SIOTHConfigUtility.init();
      
        SpringApplication app = new SpringApplication(ProjectBuilderApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "8080"));
        app.run(args);
       
//C:\Program Files\Integration Objects\Integration Objects' SmartIoT Highway\JobEngine\Builder\properties
    }


}
