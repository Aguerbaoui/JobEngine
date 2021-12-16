package io.je.utilities.execution;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.project.variables.VariableManager;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.JavaCodeInjectionError;
import io.je.utilities.exceptions.VariableNotFoundException;
import io.je.utilities.instances.ClassRepository;
import io.je.utilities.instances.InstanceManager;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;

public class Executioner {

	static final int MAX_THREAD_COUNT = 100;
	public static ObjectMapper objectMapper = new ObjectMapper();
	static ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_COUNT);

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
			String instanceId, String attributeName, Object value) {
		// Rework to use a callable for exception handling

		try {
			executor.submit(() -> {
				InstanceManager.writeToDataModelInstance(instanceId, attributeName, value);
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
			String instanceId, String attributeName, String variableId) {

		try {
			executor.submit(() -> {
				Object attribueValue;
				try {
					attribueValue = VariableManager.getVariableValue(projectId, variableId);
					InstanceManager.writeToDataModelInstance(instanceId, attributeName, attribueValue);

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
			String destinationAttributeName) {

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
							attribueValue);

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
			String blockName) {

		try {
			executor.submit(() -> {
				try {
					JERunnerAPIHandler.writeVariableValue(projectId, variableId, value);
				} catch (JERunnerErrorException e) {
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
			String destinationVariableId, String blockName) {

		try {
			executor.submit(() -> {
				try {
					JERunnerAPIHandler.writeVariableValue(projectId, destinationVariableId,
							VariableManager.getVariableValue(projectId, sourceVariableId));
				} catch (JERunnerErrorException | VariableNotFoundException e) {
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
			String sourceInstanceId, String sourceAttributeName, String blockName) {

		try {
			executor.submit(() -> {
				try {
					Object attribueValue = InstanceManager.getAttributeValue(sourceInstanceId, sourceAttributeName);
					JERunnerAPIHandler.writeVariableValue(projectId, destinationVariableId, attribueValue);
				} catch (JERunnerErrorException e) {
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

	public static void executeScript(String name, String processId, String projectId, int timeout)
			throws JavaCodeInjectionError, ClassNotFoundException {
		// JEClassLoader.overrideInstance(ClassBuilderConfig.generationPackageName +"."
		// + name);
		Class<?> loadClass = null;
		try {
			loadClass = ClassRepository
					.getClassByName(name); /*
											 * JEClassLoader.getInstance().loadClass(ClassBuilderConfig.
											 * generationPackageName +"." + name);
											 */
			Method method = loadClass.getDeclaredMethods()[0];
			method.invoke(null);
		} catch (InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}

}
