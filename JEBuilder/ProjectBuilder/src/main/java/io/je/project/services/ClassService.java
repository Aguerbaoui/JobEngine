package io.je.project.services;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;

import io.je.classbuilder.builder.ClassManager;
import io.je.classbuilder.entity.JEClass;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.constants.ClassBuilderErrors;
import io.je.utilities.constants.JEGlobalconfig;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.AddClassException;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.DataDefinitionUnreachableException;
import io.je.utilities.exceptions.JERunnerUnreachableException;
import io.je.utilities.network.JEResponse;
import io.je.utilities.network.Network;

/*
 * Service class to handle classes
 */
@Service
public class ClassService {

	private List<String> classes = new ArrayList<>();
	

	/*
	 * add class
	 */
	public void addClass(String workspaceId, String classId)
			throws IOException, DataDefinitionUnreachableException, JERunnerUnreachableException, AddClassException, ClassLoadException {

		List<JEClass> builtClasses = ClassManager.buildClass(workspaceId, classId);
		for (JEClass _class : builtClasses) {
			addClassToJeRunner(_class);
		}

	}

	
	private void addClassToJeRunner(JEClass _class) throws IOException, AddClassException, JERunnerUnreachableException {
		// TODO: send class to JERunner to be loaded
				HashMap<String, String> classMap = new HashMap<>();
				classMap.put("className", _class.getClassName());
				classMap.put("classPath", _class.getClassPath());
				classMap.put("classId", _class.getClassId());
				JEResponse jeRunnerResp = JERunnerAPIHandler.addClass(classMap);
				if(jeRunnerResp.getCode()!=ResponseCodes.CODE_OK)
				{
					throw new AddClassException(ClassBuilderErrors.classLoadFailed);
				}
		
	}

}
