package io.je.project.siothconfig;

import com.fasterxml.jackson.annotation.JsonProperty;


public class SIOTHConfig {

    public String SIOTHInstallationPath;

    public SIOTHConfig() {}

    @JsonProperty("MachineCredentials")
    public MachineCredentials machineCredentials ;
    
    @JsonProperty("MongoConfiguration")
    public MongoConfiguration mongoConfiguration ;
    
    @JsonProperty("InfluxDBConfiguration")
    public InfluxDBConfiguration influxDBConfiguration ;
    
    @JsonProperty("RedisConfiguration")
    public RedisConfiguration redisConfiguration ;
    
    @JsonProperty("PORTS")
    public SIOTHPorts siothPorts ;
    
    @JsonProperty("DateFormat")
    public String dateFormat;

    @JsonProperty("DataModelPORTS")
    public DataModelPORTS dataModelPORTS ;
    
    @JsonProperty("JobEngine")
    public JobEngine jobEngine;
    
    @JsonProperty("Connectors")
    public Connectors connectors ;
    
    @JsonProperty("isKillProcess")
    public boolean isKillProcess ;
    
    @JsonProperty("APIS")
    public APIS apis;

	public String getSIOTHInstallationPath() {
		return SIOTHInstallationPath;
	}

	public void setSIOTHInstallationPath(String sIOTHInstallationPath) {
		SIOTHInstallationPath = sIOTHInstallationPath;
	}

	public MachineCredentials getMachineCredentials() {
		return machineCredentials;
	}

	public void setMachineCredentials(MachineCredentials machineCredentials) {
		this.machineCredentials = machineCredentials;
	}

	public MongoConfiguration getMongoConfiguration() {
		return mongoConfiguration;
	}

	public void setMongoConfiguration(MongoConfiguration mongoConfiguration) {
		this.mongoConfiguration = mongoConfiguration;
	}

	public InfluxDBConfiguration getInfluxDBConfiguration() {
		return influxDBConfiguration;
	}

	public void setInfluxDBConfiguration(InfluxDBConfiguration influxDBConfiguration) {
		this.influxDBConfiguration = influxDBConfiguration;
	}

	public RedisConfiguration getRedisConfiguration() {
		return redisConfiguration;
	}

	public void setRedisConfiguration(RedisConfiguration redisConfiguration) {
		this.redisConfiguration = redisConfiguration;
	}

	public SIOTHPorts getPorts() {
		return siothPorts;
	}

	public void setPorts(SIOTHPorts sIOTHPorts) {
		this.siothPorts = sIOTHPorts;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public DataModelPORTS getDataModelPORTS() {
		return dataModelPORTS;
	}

	public void setDataModelPORTS(DataModelPORTS dataModelPORTS) {
		this.dataModelPORTS = dataModelPORTS;
	}

	public JobEngine getJobEngine() {
		return jobEngine;
	}

	public void setJobEngine(JobEngine jobEngine) {
		this.jobEngine = jobEngine;
	}

	public Connectors getConnectors() {
		return connectors;
	}

	public void setConnectors(Connectors connectors) {
		this.connectors = connectors;
	}

	public boolean isKillProcess() {
		return isKillProcess;
	}

	public void setKillProcess(boolean isKillProcess) {
		this.isKillProcess = isKillProcess;
	}

	public APIS getApis() {
		return apis;
	}

	public void setApis(APIS apis) {
		this.apis = apis;
	}

    
    
    


   /* public String LoadConfigAsString();
    public void loadSIOTHConfigPath();
    public void LoadSIOTHConfiguration(out String StrError);
    public void SaveConfiguration(String Content);
    public void SaveURLConfiguration(String Content);
    */
    
}
