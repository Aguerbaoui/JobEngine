package io.je.project.repository;

import io.je.utilities.beans.JELib;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LibraryRepository extends MongoRepository<JELib,String> {
}
