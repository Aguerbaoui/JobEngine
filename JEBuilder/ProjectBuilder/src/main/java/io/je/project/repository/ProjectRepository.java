package io.je.project.repository;

import io.je.project.beans.JEProject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<JEProject, String> {
    Optional<JEProject> findByProjectName(String jobEngineElementName);
	

}
