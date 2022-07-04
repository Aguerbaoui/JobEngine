package io.je.runtime.beans;

import io.je.runtime.config.RunnerProperties;
import io.je.runtime.services.ConfigurationService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.zmq.ZMQConfiguration;

@Component
public class JERunnerInitBean implements InitializingBean {
	
	@Autowired
	RunnerProperties runnerProperties;
	
	@Autowired
	ConfigurationService configurationService;

    @Override
    public void afterPropertiesSet() {
        try {
        	configurationService.init(runnerProperties);
            ZMQConfiguration.setHeartbeatTimeout(runnerProperties.getZmqHeartbeatValue());
            ZMQConfiguration.setHandshakeInterval(runnerProperties.getZmqHandshakeInterval());
            ZMQConfiguration.setReceiveHighWatermark(runnerProperties.getZmqReceiveHighWatermark());
            ZMQConfiguration.setSendHighWatermark(runnerProperties.getZmqSendHighWatermark());
        }
        catch (Exception e) {e.printStackTrace();}
    }
}
