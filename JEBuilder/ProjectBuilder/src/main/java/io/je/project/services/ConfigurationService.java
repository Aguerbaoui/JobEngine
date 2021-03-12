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
import io.je.utilities.exceptions.ConfigException;
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

	static boolean isConfiguredProperly = false;

	/*
	 * init configuration : > load config from database >update config
	 */
	public void init()
			throws JERunnerErrorException, InterruptedException, ExecutionException, DataDefinitionUnreachableException,
			AddClassException, ClassLoadException, IOException, ProjectNotFoundException, ConfigException {

		JELogger.trace(" Initializing builder");
		ConfigModel configModel = loadConfigFromDb();
		if (configModel != null) {
			updateBuilderSettings(configModel);
			if (isConfiguredProperly) {
				classService.loadAllClassesToBuilder();
				projectService.loadAllProjects();
				updateRunner(configModel);
			} else {
				JELogger.warning(ConfigurationService.class, Errors.MISSING_CONFIG);

			}
		}

	}

	/*
	 * update runner config
	 */
	private void updateBuilderSettings(ConfigModel configModel) {
		JEConfiguration.updateConfig(configModel);
		if (applicationIsConfiguredProperly()) {
			ConfigurationService.setConfiguredProperly(true);
		}

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

	public static void checkConfig() throws ConfigException {
		if (!isConfiguredProperly) {
			JELogger.error(ConfigurationService.class, Errors.MISSING_CONFIG);
			throw new ConfigException(Errors.MISSING_CONFIG);
		}
	}

	/*
	 * check that there is no missing config
	 */
	public boolean applicationIsConfiguredProperly() {
		boolean configurationIsValid = true;
		if (JEConfiguration.getDataDefinitionURL() == null) {
			configurationIsValid = false;
			JELogger.warning(getClass(), "Data definition URL is missing");
		}
		if (JEConfiguration.getDataManagerURL() == null) {
			configurationIsValid = false;
			JELogger.warning(getClass(), "Data Manager URL is missing");
		}
		if (JEConfiguration.getRuntimeManagerURL() == null) {
			configurationIsValid = false;
			JELogger.warning(getClass(), "JERunner URL is missing");
		}
		if (JEConfiguration.getSubscriberPort() == 0) {
			configurationIsValid = false;
			JELogger.warning(getClass(), "Subscriber port is missing");
		}
		if (JEConfiguration.getRequestPort() == 0) {
			configurationIsValid = false;
			JELogger.warning(getClass(), "Request port is missing");
		}
		if (JEConfiguration.getDroolsDateFormat() == null) {
			configurationIsValid = false;
			JELogger.warning(getClass(), "Drools date format is not specified");
		}

		return configurationIsValid;

	}

	public void updateAll(ConfigModel config) throws JERunnerErrorException, InterruptedException, ExecutionException {
		JELogger.trace(" Updating builder and runner configuration");
		updateBuilderSettings(config);
		updateRunnerSettings(config);
		configRepository.save(JEConfiguration.getInstance());

	}

	public void updateRunner(ConfigModel config) {

		JELogger.trace(" Updating runner configuration, config = " + config.toString());
		new Thread(() -> {
			try {
				boolean serverUp = false;
				while (!serverUp) {
					JELogger.debug(getClass(), " Runner is down, checking again in 5 seconds");
					Thread.sleep(5000);
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
		JELogger.trace(" Setting data definition url from controller url = " + dataDefinitionURL);
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

	public static boolean isConfiguredProperly() {
		return isConfiguredProperly;
	}

	public static void setConfiguredProperly(boolean isConfiguredProperly) {
		ConfigurationService.isConfiguredProperly = isConfiguredProperly;
	}

}
