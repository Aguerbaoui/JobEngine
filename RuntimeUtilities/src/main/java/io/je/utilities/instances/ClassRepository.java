package io.je.utilities.instances;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassRepository {

	//Map of custom classes loaded to runner
	static Map<String,Class<?>> loadedClasses = new ConcurrentHashMap<>();

	private ClassRepository() {
	}

	public static void addClass(String classId,  Class<?> classs )
	{
		loadedClasses.put(classId, classs);
	}
	
	/*
	 * retrieve class by id
	 */
	public static Class<?> getClassById(String classId){
		return loadedClasses.get(classId);
	}
	
	
	public static boolean containsClass(String classId)
	{
		return loadedClasses.containsKey(classId);
	}
}
