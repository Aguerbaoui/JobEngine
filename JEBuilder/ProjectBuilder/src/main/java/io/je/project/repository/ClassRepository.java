package io.je.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.je.classbuilder.entity.JEClass;

public interface ClassRepository extends MongoRepository<JEClass, String> {

}
