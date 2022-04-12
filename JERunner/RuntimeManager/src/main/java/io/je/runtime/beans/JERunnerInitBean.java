package io.je.runtime.beans;

import io.je.runtime.config.RunnerProperties;
import io.je.runtime.services.ConfigurationService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JERunnerInitBean implements InitializingBean {
	
	@Autowired
	RunnerProperties runnerProperties;
	
	@Autowired
	ConfigurationService configurationService;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
        	configurationService.init(runnerProperties);
         
        }
        catch (Exception e) {e.printStackTrace();}
    }
}
