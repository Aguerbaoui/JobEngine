package io.je.project.repository;

import io.je.project.config.MongoConfig;
import io.je.utilities.beans.JELib;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
@AutoConfigureAfter(value = { MongoConfig.class })
public interface LibraryRepository extends MongoRepository<JELib, String> {
    JELib findByJobEngineElementName(String jobEngineElementName);
}
