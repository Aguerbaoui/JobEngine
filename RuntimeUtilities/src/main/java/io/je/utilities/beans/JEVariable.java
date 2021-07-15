package io.je.utilities.beans;

import io.je.utilities.runtimeobject.JEObject;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "JEVariableCollection")
public class JEVariable extends JEObject {

    private String name;

    private JEType type;

    @Transient //mongo doesn't support this type
    private Class<?> typeClass;

    private Object initialValue;
    
    private Object value;




    private JEVariable() {
		

	}


    public JEVariable(String jobEngineElementID, String jobEngineProjectID, String name, String type,
			String initialValue) {
		super(jobEngineElementID, jobEngineProjectID);
		this.name = name;
		this.type = JEType.valueOf(type);
		this.initialValue = castValue(initialValue);
		typeClass = getType(this.type);
		this.value=this.initialValue;
	}




	public String getName() {
		return name;
	}




	public void setName(String name) {
		this.name = name;
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




	public void setValue(String value) {
		this.value = castValue(value);
	}






	private Object castValue(String value) {
		switch(type)
		{
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
			//TOOD: throw error msg
			return null;
		
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
