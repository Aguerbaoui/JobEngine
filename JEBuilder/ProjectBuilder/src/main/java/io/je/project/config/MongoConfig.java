package io.je.project.config;

import java.util.Collection;
import java.util.Collections;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import io.siothconfig.SIOTHConfigUtility;
import utils.string.StringUtilities;


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
        //MongoCredential credential = MongoCredential.createCredential(username, getDatabaseName(), password.toCharArray() );
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                /*.applyToClusterSettings(builder ->
                        builder.hosts(Arrays.asList(new ServerAddress(SIOTHConfigUtility.getSiothConfig().getMongoConfiguration().getMongoServerHostName(),
                                SIOTHConfigUtility.getSiothConfig().getMongoConfiguration().getMongoServerPort()))))*/
                .applyConnectionString(connectionString)
                //.credential(credential)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    /*db.createUser(
    {	user: "sa",
        pwd: "io.123",

        roles:[{role: "readWrite" , db:"SIOTHDatabase"}]})
    * */
    @Override
    public Collection<String> getMappingBasePackages() {
        return Collections.singleton("io.je.project");
    }

}