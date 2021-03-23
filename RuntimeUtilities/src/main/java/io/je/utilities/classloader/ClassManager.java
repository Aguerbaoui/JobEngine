package io.je.utilities.classloader;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.logger.JELogger;
import org.burningwave.core.Virtual;
import org.burningwave.core.classes.ClassSourceGenerator;
import org.burningwave.core.classes.FunctionSourceGenerator;
import org.burningwave.core.classes.TypeDeclarationSourceGenerator;
import org.burningwave.core.classes.UnitSourceGenerator;

public class ClassManager {
	
	static Map<String,Class<?>> loadedClasses = new ConcurrentHashMap<>();
	static String classLoadPath = ConfigurationConstants.runnerClassLoadPath;
	  // Create a new JavaClassLoader 
    static ClassLoader classLoader = ClassManager.class.getClassLoader();

	static String loadPath =  ConfigurationConstants.runnerClassLoadPath;
	static String generationPath = ConfigurationConstants.classGenerationPath;
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
	public static void generateScriptTaskClass(String name, String javaCode) {
		UnitSourceGenerator unitSG = UnitSourceGenerator.create(ClassBuilderConfig.genrationPackageName).addClass(
				ClassSourceGenerator.create(
						TypeDeclarationSourceGenerator.create(name)
				).addModifier(
						Modifier.PUBLIC
				).addMethod(
						FunctionSourceGenerator.create("executeScript")
								.setReturnType(
										TypeDeclarationSourceGenerator.create(void.class)
								)
								.addModifier(Modifier.PUBLIC)
								.addModifier(Modifier.STATIC)
								.addBodyCodeLine(javaCode)
				).addConcretizedType(Virtual.class));
		unitSG.addImport("io.je.utilities.logger.JELogger");
		unitSG.addImport("java.lang.*");
		unitSG.addImport("java.util.*");
		unitSG.addImport("java.sql.*");
		unitSG.addImport("javax.sql.*");

		System.out.println(unitSG.make());
		String filePath= generationPath + "\\" + ClassBuilderConfig.genrationPackageName  + "\\" + name +".java" ;
		File file = new File(generationPath);
		file.delete();
		unitSG.storeToClassPath(generationPath);
		try {
			JEClassLoader.loadClass(filePath, loadPath);
		} catch (ClassLoadException e) {
			e.printStackTrace();
		}
		/*try {
			Class<?> clazz = Class.forName("classes." + name);
			Method method
					= clazz.getDeclaredMethods()[0];
			method.invoke(null);
		} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}*/
	}
}
