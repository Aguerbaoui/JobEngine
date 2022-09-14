package io.je.project.repository;

import io.je.project.beans.JEProject;
import io.je.project.config.MongoConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AutoConfigureAfter(value = { MongoConfig.class })
public interface ProjectRepository extends MongoRepository<JEProject, String> {
    Optional<JEProject> findByProjectName(String jobEngineElementName);

}
