package io.je.project.siothconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MongoConfiguration {


    @JsonProperty("MongoServerHostName")
    public String mongoServerHostName ;
    
    @JsonProperty("MongoServerPort")
    public int mongoServerPort ;

    @JsonProperty("MongoUserName")
    public String mongoUserName ;

    @JsonProperty("MongoPassword")
    public String mongoPassword ;

    
    private MongoConfiguration() {
		// TODO Auto-generated constructor stub
	}


	public String getMongoServerHostName() {
		return mongoServerHostName;
	}


	public void setMongoServerHostName(String mongoServerHostName) {
		this.mongoServerHostName = mongoServerHostName;
	}


	public int getMongoServerPort() {
		return mongoServerPort;
	}


	public void setMongoServerPort(int mongoServerPort) {
		this.mongoServerPort = mongoServerPort;
	}


	public String getMongoUserName() {
		return mongoUserName;
	}


	public void setMongoUserName(String mongoUserName) {
		this.mongoUserName = mongoUserName;
	}


	public String getMongoPassword() {
		return mongoPassword;
	}


	public void setMongoPassword(String mongoPassword) {
		this.mongoPassword = mongoPassword;
	}
	
}
