package io.je.runtime.beans;

import io.je.ruleengine.data.DataModelListener;
import io.je.runtime.config.RunnerProperties;
import io.je.runtime.services.ConfigurationService;
import io.je.utilities.log.ZMQLogPublisher;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.log.LoggerUtils;
import utils.zmq.ZMQConfiguration;

import javax.annotation.PreDestroy;


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

            ZMQConfiguration.setHeartbeatTimeout(runnerProperties.getZmqHeartbeatTimeout());
            ZMQConfiguration.setHandshakeInterval(runnerProperties.getZmqHandshakeInterval());
            ZMQConfiguration.setReceiveTimeout(runnerProperties.getZmqReceiveTimeout());
            ZMQConfiguration.setSendTimeout(runnerProperties.getZmqSendTimeout());
            ZMQConfiguration.setReceiveHighWatermark(runnerProperties.getZmqReceiveHighWatermark());
            ZMQConfiguration.setSendHighWatermark(runnerProperties.getZmqSendHighWatermark());

        } catch (Exception e) {
            LoggerUtils.logException(e);
        }
    }

    @PreDestroy
    public void destroy() {
        System.err.println(
                "JERunnerInitBean Callback triggered - @PreDestroy");

        configurationService.close();

        ZMQLogPublisher.close();

        DataModelListener.close();

    }

}
