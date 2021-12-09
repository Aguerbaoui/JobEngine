package io.je.project.services;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.je.project.exception.JEExceptionHandler;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.constants.JEMessages;
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
				boolean loadedFiles = false;
				while(!loadedFiles) {
					try {
						classService.loadAllClasses();
						projectService.loadAllProjects();
						loadedFiles = true;
					}
					catch (Exception e) {
						loadedFiles = false;
						JELogger.debug(JEMessages.DATABASE_IS_DOWN_CHECKING_AGAIN,
								LogCategory.DESIGN_MODE, null,
								LogSubModule.JEBUILDER,null);
						Thread.sleep(healthCheck);
					}
				}

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
