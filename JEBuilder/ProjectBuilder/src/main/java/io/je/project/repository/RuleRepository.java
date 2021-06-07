package io.je.project.repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.je.rulebuilder.components.JERule;


@Repository
public interface RuleRepository extends MongoRepository<JERule,String>{
	List<JERule> findByJobEngineProjectID (String projectId);
	

}
