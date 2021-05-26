package io.je.project.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.je.utilities.logger.JELogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.je.classbuilder.builder.ClassManager;
import io.je.classbuilder.entity.JEClass;
import io.je.classbuilder.models.ClassModel;
import io.je.project.repository.ClassRepository;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.AddClassException;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.DataDefinitionUnreachableException;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.network.JEResponse;

/*
 * Service class to handle classes
 */
@Service
public class ClassService {

	public static final String CLASS_NAME = "className";
	public static final String CLASS_PATH = "classPath";
	public static final String CLASS_ID = "classId";

	@Autowired
	ClassRepository classRepository;

	Map<String, JEClass> loadedClasses = new HashMap<String, JEClass>();
	

	
	public List<JEClass> addClass(String workspaceId, String classId) throws DataDefinitionUnreachableException, ClassLoadException, IOException, AddClassException, JERunnerErrorException, InterruptedException, ExecutionException {
		ClassModel classModel = ClassManager.loadClassDefinition(workspaceId,classId);
		return addClass(classModel);
	}
	
	public List<JEClass> addClass(ClassModel classModel) throws AddClassException, DataDefinitionUnreachableException, ClassLoadException, IOException, JERunnerErrorException, InterruptedException, ExecutionException
	{
		List<JEClass> builtClasses = ClassManager.buildClass(classModel);
		for (JEClass _class : builtClasses) {
			addClassToJeRunner(_class);
			classRepository.save(_class);
			loadedClasses.put(_class.getClassId(), _class);
		}
		return builtClasses;
		

	}
	
	public List<JEClass> addDBClassesToBuilder(String workspaceId, String classId) throws AddClassException, DataDefinitionUnreachableException, ClassLoadException, IOException, JERunnerErrorException, InterruptedException, ExecutionException
	{
		ClassModel classModel = ClassManager.loadClassDefinition(workspaceId,classId);
		List<JEClass> builtClasses = ClassManager.buildClass(classModel);
		for (JEClass _class : builtClasses) {
			classRepository.save(_class);
			loadedClasses.put(_class.getClassId(), _class);
		}
		return builtClasses;
		

	}


	/*
	 * send class to je runner to be loaded there
	 */
	public void addClassToJeRunner(JEClass clazz) throws AddClassException, JERunnerErrorException, IOException, InterruptedException, ExecutionException {
		HashMap<String, String> classMap = new HashMap<>();
		classMap.put(CLASS_NAME, clazz.getClassName());
		classMap.put(CLASS_PATH, clazz.getClassPath());
		classMap.put(CLASS_ID, clazz.getClassId());
		JELogger.trace(ClassService.class, " " + JEMessages.ADDING_CLASS_TO_RUNNER_FROM_BUILDER_WITH_ID + " = " + clazz.getClassId());
		JEResponse jeRunnerResp = JERunnerAPIHandler.addClass(classMap);
		if (jeRunnerResp.getCode() != ResponseCodes.CODE_OK) {
			throw new AddClassException(JEMessages.CLASS_LOAD_FAILED);
		}

	}


	public void loadAllClassesToBuilder() {
		List<JEClass> classes = classRepository.findAll();
		JELogger.trace(JEMessages.LOADING_ALL_CLASSES_FROM_DB);
		for (JEClass clazz : classes) {
			try
			{
				String classId= clazz.getClassId();
				String workspaceId = clazz.getWorkspaceId();
				if (!loadedClasses.containsKey(classId) && workspaceId != null) {
					List<JEClass> builtClasses =addDBClassesToBuilder(workspaceId, classId);
					for (JEClass _class : builtClasses) {
						loadedClasses.put(_class.getClassId(), _class);
					}
				}

			}catch (Exception e) {
				JELogger.warning(getClass(), JEMessages.FAILED_TO_LOAD_CLASS + " " + clazz.getClassName());
			}
		}

	}
	public Map<String, JEClass> getLoadedClasses() {
		return loadedClasses;
	}

	public void setLoadedClasses(Map<String, JEClass> loadedClasses) {
		this.loadedClasses = loadedClasses;
	}
}
