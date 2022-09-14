package io.je.project.repository;

import io.je.project.config.MongoConfig;
import io.je.utilities.beans.JEClass;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AutoConfigureAfter(value = { MongoConfig.class })
public interface ClassRepository extends MongoRepository<JEClass, String> {
    void deleteByClassName(String className);

    List<JEClass> findByClassAuthor(String classAuthor);
}
