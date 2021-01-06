package io.je.project.services;

import org.springframework.stereotype.Service;

import io.je.classbuilder.models.ClassModel;
import io.je.project.beans.JEProject;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.Errors;
import io.je.utilities.exceptions.ClassFormatInvalidException;
import io.je.utilities.exceptions.ProjectNotFoundException;

/*
 * Service class to handle classes
 */
@Service
public class ClassService {

	
	/*
	 * add a new class to project
	 */
	public void addClass(String projectId,ClassModel classModel) throws ProjectNotFoundException, ClassFormatInvalidException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
		}
		project.addClass(classModel);
	}
}

