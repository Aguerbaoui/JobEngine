package io.je.project.repository;

import io.je.project.config.MongoConfig;
import io.je.utilities.beans.JEEvent;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AutoConfigureAfter(value = { MongoConfig.class })
public interface EventRepository extends MongoRepository<JEEvent, String> {
    List<JEEvent> findByJobEngineProjectID(String projectId);

    void deleteByJobEngineProjectID(String projectId);


}
