package io.je.rulebuilder.config;

import java.util.ArrayList;

/*
 * mapping for block parameters
 */
public class AttributesMapping {

    //common attributes
    public static final String PROJECTID = "project_id";
    public static final String RULEID = "rule_id";
   
    
    //block attributes
    public static final String BLOCKID = "block_id";
    public static final String OPERATIONID = "operation_id";
    public static final String TIMEPERSISTENCEON = "time_persistence_on";
    public static final String TIMEPERSISTENCEVALUE = "";
    public static final String TIMEPERSISTENCEUNIT = "";
    public static final String INPUTBLOCK = "input_blocks";
	public static final String OUTPUTBLOCK = "output_blocks";

    public static final String BLOCKCONFIG = "block_configuration";
    
    //block configuration
	public static final String VALUE = "value";
	public static final String INPUTUNIT = "input_unit";
	public static final String OUTPUTUNIT = "output_unit";
	public static final String CLASSID = "class_id";
	public static final String ATTRIBUTENAME = "attribute_name";
	public static final String SPECIFICINSTANCES = "specific_instances";
	
    //rule attributes 
	public static final String SALIENCE ="priority";
	public static final String ENABLED="enabled";
	public static final String DATEEFFECTIVE="date_effective";
	public static final String DATEEXPIRES="date_expires";
	public static final String TIMER="timer";

  
    
    
    
    
  


}
