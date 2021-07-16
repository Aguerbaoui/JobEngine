package io.je.project.siothconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RedisConfiguration {
    
	@JsonProperty("HostAdress")
	public String hostAdress ;
    
	@JsonProperty("Port")
	public int port ;
	
	@JsonProperty("Password")
    public String password ;
    
	@JsonProperty("User")
	public String user ;
    
	@JsonProperty("Channel")
	public String channel ;
	private RedisConfiguration() {
		// TODO Auto-generated constructor stub
	}
	public String getHostAdress() {
		return hostAdress;
	}
	public void setHostAdress(String hostAdress) {
		this.hostAdress = hostAdress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}

    
}
