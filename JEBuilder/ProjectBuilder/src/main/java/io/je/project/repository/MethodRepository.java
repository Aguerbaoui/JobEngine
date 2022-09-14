package io.je.project.repository;

import io.je.project.config.MongoConfig;
import io.je.utilities.beans.JEMethod;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
@AutoConfigureAfter(value = { MongoConfig.class })
public interface MethodRepository extends MongoRepository<JEMethod, String> {
    JEMethod findByJobEngineElementName(String jobEngineElementName);
}
