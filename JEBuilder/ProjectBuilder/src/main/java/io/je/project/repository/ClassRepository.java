package io.je.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import io.je.utilities.beans.JEClass;

import java.util.List;

@Repository
public interface ClassRepository extends MongoRepository<JEClass, String> {
    void deleteByClassName (String className);

    List<JEClass> findByClassAuthor(String classAuthor);
}
