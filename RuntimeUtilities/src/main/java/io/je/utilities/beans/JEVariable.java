package io.je.utilities.beans;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.VariableException;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "JEVariableCollection")
public class JEVariable extends JEMonitoredData {

	private JEType type;

    @Transient //mongo doesn't support this type
    private Class<?> typeClass;

    private Object initialValue;
    
    
    private Object value;
    
    private String description;
    
   




    private JEVariable() {
		

	}

    
    

    public JEVariable(String jobEngineElementID, String jobEngineProjectID, String name, String type,
			String initialValue,String description,String createdBy,String modifiedby) throws VariableException {
		super(jobEngineElementID, jobEngineProjectID, name);
		this.type = JEType.valueOf(type);
		this.initialValue = castValue(this.type, initialValue);
		typeClass = getType(this.type);
		this.value=this.initialValue;
		this.jeObjectCreatedBy=createdBy;
		this.jeObjectModifiedBy = modifiedby;
		this.description=description;
	}




	public JEVariable(String jobEngineElementID, String jobEngineProjectID, String name, String type,
			String initialValue,ArchiveOption isArchived,
			boolean isBroadcasted,String description,String createdBy,String modifiedby) throws VariableException {
		super(jobEngineElementID, jobEngineProjectID, name, isArchived, isBroadcasted);
		this.type = JEType.valueOf(type);
		this.initialValue = castValue(this.type, initialValue);
		typeClass = getType(this.type);
		this.value=this.initialValue;
		this.jeObjectCreatedBy=createdBy;
		this.jeObjectModifiedBy = modifiedby;
		this.description=description;
	}






	public JEType getType() {
		return type;
	}




	public void setType(JEType type) {
		this.type = type;
		typeClass = getType(type);
	}




	public Object getInitialValue() {
		return initialValue;
	}




	public void setInitialValue(Object initialValue) {
		this.initialValue = initialValue;
	}




	public Object getValue() {
		return value;
	}




	public void setValue(String value) throws VariableException {
		this.value = castValue(type, value);
	}






	public Class<?> getTypeClass() {
		return typeClass;
	}




	public void setTypeClass(Class<?> typeClass) {
		this.typeClass = typeClass;
	}




	public String getDescription() {
		return description;
	}




	public void setDescription(String description) {
		this.description = description;
	}




	public void setValue(Object value) {
		this.value = value;
	}




	public static Object castValue(JEType type, String value) throws VariableException {
		try {
			switch (type) {
				case BYTE:
					return Byte.valueOf(value);
				case DOUBLE:
					return Double.valueOf(value);
				case FLOAT:
					return Float.valueOf(value);
				case INT:
					return Integer.valueOf(value);
				case LONG:
					return Long.valueOf(value);
				case SHORT:
					return Short.valueOf(value);
				case STRING:
					return value;
				case BOOLEAN:
					return Boolean.valueOf(value);
				default:
					//JELogger.error("Failed to set variable\""+this.jobEngineElementName+"\" value to "+value+": Incompatible Type", null, this.jobEngineProjectID, LogSubModule.VARIABLE, this.jobEngineElementID);
					return null;

			}
		}
		catch (Exception e) {
			throw new VariableException(JEMessages.FAILED_TO_CAST_DATA_CHECK_THE_TYPE_OF_THE_VARIABLE_AND_THE_INCOMING_DATA);
		}
	}


	/*
     * returns the class type based on a string defining the type
     */
    public static Class<?> getType(JEType type) {
        String typeString = type.toString();
        Class<?> classType = null;
        switch (typeString) {
            case "BYTE":
                classType = byte.class;
                break;
            case "SBYTE":
                classType = byte.class;
                break;
            case "INT":
                classType = int.class;
                break;
            case "SHORT":
                classType = short.class;
                break;
            case "LONG":
                classType = long.class;
                break;
            case "FLOAT":
                classType = float.class;
                break;
            case "DOUBLE":
                classType = double.class;
                break;
            case "CHAR":
                classType = char.class;
                break;
            case "BOOL":
                classType = boolean.class;
                break;
            case "OBJECT":
                classType = Object.class;
                break;
            case "STRING":
                classType = String.class;
                break;
            case "DATETIME":
                classType = LocalDateTime.class;
                break;
            case "VOID":
                classType = void.class;
                break;
            default:
                break; //add default value
        }
        return classType;
    }
    
}
