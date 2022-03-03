package utils.log;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LogLevel
{
	@JsonProperty("Error")
    ERROR(0),
	@JsonProperty("Control")
    CONTROL(1),
    @JsonProperty("Warning")
    WARNING(2),
    @JsonProperty("Inform")
    INFORM(3),
    @JsonProperty("Debug")
    DEBUG(4);

	LogLevel(int i) {
		// TODO Auto-generated constructor stub
	}
    
    

}
