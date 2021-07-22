package io.je.project.siothconfig;

public class Cred {

	  public String Username ;
      public String Password ;
      public boolean useBasicAuth ;
      
      
      
	private Cred() {
		// TODO Auto-generated constructor stub
	}
	public String getUsername() {
		return Username;
	}
	public void setUsername(String username) {
		Username = username;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	public boolean isUseBasicAuth() {
		return useBasicAuth;
	}
	public void setUseBasicAuth(boolean useBasicAuth) {
		this.useBasicAuth = useBasicAuth;
	}
      
      
}
