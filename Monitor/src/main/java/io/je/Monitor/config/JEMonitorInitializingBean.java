package io.je.Monitor.config;

import io.je.Monitor.zmq.JEMonitorSubscriber;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.date.DateUtils;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.zmq.ZMQSecurity;

@Component
public class JEMonitorInitializingBean  implements InitializingBean {

    @Autowired
    JEMonitorSubscriber subscriber;

    @Autowired
    MonitorProperties monitorProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            JELogger.initLogger("JEMonitor", monitorProperties.getJeMonitorLogPath(), monitorProperties.getJeMonitorLogLevel());
            //DateUtils.setFormatter(SIOTHConfigUtility.getSiothConfig().getDateFormat());
            JELogger.control(JEMessages.LOGGER_INITIALIZED,
                    LogCategory.MONITOR, null,
                    LogSubModule.JEMONITOR, null);

            subscriber.initSubscriber();
        }
        catch (Exception e) {e.printStackTrace();}
    }
}
