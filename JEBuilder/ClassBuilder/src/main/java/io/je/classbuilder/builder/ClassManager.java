package io.je.classbuilder.builder;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;

import io.je.classbuilder.entity.ClassType;
import io.je.classbuilder.entity.JEClass;
import io.je.classbuilder.models.ClassModel;
import io.je.utilities.apis.DataDefinitionApiHandler;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.constants.ClassBuilderErrors;
import io.je.utilities.constants.JEGlobalconfig;
import io.je.utilities.exceptions.AddClassException;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.DataDefinitionUnreachableException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.Network;

public class ClassManager {

	static Map<String, JEClass> jeClasses = new ConcurrentHashMap<>(); // key = is, value = jeclass
	static Map<String, Class<?>> builtClasses = new ConcurrentHashMap<>(); // key = id , value = class
	
	//TODO: see with islem if possible to change field type to class id instead of name
	static Map<String, String> classNames = new ConcurrentHashMap<>(); // key = name, value = classid

	static ClassLoader classLoader = ClassManager.class.getClassLoader();
	static String loadPath = JEGlobalconfig.builderClassLoadPath;
	static String generationPath = JEGlobalconfig.classGenerationPath;

	/*
	 * build class (generate .java then load Class )
	 */
	public static List<JEClass> buildClass(String workspaceId, String classId)
			throws AddClassException, DataDefinitionUnreachableException, IOException, ClassLoadException {
		ArrayList<JEClass> classes = new ArrayList<>();

		// load class definition from data model rest api
		ClassModel classModel = loadClassDefinition(workspaceId, classId);

		// load class type
		ClassType classType = getClassType(classModel);

		// load inherited classes
		if (classModel.getBaseTypes() != null && !classModel.getBaseTypes().isEmpty()) {
			for (String baseTypeId : classModel.getBaseTypes()) {
			
				//load inherited class
				//if (!jeClasses.containsKey(baseTypeId)) {
					for (JEClass _class : ClassManager.buildClass(workspaceId, baseTypeId)) {
						classes.add(_class);

					//}
				}
			}
			
		
		}

		// load dependent classes
		if (classModel.getDependentEntities() != null && !classModel.getDependentEntities().isEmpty()) {
			for (String baseTypeId : classModel.getDependentEntities()) {
			//	if (!jeClasses.containsKey(baseTypeId)) {
					for (JEClass _class : ClassManager.buildClass(workspaceId, baseTypeId)) {
						classes.add(_class);
				//	}
				}
			}
		}

		// create .java
		String filePath = ClassBuilder.buildClass(classModel, generationPath);
		JELogger.info(ClassManager.class, " Class ["+classModel.getName()+"] built");
		JEClassLoader.loadClass(filePath, loadPath);
		// load class
		// Load the target class using its binary name
		Class<?> loadedClass;
		try {
			loadedClass = classLoader.loadClass(ClassBuilderConfig.genrationPackageName + "." + classModel.getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ClassLoadException("Failed to load class ["+classModel.getName()+"]" + e.getMessage()); // TODO add error msg
		}
		builtClasses.put(classId, loadedClass);
		classNames.put(classModel.getName(), classModel.get_id());
		JEClass jeClass = new JEClass(workspaceId,classId, classModel.getName(), filePath, classType);
		jeClasses.put(classModel.get_id(), jeClass);
		classes.add(jeClass);
		return classes;

	}

	/*
	 * load class definition from data definition API
	 */
	public static ClassModel loadClassDefinition(String workspaceId, String classId)
			throws IOException, DataDefinitionUnreachableException, ClassLoadException {
		ObjectMapper objectMapper = new ObjectMapper();
	
		String response = DataDefinitionApiHandler.loadClassDefinition(workspaceId, classId);
		// create class model from response
		ClassModel jeClass = objectMapper.readValue(response, ClassModel.class);

		// set workspace id
		jeClass.setWorkspaceId(workspaceId);

		return jeClass;
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
	private static ClassType getClassType(ClassModel classModel) throws ClassLoadException {
		if (classModel.getIsClass()) {
			if (classModel.getIsInterface() || classModel.getIsEnum()) {
				throw new ClassLoadException("[" + classModel.getName() + "]:" + ClassBuilderErrors.invalidClassFormat
						+ "\n" + ClassBuilderErrors.classTypeUnclear);
			} else {
				return ClassType.CLASS;
			}
		} else if (classModel.getIsInterface()) {
			if (classModel.getIsClass() || classModel.getIsEnum()) {
				throw new ClassLoadException("[" + classModel.getName() + "]:" + ClassBuilderErrors.invalidClassFormat
						+ "\n" + ClassBuilderErrors.classTypeUnclear);
			} else {
				return ClassType.INTERFACE;
			}
		} else if (classModel.getIsEnum()) {
			if (classModel.getIsInterface() || classModel.getIsClass()) {
				throw new ClassLoadException("[" + classModel.getName() + "]:" + ClassBuilderErrors.invalidClassFormat
						+ "\n" + ClassBuilderErrors.classTypeUnclear);
			} else {
				return ClassType.ENUM;
			}
		} else {
			throw new ClassLoadException("[" + classModel.getName() + "]:" + ClassBuilderErrors.invalidClassFormat
					+ "\n" + ClassBuilderErrors.classTypeUnclear);

		}

	}

	public static ClassType getClassType(String classId) {
		return jeClasses.get(classId).getClassType();
		
	}

	public static Class<?> getClassById(String id) {
		return  builtClasses.get(id);
	}

}
