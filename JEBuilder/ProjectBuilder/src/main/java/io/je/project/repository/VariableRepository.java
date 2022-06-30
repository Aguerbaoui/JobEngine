package io.je.project.repository;


import io.je.utilities.beans.JEVariable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface VariableRepository extends MongoRepository<JEVariable,String>{
	List<JEVariable> findByJobEngineProjectID (String projectId);
	List<JEVariable> findByJobEngineElementName (String projectId);
	void deleteByJobEngineProjectID (String projectId);

	

}
