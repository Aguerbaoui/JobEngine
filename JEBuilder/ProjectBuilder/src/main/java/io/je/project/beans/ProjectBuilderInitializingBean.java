package io.je.project.beans;

import io.je.project.config.AuthenticationInterceptor;
import io.je.project.config.BuilderProperties;
import io.je.project.config.LicenseProperties;
import io.je.project.services.ConfigurationService;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.LicenseNotActiveException;
import io.je.utilities.log.JELogger;
import io.je.utilities.log.ZMQLogPublisher;
import io.je.utilities.monitoring.JEMonitor;
import io.siothconfig.SIOTHConfigUtility;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import utils.ProcessRunner;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;
import utils.zmq.ZMQConfiguration;
import utils.zmq.ZMQSecurity;

import javax.annotation.PreDestroy;

@Component
public class ProjectBuilderInitializingBean implements InitializingBean {

    @Autowired
    BuilderProperties builderProperties;

    @Autowired
    @Lazy
    ConfigurationService configurationService;

    @Override
    public void afterPropertiesSet() {

        try {

            JELogger.debug(JEMessages.INITILIZING_BUILDER, LogCategory.DESIGN_MODE,
                    null, LogSubModule.JEBUILDER, null);

            //Initialize SIOTHConfig.json
            ConfigurationConstants.initConstants(builderProperties.getSiothId(), builderProperties.isDev());
            SIOTHConfigUtility.setSiothId(builderProperties.getSiothId());

            //Initialize logger
            JELogger.initLogger("JEBuilder", builderProperties.getJeBuilderLogPath(),
                    builderProperties.getJeBuilderLogLevel(), builderProperties.isDev());

            JELogger.control(JEMessages.LOGGER_INITIALIZED,
                    LogCategory.DESIGN_MODE, null,
                    LogSubModule.JEBUILDER, null);

            ConfigurationConstants.setJavaGenerationPath(SIOTHConfigUtility.getSiothConfig().getJobEngine().getGeneratedClassesPath());

            //Initialize authentication interceptor
            AuthenticationInterceptor.init(builderProperties.getIssuer());

            //Initialize License
            LicenseProperties.init();

            while (!LicenseProperties.licenseIsActive()) {
                try {
                    Thread.sleep(5000);
                    LicenseProperties.checkLicenseIsActive();
                } catch (LicenseNotActiveException e) {
                    JELogger.error(e.getMessage(), LogCategory.SIOTH_APPLICATION, "",
                            LogSubModule.JEBUILDER, "");
                } catch (InterruptedException e) {
                    LoggerUtils.logException(e);
                }
            }

            JEMonitor.setPort(builderProperties.getMonitoringPort());

            ZMQSecurity.setSecure(builderProperties.getUseZmqSecurity());

            ZMQConfiguration.setHeartbeatTimeout(builderProperties.getZmqHeartbeatTimeout());
            ZMQConfiguration.setHandshakeInterval(builderProperties.getZmqHandshakeInterval());
            ZMQConfiguration.setReceiveTimeout(builderProperties.getZmqReceiveTimeout());
            ZMQConfiguration.setSendTimeout(builderProperties.getZmqSendTimeout());
            ZMQConfiguration.setReceiveHighWatermark(builderProperties.getZmqReceiveHighWatermark());
            ZMQConfiguration.setSendHighWatermark(builderProperties.getZmqSendHighWatermark());

            ProcessRunner.setProcessDumpPath(builderProperties.getProcessesDumpPath(), builderProperties.isDumpJavaProcessExecution());

            //Initialize JE configurations
            configurationService.init();

            JELogger.control(JEMessages.BUILDER_STARTED, LogCategory.DESIGN_MODE,
                    null, LogSubModule.JEBUILDER, null);

        } catch (Exception e) {

            JELogger.logException(e);

            JELogger.error(JEMessages.UNEXPECTED_ERROR, LogCategory.DESIGN_MODE, null,
                    LogSubModule.JEBUILDER, null);
        }

    }

    @PreDestroy
    public void destroy() {
        System.err.println(
                "ProjectBuilderInitializingBean Callback triggered - @PreDestroy");

        configurationService.close();

        ZMQLogPublisher.close();
    }

}