package io.je;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.siothconfig.SIOTHConfigUtility;

import java.util.Collections;
import java.util.HashMap;

@SpringBootApplication
//@EnableAutoConfiguration
public class ProjectBuilderApplication {

    public static void main(String[] args)  {
        SIOTHConfigUtility.init();
      
        SpringApplication app = new SpringApplication(ProjectBuilderApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("server.port", "8080");
       // properties.put("spring.servlet.multipart.max-file-size", "100MB");
        //properties.put("spring.servlet.multipart.max-request-size", "100MB");
        /*app.setDefaultProperties(Collections
                .singletonMap("server.port", "8080"));*/
        app.setDefaultProperties(properties);
        app.run(args);
       
//C:\Program Files\Integration Objects\Integration Objects' SmartIoT Highway\JobEngine\Builder\properties
    }


}
