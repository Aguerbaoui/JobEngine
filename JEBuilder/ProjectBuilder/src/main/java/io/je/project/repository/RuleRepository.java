package io.je.project.repository;


import io.je.rulebuilder.components.JERule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RuleRepository extends MongoRepository<JERule,String>{
	List<JERule> findByJobEngineProjectID (String projectId);
	void deleteByJobEngineProjectID (String projectId);

	

}
