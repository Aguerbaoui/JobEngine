package io.je.utilities.beans;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.VariableException;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import utils.log.LoggerUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;


@Document(collection = "JEVariableCollection")
public class JEVariable extends JEMonitoredData {

    private UnifiedType type;

    @Transient //mongo doesn't support this type
    private Class<?> typeClass;

    private Object initialValue;


    private Object value;

    private String description;


    private JEVariable() {


    }


    public JEVariable(String jobEngineElementID, String jobEngineProjectID, String name, String type,
                      String initialValue, String description, String createdBy, String modifiedby) throws VariableException {
        super(jobEngineElementID, jobEngineProjectID, name);
        this.type = UnifiedType.valueOf(type);
        this.initialValue = castValue(this.type, initialValue);
        typeClass = getType(this.type);
        this.value = this.initialValue;
        this.jeObjectCreatedBy = createdBy;
        this.jeObjectModifiedBy = modifiedby;
        this.description = description;
    }

    public static Object castValue(UnifiedType type, String value) throws VariableException {
        try {
            switch (type) {
                case SBYTE:
                    return Byte.valueOf(value);
                case UINT16:
                case INT32:
                case INT:
                    return Integer.valueOf(value);

                case BYTE:
                case INT16:
                case SHORT:
                    return Short.valueOf(value);

                case UINT32:
                case INT64:
                case LONG:
                    return Long.valueOf(value);

                case UINT64:
                case FLOAT:
                case SINGLE:
                    return Float.valueOf(value);

                case DOUBLE:
                    return Double.valueOf(value);

                case BOOL:
                    return Boolean.valueOf(value);

                case OBJECT:
                case STRING:
                    return value;

                case DATETIME:
                    return getLocalDateTime(value);


                default:
                    //JELogger.error("Failed to set variable\""+this.jobEngineElementName+"\" value to "+value+": Incompatible Type", null, this.jobEngineProjectID, LogSubModule.VARIABLE, this.jobEngineElementID);
                    return null;

            }
        } catch (Exception e) {
            LoggerUtils.logException(e);
            throw new VariableException(JEMessages.FAILED_TO_CAST_DATA_CHECK_THE_TYPE_OF_THE_VARIABLE_AND_THE_INCOMING_DATA);
        }
    }

    private static Class<?> getType(UnifiedType type) {
        Class<?> classType = null;

        switch (type) {
            case SBYTE:
                classType = byte.class;
                break;
            case UINT16:
            case INT32:
            case INT:
                classType = int.class;
                break;
            case BYTE:
            case INT16:
            case SHORT:
                classType = short.class;
                break;
            case UINT32:
            case INT64:
            case LONG:
                classType = long.class;
                break;
            case UINT64:
            case FLOAT:
            case SINGLE:
                classType = float.class;
                break;
            case DOUBLE:
                classType = double.class;
                break;
            case CHAR:
                classType = char.class;
                break;
            case BOOL:
                classType = boolean.class;
                break;
            case OBJECT:
                classType = Object.class;
                break;
            case STRING:
                classType = String.class;
                break;
            case DATETIME:
                classType = LocalDateTime.class;
                break;

            default:
                break; //add default value
        }


        return classType;
    }

    private static LocalDateTime getLocalDateTime(String value) {
        try {

            return LocalDateTime.parse(value);
        } catch (
                DateTimeParseException e) {
            LoggerUtils.logException(e);
            return null;
        }
    }


    public JEVariable(String jobEngineElementID, String jobEngineProjectID, String name, String type,
                      String initialValue, ArchiveOption isArchived,
                      boolean isBroadcasted, String description, String createdBy, String modifiedby) throws VariableException {
        super(jobEngineElementID, jobEngineProjectID, name, isArchived, isBroadcasted);
        this.type = UnifiedType.valueOf(type);
        this.initialValue = castValue(this.type, initialValue);
        typeClass = getType(this.type);
        this.value = this.initialValue;
        this.jeObjectCreatedBy = createdBy;
        this.jeObjectModifiedBy = modifiedby;
        this.description = description;
    }

    public UnifiedType getType() {
        return type;
    }

    public void setType(UnifiedType type) {
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

    public void setValue(Object value) {
        this.value = value;
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

}
