package io.je.runtime.beans;

import io.je.runtime.config.RunnerProperties;
import io.je.utilities.logger.JELogger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JERunnerInitBean implements InitializingBean {
	
	@Autowired
	RunnerProperties runnerProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            JELogger.initRunnerLogger(runnerProperties.getJeRunnerLogPath());
        }
        catch (Exception e) {e.printStackTrace();}
    }
}
