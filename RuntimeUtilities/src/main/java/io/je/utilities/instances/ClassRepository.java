package io.je.utilities.instances;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.je.utilities.constants.ClassBuilderConfig;


public class ClassRepository {

	//Map of custom classes loaded to runner
	static Map<String,Class<?>> loadedClasses = new ConcurrentHashMap<>();
	static Map<String,String> classIdByName = new ConcurrentHashMap<String, String>();

	private ClassRepository() {
	}

	
	
	public static Set<String> getAllClassNames()
	{
		return classIdByName.keySet();
	}
	
	public static void addClass(String classId, String className, Class<?> classs )
	{
		loadedClasses.put(classId, classs);
		if(className.contains(ClassBuilderConfig.generationPackageName))
		{
			className = className.replaceFirst(ClassBuilderConfig.generationPackageName+".", "");
		}
		classIdByName.put(className, classId);

	}
	
	public static String getClassIdByName(String className)
	{
		if(className.contains(ClassBuilderConfig.generationPackageName))
		{
			className = className.replaceFirst(ClassBuilderConfig.generationPackageName+".", "");
		}
		return classIdByName.getOrDefault(className, "unknown");
	}

	
	
	public static void addClassByName(String className,  Class<?> classs )
	{
		if(classIdByName.containsKey(className) && !classIdByName.get(className).equals("unknown"))
		{
			loadedClasses.put(classIdByName.get(className), classs);
		}
	}
	
	/*
	 * retrieve class by id
	 */
	public static Class<?> getClassById(String classId){
		return loadedClasses.get(classId);
	}
	
	/*
	 * retrieve class by id
	 */
	public static Class<?> getClassByName(String className){
		return loadedClasses.get(classIdByName.get(className));
	}
	
	public static boolean containsClass(String classId)
	{
		return loadedClasses.containsKey(classId);
	}
}
