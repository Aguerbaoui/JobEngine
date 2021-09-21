package io.je.project.siothconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


public class SIOTHConfig {

    private String SIOTHInstallationPath;

    private SIOTHConfig() {}

    @JsonProperty("MachineCredentials")
    private MachineCredentials machineCredentials ;
    
    @JsonProperty("MongoConfiguration")
    private MongoConfiguration mongoConfiguration ;
    
    @JsonProperty("InfluxDBConfiguration")
    private InfluxDBConfiguration influxDBConfiguration ;
    
    @JsonProperty("RedisConfiguration")
    private RedisConfiguration redisConfiguration ;
    
    @JsonProperty("PORTS")
    private SIOTHPorts siothPorts ;
    
    @JsonProperty("DateFormat")
    private String dateFormat;

    @JsonProperty("DataModelPORTS")
    private DataModelPORTS dataModelPORTS ;
    
    @JsonProperty("JobEngine")
    private JobEngine jobEngine;
    
    @JsonProperty("Connectors")
    private Connectors connectors ;
    
    @JsonProperty("isKillProcess")
    private boolean isKillProcess ;
    
    @JsonProperty("APIS")
    private APIS apis;

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
		return dateFormat.replace("f", "S");
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
