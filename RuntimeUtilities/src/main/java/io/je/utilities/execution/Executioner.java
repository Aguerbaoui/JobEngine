package io.je.utilities.execution;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.project.variables.VariableManager;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.apis.JERunnerRequester;
import io.je.utilities.beans.JEVariable;
import io.je.utilities.beans.JEZMQResponse;
import io.je.utilities.beans.ZMQResponseType;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.VariableNotFoundException;
import io.je.utilities.instances.ClassRepository;
import io.je.utilities.instances.InstanceManager;
import io.je.utilities.log.JELogger;
import utils.ProcessRunner;
import utils.log.LogCategory;
import utils.log.LogSubModule;

public class Executioner {

	static final int MAX_THREAD_COUNT = 100;
	public static ObjectMapper objectMapper = new ObjectMapper();
	static ExecutorService executor = Executors.newCachedThreadPool();
	static int test = 0;
	private Executioner() {
	}

	/***************************************
	 * INFORM
	 *********************************************************/

	/*
	 * Execute inform block
	 */
	public static void informRuleBlock(String projectId, String ruleId, String message, String logDate,
			String blockName) {
		try {
			executor.submit(() -> {
				JELogger.info(message, LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId, blockName);
			});
		} catch (Exception e) {
			JELogger.error(JEMessages.INFORM_BLOCK_ERROR, LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId);

		}

	}

	/***************************************************
	 * SETTERS
	 *******************************************************************/
	/***************************************
	 * SET DATA MODEL INSTANCE VALUE
	 *********************************************************/
	/***** SET FROM STATIC VALUE *****/
	public static void updateInstanceAttributeValueFromStaticValue(String projectId, String ruleId, String blockName,
			String instanceId, String attributeName, Object value,boolean ignoreSameValue) {
		// Rework to use a callable for exception handling

		try {
			executor.submit(() -> {
				InstanceManager.writeToDataModelInstance(instanceId, attributeName, value,ignoreSameValue);
			});
		} catch (Exception e) {
			JELogger.error(JEMessages.WRITE_INSTANCE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
					LogSubModule.RULE, ruleId, blockName);
		}
	}

	/***** SET FROM VARIABLE *****/
	/*
	 * update instance attribute from variable
	 */
	public static void updateInstanceAttributeValueFromVariable(String projectId, String ruleId, String blockName,
			String instanceId, String attributeName, String variableId,boolean ignoreSameValue) {

		try {
			executor.submit(() -> {
				Object attribueValue;
				try {
					attribueValue = VariableManager.getVariableValue(projectId, variableId);
					InstanceManager.writeToDataModelInstance(instanceId, attributeName, attribueValue, ignoreSameValue);

				} catch (VariableNotFoundException e) {
					JELogger.error(JEMessages.WRITE_INSTANCE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
							LogSubModule.RULE, ruleId, blockName);
				}
			});
		} catch (Exception e) {
			JELogger.error(JEMessages.WRITE_INSTANCE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
					LogSubModule.RULE, ruleId, blockName);
		}

	}

	/***** SET FROM ANOTHER DATAMODEL INSTANCE *****/
	public static void updateInstanceAttributeValueFromAnotherInstance(String projectId, String ruleId,
			String blockName, String sourceInstanceId, String sourceAttributeName, String destinationInstanceId,
			String destinationAttributeName,boolean ignoreSameValue) {

		try {
			executor.submit(() -> {
				try {
					Object attribueValue = InstanceManager.getAttributeValue(sourceInstanceId, sourceAttributeName);
					if (attribueValue == null) {
						JELogger.error(JEMessages.READ_INSTANCE_VALUE_FAILED, LogCategory.RUNTIME, projectId,
								LogSubModule.RULE, ruleId, blockName);
						return;
					}

					InstanceManager.writeToDataModelInstance(destinationInstanceId, destinationAttributeName,
							attribueValue, ignoreSameValue);

				} catch (Exception e) {
					JELogger.error(JEMessages.WRITE_INSTANCE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
							LogSubModule.RULE, ruleId, blockName);
				}
			});
		} catch (Exception e) {
			JELogger.error(JEMessages.WRITE_INSTANCE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
					LogSubModule.RULE, ruleId, blockName);
		}

	}

	/***************************************
	 * SET VARIABLE VALUE
	 *********************************************************/
	/***** SET FROM STATIC VALUE *****/
	/*
	 * update Variable from a static value
	 */
	public static void updateVariableValue(String projectId, String ruleId, String variableId, Object value,
			String blockName,boolean ignoreIfSameValue) {

		try {
			executor.submit(() -> {
				try {
					JERunnerRequester.updateVariable(projectId, variableId, value,ignoreIfSameValue);
				} catch (Exception e) {
					JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
							LogSubModule.RULE, ruleId, blockName);
				}
			});
		} catch (Exception e) {
			JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
					LogSubModule.RULE, ruleId, blockName);
		}

	}

	/***** SET FROM VARIABLE *****/

	/*
	 * update Variable from another variable
	 */
	public static void updateVariableValueFromAnotherVariable(String projectId, String ruleId, String sourceVariableId,
			String destinationVariableId, String blockName,boolean ignoreIfSameValue) {

		try {
			executor.submit(() -> {
				try {
					JERunnerRequester.updateVariable(projectId, destinationVariableId,
							VariableManager.getVariableValue(projectId, sourceVariableId),ignoreIfSameValue);
				} catch (Exception e) {
					JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
							LogSubModule.RULE, ruleId, blockName);
				}
			});
		} catch (Exception e) {
			JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
					LogSubModule.RULE, ruleId, blockName);
		}

	}

	/***** SET FROM ANOTHER DATAMODEL INSTANCE *****/

	/*
	 * update Variable from a data model instance
	 */
	public static void updateVariableValueFromDataModel(String projectId, String ruleId, String destinationVariableId,
			String sourceInstanceId, String sourceAttributeName, String blockName,boolean ignoreIfSameValue) {

		try {
			executor.submit(() -> {
				try {
					Object attribueValue =  InstanceManager.getAttributeValue(sourceInstanceId, sourceAttributeName);
					if(attribueValue!=null)
					{
						JEZMQResponse response = JERunnerRequester.updateVariable(projectId, destinationVariableId, attribueValue, ignoreIfSameValue);
						JEZMQResponse response1 = JERunnerRequester.readVariable (projectId, destinationVariableId);

						if(response.getResponse()!=ZMQResponseType.SUCCESS)
						{
							JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED + response.getErrorMessage() , LogCategory.RUNTIME, projectId,
									LogSubModule.RULE, ruleId, blockName);
						}
					}else {
						JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED  , LogCategory.RUNTIME, projectId,
								LogSubModule.RULE, ruleId, blockName);
					}

				} catch (Exception e) {
					JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
							LogSubModule.RULE, ruleId, blockName);
				}
			});
		} catch (Exception e) {
			JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
					LogSubModule.RULE, ruleId, blockName);
		}

	}

	/***************************************
	 * EVENT
	 *********************************************************/

	/*
	 * trigger an event + send event data to logging system
	 */
	public static void triggerEvent(String projectId, String eventId, String eventName, String ruleId,
			String triggerSource) {

		executor.submit(() -> {
			try {
				JERunnerAPIHandler.triggerEvent(eventId, projectId);
			} catch (JERunnerErrorException e) {
				JELogger.error("Failed to trigger event", LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId,
						triggerSource);

			}
		});

	}
	public static String getCurrentClassPath() {
		StringBuilder sb = new StringBuilder();
		sb.append(File.pathSeparator);
		URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
		for (URL url : urlClassLoader.getURLs()){
			//JELogger.info(JEClassLoader.class, url.getFile().substring(1));
			sb.append(url.getFile().substring(1).replace("%20", " ")).append(File.pathSeparator);
		}
		return sb.toString().replace("/", "\\");
	}
	public static Thread executeScript(String filePath) throws IOException, InterruptedException {
		//String classpathFolder = System.getenv(ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE) + "\\..\\Job Engine\\libs\\*";
		//String command = "java" + " " + "-cp" + " \"" + classpathFolder  + getCurrentClassPath() + "\" " + filePath;
		return CommandExecutioner.runCode(filePath);
		//return ProcessRunner.executeCommandWithPidOutput(command);

		//long pid = ProcessRunner.executeCommandWithPidOutput(command);

	}

	public static void executeScript(String name, String processId, String projectId, int timeout){
		Class<?> loadClass = null;
		try {
			loadClass = ClassRepository
					.getClassByName(name); 
			Method method = loadClass.getDeclaredMethods()[0];
			method.invoke(null);
		} catch (InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}

}
