package io.je.project.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.je.utilities.beans.JEEvent;

@Repository
public interface EventRepository extends MongoRepository<JEEvent,String>{
	List<JEEvent> findByJobEngineProjectID (String projectId);
	

}
