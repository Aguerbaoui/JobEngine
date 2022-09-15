package io.je.Monitor;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.*;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.jdbc.DataSourcePoolMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.web.client.HttpClientMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.web.tomcat.TomcatMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;

@SpringBootApplication
public class JEMonitorApplication {

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(JEMonitorApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("server.port", "59088");
        properties.put("server.servlet.context-path", "/JEMonitor");
        app.setDefaultProperties(properties);
        app.run(args);

    }

}
