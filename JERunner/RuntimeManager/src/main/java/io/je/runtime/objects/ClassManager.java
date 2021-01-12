package io.je.runtime.objects;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.exceptions.ClassLoadException;

public class ClassManager {
	
	static Map<String,Class<?>> loadedClasses = new ConcurrentHashMap<>();
	static String classLoadPath = System.getProperty("java.class.path").split(";")[0]; //TODO : add default value
	  // Create a new JavaClassLoader 
    static ClassLoader classLoader = ClassManager.class.getClassLoader();
	
	
	/*
	 * load class
	 */
	public static void loadClass(String classId,String className,  String classPath) throws ClassLoadException
	{
		//create .class file
		JEClassLoader.loadClass(classPath, classLoadPath);
       
        // Load the target class using its binary name
		Class<?> loadedClass;
        try {
			 loadedClass = classLoader.loadClass(ClassBuilderConfig.genrationPackageName+"."+className);
		} catch (ClassNotFoundException e) {
			throw new ClassLoadException(""); //TODO add error msg
		}
        loadedClasses.put(classId, loadedClass);
		

	}
	
	/*
	 * retrieve class by id
	 */
	public static Class<?> getClassById(String classId){
		return loadedClasses.get(classId);
	}

}
