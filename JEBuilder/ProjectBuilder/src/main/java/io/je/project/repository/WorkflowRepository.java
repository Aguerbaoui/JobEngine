package io.je.project.repository;


import models.JEWorkflow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface WorkflowRepository extends MongoRepository<JEWorkflow,String>{
	List<JEWorkflow> findByJobEngineProjectID (String projectId);
	void deleteByJobEngineProjectID (String projectId);
	List<JEWorkflow> findByJobEngineElementName(String jobEngineElementName);

}
