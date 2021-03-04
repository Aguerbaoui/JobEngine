package io.je.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.je.utilities.models.ConfigModel;

public interface ConfigRepository extends MongoRepository<ConfigModel, String> {

}
