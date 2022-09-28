package io.je;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;

@SpringBootApplication
public class ProjectBuilderApplication {

    public static void main(String[] args) {

        System.setProperty("spring.devtools.restart.enabled", "false");

        SpringApplication app = new SpringApplication(ProjectBuilderApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        HashMap<String, Object> properties = new HashMap<>();
        //DEV port configurations
        properties.put("server.port", "59188");
        properties.put("server.servlet.context-path", "/ProjectBuilder");
        app.setDefaultProperties(properties);
        app.run(args);

    }

}
