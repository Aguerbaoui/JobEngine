package io.je.classbuilder.builder;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;

import io.je.classbuilder.entity.JEClass;
import io.je.classbuilder.models.ClassModel;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.constants.ClassBuilderErrors;
import io.je.utilities.exceptions.AddClassException;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.DataDefinitionUnreachableException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.Network;

public class ClassManager {
	
	static Map<String,Class<?>> builtClasses = new ConcurrentHashMap<>(); //key = id , value = class
	static Map <String, String > classNames =  new ConcurrentHashMap<>(); //key = name, value = classid
    static ClassLoader classLoader = ClassManager.class.getClassLoader();
    static String loadPath = System.getProperty("java.class.path").split(";")[0];

	
	/*
	 * build class (generate .java then load Class )
	 */
	public static List<JEClass> buildClass(String workspaceId ,String classId,String generationPath) throws AddClassException, DataDefinitionUnreachableException, IOException, ClassLoadException
	{
		ArrayList<JEClass> classes = new ArrayList<>();
		ClassModel classModel = loadClassDefinition(workspaceId, classId);
				//load inherited classes
				if(classModel.getBaseTypes()!=null && !classModel.getBaseTypes().isEmpty())
				{
					for(String baseTypeId : classModel.getBaseTypes())
					{
						if(!classNames.containsKey(baseTypeId))
						{
							for ( JEClass _class : ClassManager.buildClass(workspaceId,baseTypeId,generationPath) )
							{
								classes.add(_class);
							}
						}
					}
				}
				
				//load dependent classes
				if(classModel.getDependentEntities()!=null && !classModel.getDependentEntities().isEmpty())
				{
					for(String baseTypeId : classModel.getDependentEntities())
					{
						if(!classNames.containsKey(baseTypeId))
						{
							for ( JEClass _class : ClassManager.buildClass(workspaceId,baseTypeId,generationPath) )
							{
								classes.add(_class);
							}
						}
					}
				}
				
				//create .java
				String filePath = ClassBuilder.buildClass(classModel, generationPath);
				JELogger.info(ClassManager.class, " class built");
				JEClassLoader.loadClass(filePath, loadPath);
				//load class
				  // Load the target class using its binary name
				Class<?> loadedClass;
		        try {
					 loadedClass = classLoader.loadClass(ClassBuilderConfig.genrationPackageName+"."+classModel.getName());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					throw new ClassLoadException(""); //TODO add error msg
				}
		        builtClasses.put(classId, loadedClass); 
		        classNames.put(classModel.getName(), classId);
		        
		        classes.add( new JEClass(classId,classModel.getName(),filePath));
				return classes;
				
	}
	
	
	/*
	 * load class definition from data definition API
	 */
	public static ClassModel loadClassDefinition(String workspaceId,String classId) throws IOException, DataDefinitionUnreachableException
	{
		ObjectMapper objectMapper = new ObjectMapper();
		String requestURL = APIConstants.CLASS_DEFINITION_API + "/Class/" + classId + "/workspace/" + workspaceId;
		Response resp = null;
		try
		{
			resp = Network.makeNetworkCallWithResponse(requestURL );
			
		}catch(ConnectException e )
		{
			throw new DataDefinitionUnreachableException(ClassBuilderErrors.dataDefinitonUnreachable);
		}
		if(resp == null || resp.code() == 404 )
		{
			throw new DataDefinitionUnreachableException(ClassBuilderErrors.dataDefinitonUnreachable);

		}
		String resp1 = resp.body().string();
		
		JELogger.info( ClassManager.class, " loaded definition : " + resp1);

		// create class model from response
		ClassModel jeClass = objectMapper.readValue(resp1, ClassModel.class);

		// set workspace id
		jeClass.setWorkspaceId(workspaceId);

		return jeClass;
	}
	
	public static boolean classExistsByName(String className)
	{
		return classNames.containsKey(className);
	}
	
	public static Class getClassByName(String className)
	{
		return builtClasses.get(classNames.get(className));
	}

}
