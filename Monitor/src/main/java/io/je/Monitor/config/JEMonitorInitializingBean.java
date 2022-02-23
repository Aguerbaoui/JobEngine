package io.je.Monitor.config;

import io.siothconfig.SIOTHConfigUtility;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.je.Monitor.zmq.JEMonitorSubscriber;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;

@Component
public class JEMonitorInitializingBean  implements InitializingBean {

    @Autowired
    JEMonitorSubscriber subscriber;

    @Autowired
    MonitorProperties monitorProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            ConfigurationConstants.initConstants(monitorProperties.getSiothId(), monitorProperties.isDev());
            JELogger.initLogger("JEMonitor", monitorProperties.getJeMonitorLogPath(), monitorProperties.getJeMonitorLogLevel(), monitorProperties.isDev());
            SIOTHConfigUtility.setSiothId(monitorProperties.getSiothId());
            JELogger.control(JEMessages.LOGGER_INITIALIZED,
                    LogCategory.MONITOR, null,
                    LogSubModule.JEMONITOR, null);
            subscriber.initSubscriber();
        }
        catch (Exception e) {e.printStackTrace();}
    }
}
