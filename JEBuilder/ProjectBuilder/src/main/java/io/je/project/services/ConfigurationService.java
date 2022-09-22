package io.je.project.services;

import io.je.project.exception.JEExceptionHandler;
import io.je.project.listener.ProjectZMQResponder;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;

import static io.je.utilities.constants.JEMessages.ZMQ_RESPONSE_STARTED;
import static io.je.utilities.constants.JEMessages.ZMQ_RESPONSE_START_FAIL;

/*
 * Class responsible for application configuration
 */
@Service
@Lazy
public class ConfigurationService {

    @Autowired
    @Lazy
    ProjectService projectService;
    @Autowired
    @Lazy
    ClassService classService;
    @Autowired
    @Lazy
    ProjectZMQResponder projectZMQResponder;

    static boolean runnerStatus = true;

    final int healthCheck = SIOTHConfigUtility.getSiothConfig().getJobEngine().getCheckHealthEveryMs();

    Thread classZMQSubscriberThread = null;
    Thread projectZMQResponderThread = null;

    /*
     * check JERunner health
     * */
    private static boolean checkRunnerHealth() {
        try {
            runnerStatus = JERunnerAPIHandler.checkRunnerHealth();
        } catch (Exception e) {
            JEExceptionHandler.handleException(e);
            return false;
        }
        return runnerStatus;
    }

    /*
     * Returns JERunner status
     * */
    public static boolean isRunnerStatus() {
        return runnerStatus;
    }

    /*
     * Set JERunner status
     * */
    public static void setRunnerStatus(boolean status) {
        runnerStatus = status;
    }

    /*
     * init configuration : > load config from database >update config
     */
    public void init() {

        try {
            JELogger.debug(JEMessages.INITILIZING_BUILDER, LogCategory.DESIGN_MODE,
                    null, LogSubModule.JEBUILDER, null);

            updateRunner();

            classZMQSubscriberThread = classService.initClassZMQSubscriber();

        } catch (Exception exception) {
            JELogger.logException(exception);
            JELogger.debug("JEBuilder did not start properly : " + exception.getMessage(), LogCategory.DESIGN_MODE,
                    null, LogSubModule.JEBUILDER, null);
        }

    }

    /*
     * Initialize JE ZMQ responder
     * */
    public void initResponder() {
        try {

            projectZMQResponderThread = new Thread(projectZMQResponder);

            projectZMQResponderThread.start();

            JELogger.info(ZMQ_RESPONSE_STARTED + "tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode() + ":" + SIOTHConfigUtility.getSiothConfig().getPorts().getJeResponsePort(), null, null, LogSubModule.JEBUILDER, null);

        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error(ZMQ_RESPONSE_START_FAIL + JEExceptionHandler.getExceptionMessage(e), null, null, LogSubModule.JEBUILDER, null);

        }

    }

    /*
     * Close
     */
    public void close() {

        // FIXME Interrupt Thread before closing socket to avoid org.zeromq.ZMQException: Errno 4
        if (classZMQSubscriberThread != null) {
            if (classZMQSubscriberThread.isAlive()) {
                classZMQSubscriberThread.interrupt();
            }
        }

        // FIXME Interrupt Thread before closing socket to avoid org.zeromq.ZMQException: Errno 4
        if (projectZMQResponderThread != null) {
            if (projectZMQResponderThread.isAlive()) {
                projectZMQResponderThread.interrupt();
            }
        }

        if (projectZMQResponder != null) {
            projectZMQResponder.setListening(false);
            projectZMQResponder.closeSocket();
        }

        if (classService.getClassZMQSubscriber() != null) {
            classService.getClassZMQSubscriber().setListening(false);
            classService.getClassZMQSubscriber().closeSocket();
        }

    }

    /*
     * Update JERunner with projects and classes data
     * */
    public void updateRunner() {
        new Thread(() -> {
            try {

                boolean serverUp = false;

                while (!serverUp) {
                    JELogger.debug(JEMessages.RUNNER_IS_DOWN_CHECKING_AGAIN_IN_5_SECONDS,
                            LogCategory.DESIGN_MODE, null,
                            LogSubModule.JEBUILDER, null);
                    Thread.sleep(healthCheck);
                    serverUp = checkRunnerHealth();
                }

                JELogger.debug(JEMessages.RUNNER_IS_UP_UPDATING_NOW,
                        LogCategory.DESIGN_MODE, null,
                        LogSubModule.JEBUILDER, null);

                boolean loadedFiles = false;

                while (!loadedFiles) {
                    try {

                        classService.loadAllClasses();

                        projectService.loadAllProjects();

                        loadedFiles = true;
                    } catch (Exception e) {
                        LoggerUtils.logException(e);
                        loadedFiles = false;
                        JELogger.debug(JEMessages.DATABASE_IS_DOWN_CHECKING_AGAIN,
                                LogCategory.DESIGN_MODE, null,
                                LogSubModule.JEBUILDER, null);
                        Thread.sleep(healthCheck);
                    }
                }

            } catch (Exception e) {
                JEExceptionHandler.handleException(e);
            }
        }).start();

    }

}
