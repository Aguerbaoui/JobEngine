package io.je.runtime;

import io.je.ruleengine.data.DataModelListener;
import io.je.utilities.apis.JEBuilderApiHandler;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import javax.annotation.PreDestroy;
import java.util.HashMap;

@SpringBootApplication
public class RuntimeManagerApplication {

    public static void main(String[] args) {

        System.setProperty("spring.devtools.restart.enabled", "false");

        SpringApplication app = new SpringApplication(RuntimeManagerApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("server.port", "59288");
        properties.put("server.servlet.context-path", "/RuntimeManager");
        app.setDefaultProperties(properties);
        app.run(args);

        JELogger.debug(JEMessages.RUNNER_STARTED, LogCategory.RUNTIME,
                null, LogSubModule.JERUNNER, null);

        System.out.println("Runtime.getRuntime().totalMemory() : " + Runtime.getRuntime().totalMemory());
        System.out.println("Runtime.getRuntime().maxMemory() : " + Runtime.getRuntime().maxMemory());
        System.out.println("Runtime.getRuntime().freeMemory() : " + Runtime.getRuntime().freeMemory());

        System.out.println("All Stack Traces size : " + (Thread.getAllStackTraces().size()));
        System.out.println("Current Thread Stack Trace length : " + (Thread.currentThread().getStackTrace().length));

        JEClassLoader.getDataModelInstance();

    }

    @PreDestroy
    public void onDestroy() throws Exception {
        System.err.println(
                "RuntimeManagerApplication Callback triggered - @PreDestroy");

        DataModelListener.close();

        JEBuilderApiHandler.requestUpdateFromBuilder();
    }
}



