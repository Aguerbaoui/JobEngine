package io.je.runtime.beans;

import io.je.utilities.logger.JELogger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class JERunnerInitBean implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            JELogger.initRunnerLogger();
        }
        catch (Exception e) {e.printStackTrace();}
    }
}
