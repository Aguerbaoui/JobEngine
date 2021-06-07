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
import io.je.utilities.constants.JEMessages;
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

		JELogger.trace(JEMessages.INITILIZING_BUILDER);
		ConfigModel configModel = loadConfigFromDb();
		if (configModel != null) {
			updateBuilderSettings(configModel);
			if (isConfiguredProperly) {
				classService.loadAllClassesToBuilder();
				projectService.loadAllProjects();
				updateRunner(configModel);
			} else {
				JELogger.warning(ConfigurationService.class, JEMessages.MISSING_CONFIG);
				

			}
		}else {
			configRepository.save(new ConfigModel());
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
		configRepository.save(JEConfiguration.getInstance());


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
			JELogger.warning(getClass(), JEMessages.MISSING_CONFIG);
		}
		if (config.isPresent()) {
			return config.get();
		}
		return null;
	}

	public static void checkConfig() throws ConfigException {
		if (!isConfiguredProperly) {
			JELogger.error(ConfigurationService.class, JEMessages.MISSING_CONFIG);
			throw new ConfigException(JEMessages.MISSING_CONFIG);
		}
	}

	/*
	 * check that there is no missing config
	 */
	public boolean applicationIsConfiguredProperly() {
		boolean configurationIsValid = true;
		if (JEConfiguration.getDataDefinitionURL() == null || JEConfiguration.getDataDefinitionURL().isEmpty()) {
			configurationIsValid = false;
			JEConfiguration.setDataDefinitionURL("");
			JELogger.warning(getClass(), JEMessages.DATA_DEFINITION_URL_MISSING);
		}
		if (JEConfiguration.getDataManagerURL() == null || JEConfiguration.getDataManagerURL().isEmpty()) {
			configurationIsValid = false;
			JEConfiguration.setDataManagerURL("");
			JELogger.warning(getClass(), JEMessages.DATA_MODEL_URL_MISSING);
		}
		if (JEConfiguration.getRuntimeManagerURL() == null || JEConfiguration.getRuntimeManagerURL().isEmpty()) {
			configurationIsValid = false;
			JEConfiguration.setRuntimeManagerURL("");
			JELogger.warning(getClass(), JEMessages.JERUNNER_URL_MISSING);
		}
		if (JEConfiguration.getSubscriberPort() == 0) {
			configurationIsValid = false;
			JELogger.warning(getClass(), JEMessages.DATA_MODEL_SUB_PORT_MISSING);
		}
		if (JEConfiguration.getRequestPort() == 0) {
			configurationIsValid = false;
			JELogger.warning(getClass(), JEMessages.DATA_MODEL_REQ_PORT_MISSING);
		}
		if (JEConfiguration.getDroolsDateFormat() == null || JEConfiguration.getDroolsDateFormat().isEmpty()) {
			configurationIsValid = false;
			JEConfiguration.setDroolsDateFormat("");
			JELogger.warning(getClass(), JEMessages.DROOLS_DATE_FORMAT_MISSING);
		}
		if (JEConfiguration.getDataModelDateFormat() == null || JEConfiguration.getDataModelDateFormat().isEmpty()) {
			configurationIsValid = false;
			JEConfiguration.setDataModelDateFormat("");
			JELogger.warning(getClass(), JEMessages.DATA_MODEL_DATE_FORMAT_MISSING);
		}
		if(JEConfiguration.getLoggingSystemURL()==null || JEConfiguration.getLoggingSystemURL().isEmpty())
		{
			configurationIsValid = false;
			JEConfiguration.setLoggingSystemURL("");
			JELogger.warning(getClass(), JEMessages.LOGGING_SYSTEM_URL_MISSING);
		}
		if(JEConfiguration.getLoggingSystemZmqPublishPort()==0)
		{
			configurationIsValid = false;
			JELogger.warning(getClass(), JEMessages.LOGGING_SYSTEM_PORT_MISSING);
		}
		if(JEConfiguration.getEmailApiUrl()==null || JEConfiguration.getEmailApiUrl().isEmpty())
		{
			configurationIsValid=false;
			JEConfiguration.setEmailApiUrl("");
			JELogger.warning(getClass(), JEMessages.EMAIL_API_URL_MISSING);

		}
		return configurationIsValid;

	}

	public void updateAll(ConfigModel config) throws JERunnerErrorException, InterruptedException, ExecutionException {
		JELogger.trace(" " + JEMessages.UPDATING_BUILDER_AND_RUNNER_CONFIGURATION);
		updateBuilderSettings(config);
		updateRunnerSettings(config);
		configRepository.save(JEConfiguration.getInstance());

	}

	public void updateRunner(ConfigModel config) {

		JELogger.trace(" " + JEMessages.UPDATING_RUNNER_CONFIGURATION_CONFIG + " = " + config.toString());
		new Thread(() -> {
			try {
				boolean serverUp = false;
				while (!serverUp) {
					JELogger.debug(getClass(), " " + JEMessages.RUNNER_IS_DOWN_CHECKING_AGAIN_IN_5_SECONDS);
					Thread.sleep(5000);
					serverUp = checkRunnerHealth();
				}

				updateRunnerSettings(config);

				for (JEClass clazz : classService.getLoadedClasses().values()) {
					classService.addClassToJeRunner(clazz);
				}

				JELogger.info(ProjectService.class, JEMessages.RUNNER_IS_UP_UPDATING_NOW);
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
		JELogger.trace(" " + JEMessages.SETTING_DATA_DEFINITION_URL_FROM_CONTROLLER + " url = " + dataDefinitionURL);
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
