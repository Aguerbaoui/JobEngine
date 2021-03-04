package io.je.utilities.classloader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.logger.JELogger;

public class ClassManager {
	
	static Map<String,Class<?>> loadedClasses = new ConcurrentHashMap<>();
	static String classLoadPath = ConfigurationConstants.runnerClassLoadPath;
	  // Create a new JavaClassLoader 
    static ClassLoader classLoader = ClassManager.class.getClassLoader();
	
	
	/*
	 * load class
	 */
	public static void loadClass(String classId,String className,  String classPath) throws ClassLoadException
	{
		if(!loadedClasses.containsKey(classId)) {
			//create .class file
			JEClassLoader.loadClass(classPath, classLoadPath);

			// Load the target class using its binary name
			Class<?> loadedClass;
			JELogger.trace(" Loading class in runner id = " + classId + " class name = " + className + " class path = " + classPath);
			try {
				loadedClass = classLoader.loadClass(ClassBuilderConfig.genrationPackageName + "." + className);
			} catch (ClassNotFoundException e) {
				throw new ClassLoadException(""); //TODO add error msg
			}
			loadedClasses.put(classId, loadedClass);
		}

	}
	
	/*
	 * retrieve class by id
	 */
	public static Class<?> getClassById(String classId){
		return loadedClasses.get(classId);
	}

}
