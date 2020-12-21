package io.je.rulebuilder.components.blocks;

import java.util.ArrayList;
import java.util.List;

import io.je.rulebuilder.components.enumerations.TimePersistenceUnit;


/*
 * blocks that can be persisted in time
 */
public abstract class PersistableBlock extends ConditionBlock {

    // persistence in time
	  boolean timePersistenceOn;
	    int timePersistenceValue;
	    TimePersistenceUnit timePersistenceUnit;

   
    
    


}

