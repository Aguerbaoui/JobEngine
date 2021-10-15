package io.je.project.repository;

import io.je.utilities.beans.JEMethod;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MethodRepository extends MongoRepository<JEMethod, String> {
    JEMethod findByJobEngineElementName(String jobEngineElementName);
}
