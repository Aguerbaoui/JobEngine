package io.je.project.repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.je.utilities.beans.JEVariable;



@Repository
public interface VariableRepository extends MongoRepository<JEVariable,String>{
	List<JEVariable> findByJobEngineProjectID (String projectId);
	void deleteByJobEngineProjectID (String projectId);

	

}
