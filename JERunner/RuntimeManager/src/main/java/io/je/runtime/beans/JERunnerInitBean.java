package io.je.runtime.beans;

import io.je.runtime.config.RunnerProperties;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import io.je.utilities.monitoring.JEMonitor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.zmq.ZMQSecurity;

@Component
public class JERunnerInitBean implements InitializingBean {
	
	@Autowired
	RunnerProperties runnerProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
        	
            ZMQSecurity.setSecure(runnerProperties.getUseZmqSecurity());
            JELogger.initLogger("JERunner", runnerProperties.getJeRunnerLogPath(),runnerProperties.getJeRunnerLogLevel());
            JELogger.control(JEMessages.LOGGER_INITIALIZED,
                    LogCategory.DESIGN_MODE, null,
                    LogSubModule.JERUNNER, null);
            ConfigurationConstants.setSIOTHID(runnerProperties.getSiothId());
            JEMonitor.setPort(runnerProperties.getMonitoringPort());
            System.setProperty("drools.dateformat", ConfigurationConstants.DROOLS_DATE_FORMAT);
            
        }
        catch (Exception e) {e.printStackTrace();}
    }
}
