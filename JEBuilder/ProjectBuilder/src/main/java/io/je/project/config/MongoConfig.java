package io.je.project.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.siothconfig.SIOTHConfigUtility;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import utils.string.StringUtilities;

import java.util.Collection;
import java.util.Collections;


/*
 * MongoDB configuration
 * */
@Configuration
@EnableMongoRepositories(basePackages = "io.je.project.repository")
@Lazy
public class MongoConfig extends AbstractMongoClientConfiguration {


    @Override
    protected String getDatabaseName() {
        return "SIOTHDatabase";
    }

    @Override
    public MongoClient mongoClient() {

        String username = SIOTHConfigUtility.getSiothConfig().getMongoConfiguration().getMongoUserName();
        String password = SIOTHConfigUtility.getSiothConfig().getMongoConfiguration().getMongoPassword();

        String host = SIOTHConfigUtility.getSiothConfig().getMongoConfiguration().getMongoServerHostName();
        int port = SIOTHConfigUtility.getSiothConfig().getMongoConfiguration().getMongoServerPort();

        final ConnectionString connectionString;

        if (StringUtilities.isEmpty(username) || StringUtilities.isEmpty(password)) {
            connectionString = new ConnectionString("mongodb://" + host + ":" + port + "/" + getDatabaseName());
        } else {
            connectionString = new ConnectionString("mongodb://" + username + ":" + password + "@" + host + ":" + port);
        }

        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Override
    public Collection<String> getMappingBasePackages() {
        return Collections.singleton("io.je.project");
    }

}