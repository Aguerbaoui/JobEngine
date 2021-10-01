package io.je.project.services;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

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
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import utils.log.LogCategory;
import utils.log.LogSubModule;

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

	final int healthCheck = SIOTHConfigUtility.getSiothConfig().getJobEngine().getCheckHealthEveryMs();
	
	
	

	/*
	 * init configuration : > load config from database >update config
	 */
	public void init()
			{
		try{
				JELogger.debug(JEMessages.INITILIZING_BUILDER,  LogCategory.DESIGN_MODE,
					null, LogSubModule.JEBUILDER, null);
				updateRunner();
				classService.initClassUpdateListener();
		}catch (Exception e) {
			JELogger.debug("JEBuilder did not start properly\n" + Arrays.toString(e.getStackTrace()),  LogCategory.DESIGN_MODE,
					null, LogSubModule.JEBUILDER, null);
		}
	
	}







	
	public void updateRunner(){
		new Thread(() -> {
			try {
				boolean serverUp = false;
				while (!serverUp) {
					JELogger.debug(JEMessages.RUNNER_IS_DOWN_CHECKING_AGAIN_IN_5_SECONDS,
							LogCategory.DESIGN_MODE, null,
							LogSubModule.JEBUILDER,null);
					Thread.sleep(healthCheck);
					serverUp = checkRunnerHealth();
				}
				JELogger.debug(JEMessages.RUNNER_IS_UP_UPDATING_NOW,
						LogCategory.DESIGN_MODE, null,
						LogSubModule.JEBUILDER,null);
				classService.loadAllClasses();
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
