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
import io.je.project.repository.ClassRepository;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.constants.ClassBuilderErrors;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.AddClassException;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.DataDefinitionUnreachableException;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.network.JEResponse;
import io.je.utilities.runtimeobject.ClassDefinition;

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


	/*
	 * add/update class
	 */
	public void addClass(ClassDefinition classDefinition) throws IOException, DataDefinitionUnreachableException,
			JERunnerErrorException, AddClassException, ClassLoadException, InterruptedException, ExecutionException {
		String classId= classDefinition.getClassId();
		String workspaceId = classDefinition.getWorkspaceId();
		addClass(workspaceId,classId);

	}
	
	
	/*
	 * add/update class
	 */
	public void addClass(String workspaceId, String classId) throws IOException, DataDefinitionUnreachableException,
			JERunnerErrorException, AddClassException, ClassLoadException, InterruptedException, ExecutionException {
	
		if (!loadedClasses.containsKey(classId)) {
			JELogger.trace(ClassService.class, " Adding class to builder from data definition api with id = " + classId);
			List<JEClass> builtClasses = ClassManager.buildClass(workspaceId, classId);
			for (JEClass _class : builtClasses) {
				// TODO: manage what happens when class addition fails
				addClassToJeRunner(_class);
				classRepository.save(_class);
				loadedClasses.put(_class.getClassId(), _class);
			}
		}

	}



	/*
	 * send class to je runner to be loaded there
	 */
	public void addClassToJeRunner(JEClass clazz) throws AddClassException, JERunnerErrorException, IOException, InterruptedException, ExecutionException {
		HashMap<String, String> classMap = new HashMap<>();
		classMap.put(CLASS_NAME, clazz.getClassName());
		classMap.put(CLASS_PATH, clazz.getClassPath());
		classMap.put(CLASS_ID, clazz.getClassId());
		JELogger.trace(ClassService.class, " Adding class to runner from builder with id = " + clazz.getClassId());
		JEResponse jeRunnerResp = JERunnerAPIHandler.addClass(classMap);
		if (jeRunnerResp.getCode() != ResponseCodes.CODE_OK) {
			throw new AddClassException(ClassBuilderErrors.classLoadFailed);
		}

	}


	public void loadAllClassesToBuilder() {
		List<JEClass> classes = classRepository.findAll();
		JELogger.trace(" Loading all classes from db to memory");
		for (JEClass clazz : classes) {
			try
			{
				String classId= clazz.getClassId();
				String workspaceId = clazz.getWorkspaceId();
				if (!loadedClasses.containsKey(classId)) {
					List<JEClass> builtClasses = ClassManager.buildClass(workspaceId, classId);
					for (JEClass _class : builtClasses) {
						loadedClasses.put(_class.getClassId(), _class);
					}
				}

			}catch (Exception e) {
				JELogger.warning(getClass(), "Failed to load class : " + clazz.getClassName());
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
