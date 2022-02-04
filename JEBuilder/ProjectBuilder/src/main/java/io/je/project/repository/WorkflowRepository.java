package io.je.project.repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import models.JEWorkflow;



@Repository
public interface WorkflowRepository extends MongoRepository<JEWorkflow,String>{
	List<JEWorkflow> findByJobEngineProjectID (String projectId);
	void deleteByJobEngineProjectID (String projectId);
	List<JEWorkflow> findByJobEngineElementName(String jobEngineElementName);

	

}
