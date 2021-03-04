package io.je.project.services;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.je.classbuilder.entity.JEClass;
import io.je.project.exception.JEExceptionHandler;
import io.je.project.repository.ConfigRepository;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.config.JEConfiguration;
import io.je.utilities.constants.Errors;
import io.je.utilities.exceptions.AddClassException;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.DataDefinitionUnreachableException;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.models.ConfigModel;

/*
 * class responsible for application configuration
 */
@Service
public class ConfigurationService {

	@Autowired
	ConfigRepository configRepository;

	@Autowired
	ProjectService projectService;

	@Autowired
	ClassService classService;

	static boolean runnerStatus = true;

	/*
	 * init configuration : > load config from database >update config
	 */
	public void init()
			throws JERunnerErrorException, InterruptedException, ExecutionException, DataDefinitionUnreachableException,
			AddClassException, ClassLoadException, IOException, ProjectNotFoundException {

		ConfigModel configModel = loadConfigFromDb();
		if (configModel != null) {
			updateBuilderSettings(configModel);
			classService.loadAllClassesToBuilder();
			projectService.loadAllProjects();
			updateRunner(configModel);
		}

	}

	/*
	 * update runner config
	 */
	private void updateBuilderSettings(ConfigModel configModel) {
		JEConfiguration.updateConfig(configModel);

	}

	private void updateRunnerSettings(ConfigModel configModel)
			throws JERunnerErrorException, InterruptedException, ExecutionException {
		JERunnerAPIHandler.updateRunnerSettings(configModel);

	}

	/*
	 * load config from db
	 */
	public ConfigModel loadConfigFromDb() {

		// load config from db
		Optional<ConfigModel> config = configRepository.findById("ConfigJE");
		if (config.isEmpty()) {
			JELogger.warning(getClass(), Errors.MISSING_CONFIG);
		}
		if (config.isPresent()) {
			return config.get();
		}
		return null;
	}

	/*
	 * check that there is no missing config
	 */
	public boolean applicationIsConfiguredProperly() {
		return (JEConfiguration.getDataDefinitionURL() != null && JEConfiguration.getDataManagerURL() != null
				&& JEConfiguration.getRuntimeManagerURL() != null && JEConfiguration.getSubscriberPort() != 0
				&& JEConfiguration.getRequestPort() != 0);

	}

	public void updateAll(ConfigModel config) throws JERunnerErrorException, InterruptedException, ExecutionException {
		updateBuilderSettings(config);
		updateRunnerSettings(config);
		configRepository.save(JEConfiguration.getInstance());

	}

	public void updateRunner(ConfigModel config) {

		new Thread(() -> {
			try {
				boolean serverUp = false;
				while (!serverUp) {
					JELogger.info(getClass(), "runner down");
					Thread.sleep(2000);
					serverUp = checkRunnerHealth();
				}

				updateRunnerSettings(config);

				for (JEClass clazz : classService.getLoadedClasses().values()) {
					classService.addClassToJeRunner(clazz);
				}

				JELogger.info(ProjectService.class, "Runner is up, updating now");
				projectService.resetProjects();
			} catch (Exception e) {
				JEExceptionHandler.handleException(e);
			}
		}).start();

	}

	private boolean checkRunnerHealth() {
		try {
			runnerStatus = JERunnerAPIHandler.checkRunnerHealth();
		} catch (InterruptedException | JERunnerErrorException | ExecutionException | IOException e) {
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

	public void setDataDefinitionURL(String dataDefinitionURL) {
		JEConfiguration.setDataDefinitionURL(dataDefinitionURL);
		configRepository.save(JEConfiguration.getInstance());

	}

	public void setDataManagerURL(String dataManagerURL) {
		JEConfiguration.setDataManagerURL(dataManagerURL);
		configRepository.save(JEConfiguration.getInstance());

	}

	public void setRuntimeManagerURL(String runtimeManagerURL) {
		JEConfiguration.setRuntimeManagerURL(runtimeManagerURL);
		JERunnerAPIHandler.setRuntimeManagerBaseApi(runtimeManagerURL);
		configRepository.save(JEConfiguration.getInstance());

	}

	public void setSubscriberPort(int subscriberPort) {
		JEConfiguration.setSubscriberPort(subscriberPort);
		configRepository.save(JEConfiguration.getInstance());

	}

	public void setRequestPort(int requestPort) {
		JEConfiguration.setRequestPort(requestPort);
		configRepository.save(JEConfiguration.getInstance());

	}

}
