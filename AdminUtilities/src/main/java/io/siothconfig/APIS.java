package io.siothconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class APIS {

	@JsonProperty("EmailAPI")
	 public Conf emailAPI ;
	
	@JsonProperty("DatabaseAPI")
     public Conf databaseAPI ;

	public Conf getEmailAPI() {
		return emailAPI;
	}

	public void setEmailAPI(Conf emailAPI) {
		this.emailAPI = emailAPI;
	}

	public Conf getDatabaseAPI() {
		return databaseAPI;
	}

	public void setDatabaseAPI(Conf databaseAPI) {
		this.databaseAPI = databaseAPI;
	}
     
     
     
	
     
}
