package io.je.runtime.objects;


import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.je.utilities.execution.*;
import io.je.utilities.models.*;
import io.je.utilities.runtimeobject.JEObject;
import java.lang.*;
import java.lang.String;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import javax.sql.*;

public class ClassC extends JEObject { 

	public String TagName;
	public String Value;
	@JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)  
	@JsonSerialize(using = LocalDateTimeSerializer.class)  
	public LocalDateTime TimeStamp;
	public String Type; 

	@JsonProperty("TagName")
	public void setTagName(
		String TagName
	) { 
		this.TagName=TagName; 
	}
	
	@JsonProperty("TagName")
	public String getTagName() { 
		return this.TagName; 
	}
	
	@JsonProperty("Value")
	public void setValue(
		String Value
	) { 
		this.Value=Value; 
	}
	
	@JsonProperty("Value")
	public String getValue() { 
		return this.Value; 
	}
	
	@JsonProperty("TimeStamp")
	public void setTimeStamp(
		LocalDateTime TimeStamp
	) { 
		this.TimeStamp=TimeStamp; 
	}
	
	@JsonProperty("TimeStamp")
	public LocalDateTime getTimeStamp() { 
		return this.TimeStamp; 
	}
	
	@JsonProperty("Type")
	public void setType(
		String Type
	) { 
		this.Type=Type; 
	}
	
	@JsonProperty("Type")
	public String getType() { 
		return this.Type; 
	} 

}