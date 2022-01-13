package io.je.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.je.utilities.beans.JEClass;

@Repository
public interface ClassRepository extends MongoRepository<JEClass, String> {
    void deleteByClassName (String className);
}
