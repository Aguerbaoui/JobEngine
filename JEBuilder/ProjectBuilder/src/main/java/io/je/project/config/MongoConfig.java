package io.je.project.config;

import java.util.Collection;
import java.util.Collections;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import io.je.utilities.config.Utility;

@Configuration
@EnableMongoRepositories(basePackages = "io.je.project.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {


    @Override
    protected String getDatabaseName() {
        return "SIOTHDatabase";
    }

    @Override
    public MongoClient mongoClient() {
        final ConnectionString connectionString = new ConnectionString("mongodb://"+Utility.getSiothConfig().getMongoConfiguration().getMongoServerHostName()+":"+Utility.getSiothConfig().getMongoConfiguration().getMongoServerPort() +"/"+getDatabaseName());
       //  MongoCredential credential = MongoCredential.createCredential(SIOTHConfiguration.getMongoUserName(), SIOTHConfiguration.getDataBaseName(), SIOTHConfiguration.getMongoPassword().toCharArray());
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
          //  .credential(credential)
            .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Override
    public Collection<String> getMappingBasePackages() {
        return Collections.singleton("io.je.project");
    }

    }