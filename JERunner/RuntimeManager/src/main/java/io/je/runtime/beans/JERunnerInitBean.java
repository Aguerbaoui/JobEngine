package io.je.runtime.beans;

import io.je.runtime.config.RunnerProperties;
import io.je.utilities.log.JELogger;
import io.je.utilities.monitoring.JEMonitor;
import io.siothconfig.SIOTHConfigUtility;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.date.DateUtils;
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
            DateUtils.setFormatter(SIOTHConfigUtility.getSiothConfig().getDateFormat());
            JEMonitor.setPort("15020");
        }
        catch (Exception e) {e.printStackTrace();}
    }
}
