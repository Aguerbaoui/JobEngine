package io.je.runtime.services;

import io.je.project.exception.JEExceptionHandler;
import io.je.runtime.config.RunnerProperties;
import io.je.runtime.listener.JERunnerResponder;
import io.je.utilities.apis.JERunnerRequester;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import io.je.utilities.monitoring.JEMonitor;
import io.siothconfig.SIOTHConfigUtility;
import org.springframework.stereotype.Service;
import utils.ProcessRunner;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;
import utils.zmq.ZMQSecurity;
import utils.zmq.ZMQType;

@Service
public class ConfigurationService {

    public void init(RunnerProperties properties) {


        //init constants
        initConstants(properties.getSiothId(), properties.isDev());

        //init logger
        initLogger(properties.getJeRunnerLogPath(), properties.getJeRunnerLogLevel());

        //Set ZMQ configuration
        ZMQSecurity.setSecure(properties.getUseZmqSecurity());


        JEMonitor.setPort(properties.getMonitoringPort());


        System.setProperty("drools.dateformat", ConfigurationConstants.DROOLS_DATE_FORMAT);
        ProcessRunner.setProcessDumpPath(properties.getProcessesDumpPath(), properties.isDumpJavaProcessExecution());

        initResponder(properties.getJeRunnerZMQResponsePort());
        JERunnerRequester.setRequesterPort(properties.getJeRunnerZMQResponsePort());


    }


    public void initResponder(int responsePort) {

        try {

            JERunnerResponder responser = new JERunnerResponder("tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(), responsePort, ZMQType.BIND);

            Thread listener = new Thread(responser);

            listener.start();

            JELogger.info(JEMessages.ZMQ_RESPONSE_STARTED + "tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode() + ":" + responsePort, null, null, LogSubModule.JEBUILDER, null);

        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error(JEMessages.ZMQ_RESPONSE_START_FAIL + JEExceptionHandler.getExceptionMessage(e), null, null, LogSubModule.JEBUILDER, null);

        }

    }


    private void initConstants(String siothId, boolean isDev) {

        SIOTHConfigUtility.setSiothId(siothId);
        ConfigurationConstants.setDev(isDev);
        ConfigurationConstants.setJavaGenerationPath(SIOTHConfigUtility.getSiothConfig().getJobEngine().getGeneratedClassesPath());

    }

    private void initLogger(String logPath, String logLevel) {
        LoggerUtils.initLogger("JERunner", logPath, logLevel, ConfigurationConstants.isDev());
        JELogger.control(JEMessages.LOGGER_INITIALIZED,
                LogCategory.DESIGN_MODE, null,
                LogSubModule.JERUNNER, null);
    }

}
