package io.je.classbuilder.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.classbuilder.entity.ClassType;
import io.je.classbuilder.entity.JEClass;
import io.je.classbuilder.models.ClassModel;
import io.je.utilities.apis.DataDefinitionApiHandler;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.constants.ClassBuilderErrors;
import io.je.utilities.exceptions.AddClassException;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.DataDefinitionUnreachableException;
import io.je.utilities.logger.JELogger;


/*
 * Class to manage user defined Models 
 */
public class ClassManager {

	static Map<String, JEClass> jeClasses = new ConcurrentHashMap<>(); // key = is, value = jeclass
	static Map<String, Class<?>> builtClasses = new ConcurrentHashMap<>(); // key = id , value = class
	
	//TODO: see with islem if possible to change field type to class id instead of name
	static Map<String, String> classNames = new ConcurrentHashMap<>(); // key = name, value = classid
	static ClassLoader classLoader = ClassManager.class.getClassLoader();
	static String loadPath =  ConfigurationConstants.builderClassLoadPath;
	static String generationPath = ConfigurationConstants.classGenerationPath;

	/*
	 * build class (generate .java then load Class )
	 */
	public static List<JEClass> buildClass(ClassModel classModel)
			throws AddClassException, DataDefinitionUnreachableException, IOException, ClassLoadException {
		
		JELogger.debug(ClassManager.class, "building Class [className = "+classModel.getName()+"]");
		ArrayList<JEClass> classes = new ArrayList<>();
		
		if(!builtClasses.containsKey(classModel.getIdClass()))
		{
			// load class type ( interface/enum or class)
			ClassType classType = getClassType(classModel);

			// build inherited classes
			if (classModel.getBaseTypes() != null && !classModel.getBaseTypes().isEmpty()) {
				for (String baseTypeId : classModel.getBaseTypes()) {
					ClassModel inheritedClassModel = loadClassDefinition(classModel.getWorkspaceId(), baseTypeId);
					//load inherited class
						for (JEClass _class : ClassManager.buildClass(inheritedClassModel)) {
							classes.add(_class);
					}
				}
				
			
			}

			// build dependent classes
			if (classModel.getDependentEntities() != null && !classModel.getDependentEntities().isEmpty()) {
				for (String baseTypeId : classModel.getDependentEntities()) {
					ClassModel dependentClass = loadClassDefinition(classModel.getWorkspaceId(), baseTypeId);
						for (JEClass _class : ClassManager.buildClass(dependentClass)) {
							classes.add(_class);
					}
				}
			}

			// create .java
			String filePath = ClassBuilder.buildClass(classModel, generationPath);
			
			// load .java -> .class
			JEClassLoader.loadClass(filePath, loadPath);
			
			// Load the target class using its binary name
			Class<?> loadedClass;
			try {
				loadedClass = classLoader.loadClass(ClassBuilderConfig.genrationPackageName + "." + classModel.getName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new ClassLoadException("Failed to load class ["+classModel.getName()+"]" + e.getMessage()); // TODO add error msg
			}
			builtClasses.put(classModel.getIdClass(), loadedClass);
			classNames.put(classModel.getName(), classModel.getIdClass());
			JEClass jeClass = new JEClass(classModel.getWorkspaceId(),classModel.getIdClass(), classModel.getName(), filePath, classType);
			jeClasses.put(classModel.getIdClass(), jeClass);
			classes.add(jeClass);
		}
	
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
