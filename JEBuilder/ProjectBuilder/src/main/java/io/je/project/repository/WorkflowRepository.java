package io.je.project.repository;


import io.je.project.config.MongoConfig;
import models.JEWorkflow;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@AutoConfigureAfter(value = { MongoConfig.class })
public interface WorkflowRepository extends MongoRepository<JEWorkflow, String> {
    List<JEWorkflow> findByJobEngineProjectID(String projectId);

    void deleteByJobEngineProjectID(String projectId);

    List<JEWorkflow> findByJobEngineElementName(String jobEngineElementName);

}
