package io.je.project.services;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import io.je.utilities.config.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.je.classbuilder.entity.JEClass;
import io.je.project.exception.JEExceptionHandler;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.AddClassException;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.ConfigException;
import io.je.utilities.exceptions.DataDefinitionUnreachableException;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.logger.JELogger;

/*
 * class responsible for application configuration
 */
@Service
public class ConfigurationService {



	@Autowired
	ProjectService projectService;

	@Autowired
	ClassService classService;

	static boolean runnerStatus = true;

	final int healthCheck = Utility.getSiothConfig().jobEngine.getCheckHealthEveryMs();
	
	
	

	/*
	 * init configuration : > load config from database >update config
	 */
	public void init()
			{
		try{
				JELogger.info(JEMessages.INITILIZING_BUILDER);

				updateRunner();
				classService.initClassUpdateListener();
		}catch (Exception e) {
			JELogger.error("JEBuilder did not start properly");
		}
	
	}







	
	public void updateRunner(){
		new Thread(() -> {
			try {
				boolean serverUp = false;
				while (!serverUp) {
					JELogger.debug(getClass(), " " + JEMessages.RUNNER_IS_DOWN_CHECKING_AGAIN_IN_5_SECONDS);
					Thread.sleep(healthCheck);
					serverUp = checkRunnerHealth();
				}
				JELogger.info(ProjectService.class, JEMessages.RUNNER_IS_UP_UPDATING_NOW);
				classService.loadAllClassesToBuilder();
				projectService.loadAllProjects();
			} catch (Exception e) {
				JEExceptionHandler.handleException(e);
			}
		}).start();

	}

	private static boolean checkRunnerHealth() {
		try {
			runnerStatus = JERunnerAPIHandler.checkRunnerHealth();
		} catch (Exception e) {
			JEExceptionHandler.handleException(e);
			return false;
		}
		return runnerStatus;
	}

	public static boolean isRunnerStatus() {
		return runnerStatus;
	}

	public static void setRunnerStatus(boolean status) {
		runnerStatus = status;
	}

	

}
