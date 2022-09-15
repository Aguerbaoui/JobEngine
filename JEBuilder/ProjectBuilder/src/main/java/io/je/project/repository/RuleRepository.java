package io.je.project.repository;


import io.je.project.config.MongoConfig;
import io.je.rulebuilder.components.JERule;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@AutoConfigureAfter(value = { MongoConfig.class })
public interface RuleRepository extends MongoRepository<JERule, String> {
    List<JERule> findByJobEngineProjectID(String projectId);

    void deleteByJobEngineProjectID(String projectId);


}
