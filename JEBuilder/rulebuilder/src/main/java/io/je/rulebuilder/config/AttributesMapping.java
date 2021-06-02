package io.je.rulebuilder.config;

import java.util.ArrayList;

/*
 * mapping for block parameters
 */
public class AttributesMapping {

    //common attributes
    public static final String PROJECTID = "project_id";
    public static final String RULEID = "rule_id";
	public static final String RULENAME = "rule_name";
	public static final String DESC = "description";
	public static final String CREATEDAT = "created_at";
	public static final String LASTUPDATE = "last_update_at";
	public static final String BUILDSTATUS = "isBuilt";




    
    //block attributes
    public static final String BLOCKID = "block_id";
    public static final String BLOCKNAME = "block_name";
    public static final String OPERATIONID = "operation_id";
    public static final String TIMEPERSISTENCEON = "time_persistence_on";
    public static final String TIMEPERSISTENCEVALUE = "persistence_value";
    public static final String TIMEPERSISTENCEUNIT = "persistence_unit";
    public static final String INPUTBLOCK = "input_blocks";
	public static final String OUTPUTBLOCK = "output_blocks";

    public static final String BLOCKCONFIG = "block_configuration";
    
    //block configuration
	public static final String VALUE = "value";
	public static final String VALUE2 = "value2";

	public static final String INPUTUNIT = "input_unit";
	public static final String OUTPUTUNIT = "output_unit";
	public static final String CLASSID = "idClass";
	public static final String ATTRIBUTENAME = "attribute_name";
	public static final String SPECIFICINSTANCES = "specific_instances";
	
    //rule attributes 
	public static final String SCRIPT = "script";
	public static final String SALIENCE ="priority";
	public static final String ENABLED="enabled";
	public static final String DATEEFFECTIVE="date_effective";
	public static final String DATEEXPIRES="date_expires";
	public static final String TIMER="scheduler";
	public static final String CLASSNAME = "class_name"; 
	public static final String WORKSPACEID = "workspace_id";
	public static final String CLASSES = "classes";
	public static final String FRONTCONFIG = "ruleFrontConfig";
	public static final String TYPE = "type";
	public static final String INSTANCEID = "instanceId";
	public static final String DATEVALUE = "dateValue";

	public static final String BOOLEANVALUE = "includeBounds";

  
    
    
    
    
  


}
