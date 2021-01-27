package io.je.project.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Autowired
	ClassRepository classRepository;

	Map<String, JEClass> loadedClasses = new HashMap<String, JEClass>();

	public void addClasses(List<ClassDefinition> classDefinitions) throws DataDefinitionUnreachableException,
			JERunnerErrorException, AddClassException, ClassLoadException, IOException {
		for (ClassDefinition clazz : classDefinitions) {
			addClass(clazz.getWorkspaceId(), clazz.getClassId());
		}
	}

	/*
	 * add/update class
	 */
	public void addClass(String workspaceId, String classId) throws IOException, DataDefinitionUnreachableException,
			JERunnerErrorException, AddClassException, ClassLoadException {

		if (!loadedClasses.containsKey(classId)) {
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
	private void addClassToJeRunner(JEClass clazz) throws IOException, AddClassException, JERunnerErrorException {
		HashMap<String, String> classMap = new HashMap<>();
		classMap.put("className", clazz.getClassName());
		classMap.put("classPath", clazz.getClassPath());
		classMap.put("classId", clazz.getClassId());
		JEResponse jeRunnerResp = JERunnerAPIHandler.addClass(classMap);
		if (jeRunnerResp.getCode() != ResponseCodes.CODE_OK) {
			throw new AddClassException(ClassBuilderErrors.classLoadFailed);
		}

	}

	public void loadAllClasses() throws DataDefinitionUnreachableException, JERunnerErrorException, AddClassException,
			ClassLoadException, IOException {
		List<JEClass> classes = classRepository.findAll();

		for (JEClass clazz : classes) {
			addClass(clazz.getWorkspaceId(), clazz.getClassId());
		}

	}

}
