package io.je.classbuilder.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.classbuilder.entity.ClassType;
import io.je.classbuilder.entity.JEClass;
import io.je.classbuilder.models.ClassDefinition;
import io.je.classbuilder.models.GetModelObject;
import io.je.utilities.classloader.JEClassCompiler;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.config.Utility;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.AddClassException;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.DataDefinitionUnreachableException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogMessage;
import io.je.utilities.logger.LogSubModule;
import io.je.utilities.zmq.ZMQPublisher;
import io.je.utilities.zmq.ZMQRequester;

/*
 * Class to manage user defined Models 
 */
public class ClassManager {

	static Map<String, JEClass> jeClasses = new ConcurrentHashMap<>(); // key = is, value = jeclass
	static Map<String, Class<?>> builtClasses = new ConcurrentHashMap<>(); // key = id , value = class
	static ZMQPublisher publisher = new ZMQPublisher("tcp://"+Utility.getSiothConfig().getMachineCredentials().getIpAddress(),
			Utility.getSiothConfig().getPorts().getLogServicePort());
	static ObjectMapper objectMapper = new ObjectMapper();

	// TODO: see with islem if possible to change field type to class id instead of
	// name
	static Map<String, String> classNames = new ConcurrentHashMap<>(); // key = name, value = classid
	static ClassLoader classLoader = ClassManager.class.getClassLoader();
	static String loadPath = ConfigurationConstants.builderClassLoadPath;
	static String generationPath = ConfigurationConstants.classGenerationPath;

	/*
	 * build class (generate .java then load Class )
	 */
	public static List<JEClass> buildClass(ClassDefinition classDefinition)
			throws AddClassException, DataDefinitionUnreachableException, IOException, ClassLoadException {

		JELogger.debug(JEMessages.BUILDING_CLASS + "[className = " + classDefinition.getName() + "]",
				LogCategory.DESIGN_MODE, null,
				LogSubModule.CLASS,null);
		ArrayList<JEClass> classes = new ArrayList<>();

		/*
		 * if(!builtClasses.containsKey(classModel.getIdClass()) ||
		 * classModel.getWorkspaceId() == null) {
		 */
		// load class type ( interface/enum or class)
		ClassType classType = getClassType(classDefinition);

		// build inherited classes
		if (classDefinition.getBaseTypes() != null && !classDefinition.getBaseTypes().isEmpty()) {
			for (String baseTypeId : classDefinition.getBaseTypes()) {
				ClassDefinition inheritedClassModel = loadClassDefinition(classDefinition.getWorkspaceId(), baseTypeId);
				// load inherited class
				for (JEClass _class : ClassManager.buildClass(inheritedClassModel)) {
					classes.add(_class);
				}
			}

		}

		// build dependent classes
		if (classDefinition.getDependentEntities() != null && !classDefinition.getDependentEntities().isEmpty()) {
			for (String baseTypeId : classDefinition.getDependentEntities()) {
				ClassDefinition dependentClass = loadClassDefinition(classDefinition.getWorkspaceId(), baseTypeId);
				for (JEClass _class : ClassManager.buildClass(dependentClass)) {
					classes.add(_class);
				}
			}
		}

		// create .java
		String filePath = ClassBuilder.buildClass(classDefinition, generationPath);

		// load .java -> .class
		JEClassCompiler.compileClass(filePath, loadPath);

		// Load the target class using its binary name
		Class<?> loadedClass;
		try {
			loadedClass = classLoader
					.loadClass(ClassBuilderConfig.generationPackageName + "." + classDefinition.getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ClassLoadException(
					JEMessages.CLASS_LOAD_FAILED + "[" + classDefinition.getName() + "]" + e.getMessage());
		}
		builtClasses.put(classDefinition.getIdClass(), loadedClass);
		classNames.put(classDefinition.getName(), classDefinition.getIdClass());
		JEClass jeClass = new JEClass(classDefinition.getWorkspaceId(), classDefinition.getIdClass(),
				classDefinition.getName(), filePath, classType);
		jeClasses.put(classDefinition.getIdClass(), jeClass);
		classes.add(jeClass);
		// }

		return classes;

	}

	/*
	 * load class definition from data definition API
	 */
	public static ClassDefinition loadClassDefinition(String workspaceId, String classId)
			throws IOException, DataDefinitionUnreachableException, ClassLoadException {
	
		ClassDefinition jeClass=null;

		String response = requestClassDefinition(workspaceId, classId);
		// create class model from response
		if(response!=null && jeClass!=null)
		{
			 jeClass = objectMapper.readValue(response, ClassDefinition.class);
			 jeClass.setWorkspaceId(workspaceId);

		}

		// set workspace id

		return jeClass;
	}

	
	/*
	 * request class definition from Data Model RESTAPI via ZMQ

	 */
	private static String requestClassDefinition(String workspaceId, String classId) {

		String response=null;
		try {
			GetModelObject request = new GetModelObject(classId, workspaceId);
			String jsonMsg = objectMapper.writeValueAsString(request);
			JELogger.debug(JEMessages.SENDING_REQUEST_TO_DATA_MODEL + " : " + request,
					LogCategory.DESIGN_MODE, null,
					LogSubModule.JEBUILDER,null);
              ZMQRequester requester = new ZMQRequester("tcp://"+Utility.getSiothConfig().getMachineCredentials().getIpAddress(), Utility.getSiothConfig().getDataModelPORTS().getDmRestAPI_ReqAddress());
               response = requester.sendRequest(jsonMsg) ;
              if (response == null ) {
            	  
            	  //Data Model RESTAPI unreachable
				  JELogger.error("Data Model RESTAPI unreachable",
						  LogCategory.DESIGN_MODE, null,
						  LogSubModule.JEBUILDER,null);
                  throw new ClassNotFoundException("Data Model RESTAPI unreachable");
              } else
              if(response.isEmpty())
              {
            	  //Data Model RESTAPI Error -> check its logs for more details
				  JELogger.error(JEMessages.CLASS_NOT_FOUND,
						  LogCategory.DESIGN_MODE, null,
						  LogSubModule.JEBUILDER,null);
                  throw new ClassNotFoundException(JEMessages.CLASS_NOT_FOUND + classId);
              }
              else
              {
				  JELogger.debug("Data Model defintion Returned : " + response,
						  LogCategory.DESIGN_MODE, null,
						  LogSubModule.JEBUILDER,null);
              }

		} catch (Exception e) {
			JELogger.error("Failed to send log message to the logging system : " + e.getMessage(),
					LogCategory.DESIGN_MODE, null,
					LogSubModule.JEBUILDER,null);
		}
		return  response;
	}

	public static boolean classExistsByName(String className) {
		return classNames.containsKey(className);
	}

	public static Class getClassByName(String className) {
		return builtClasses.get(classNames.get(className));
	}

	/*
	 * check if class model is a class, interface or enum
	 */
	private static ClassType getClassType(ClassDefinition classDefinition) throws ClassLoadException {
		if (classDefinition.getIsClass()) {
			if (classDefinition.getIsInterface() || classDefinition.getIsEnum()) {
				throw new ClassLoadException("[" + classDefinition.getName() + "]:" + JEMessages.INVALID_CLASS_FORMAT
						+ "\n" + JEMessages.UNKNOW_CLASS_TYPE);
			} else {
				return ClassType.CLASS;
			}
		} else if (classDefinition.getIsInterface()) {
			if (classDefinition.getIsClass() || classDefinition.getIsEnum()) {
				throw new ClassLoadException("[" + classDefinition.getName() + "]:" + JEMessages.INVALID_CLASS_FORMAT
						+ "\n" + JEMessages.UNKNOW_CLASS_TYPE);
			} else {
				return ClassType.INTERFACE;
			}
		} else if (classDefinition.getIsEnum()) {
			if (classDefinition.getIsInterface() || classDefinition.getIsClass()) {
				throw new ClassLoadException("[" + classDefinition.getName() + "]:" + JEMessages.INVALID_CLASS_FORMAT
						+ "\n" + JEMessages.UNKNOW_CLASS_TYPE);
			} else {
				return ClassType.ENUM;
			}
		} else {
			throw new ClassLoadException("[" + classDefinition.getName() + "]:" + JEMessages.INVALID_CLASS_FORMAT + "\n"
					+ JEMessages.UNKNOW_CLASS_TYPE);

		}

	}

	public static ClassType getClassType(String classId) {
		return jeClasses.get(classId).getClassType();

	}

	public static Class<?> getClassById(String id) {
		return builtClasses.get(id);
	}

}
