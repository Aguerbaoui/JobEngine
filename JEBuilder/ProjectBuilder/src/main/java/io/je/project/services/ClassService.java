package io.je.project.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
import io.je.utilities.exceptions.JERunnerUnreachableException;
import io.je.utilities.network.JEResponse;
import io.je.utilities.runtimeobject.ClassDefinition;

/*
 * Service class to handle classes
 */
@Service
public class ClassService {

	@Autowired
	ClassRepository classRepository;
	
	
	
	
	public void addClasses(List<ClassDefinition> classDefinitions) throws DataDefinitionUnreachableException, JERunnerUnreachableException, AddClassException, ClassLoadException, IOException
	{
		for (ClassDefinition clazz : classDefinitions)
		{
			addClass(clazz.getWorkspaceId(),clazz.getClassId());
		}
	}

	/*
	 * add/update class
	 */
	public void addClass(String workspaceId, String classId)
			throws IOException, DataDefinitionUnreachableException, JERunnerUnreachableException, AddClassException, ClassLoadException {

		List<JEClass> builtClasses = ClassManager.buildClass(workspaceId, classId);
		for (JEClass _class : builtClasses) {
			//TODO: manage what happens when class addition fails
			addClassToJeRunner(_class);
			classRepository.save(_class);
		}

	}


	/*
	 * send class to je runner to be loaded there
	 */
	private void addClassToJeRunner(JEClass clazz) throws IOException, AddClassException, JERunnerUnreachableException {
				HashMap<String, String> classMap = new HashMap<>();
				classMap.put("className", clazz.getClassName());
				classMap.put("classPath", clazz.getClassPath());
				classMap.put("classId", clazz.getClassId());
				JEResponse jeRunnerResp = JERunnerAPIHandler.addClass(classMap);
				if(jeRunnerResp.getCode()!=ResponseCodes.CODE_OK)
				{
					throw new AddClassException(ClassBuilderErrors.classLoadFailed);
				}
		
	}

}
