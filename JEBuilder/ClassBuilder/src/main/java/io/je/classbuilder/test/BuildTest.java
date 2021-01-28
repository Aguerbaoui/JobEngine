package io.je.classbuilder.test;

import java.io.IOException;

import io.je.classbuilder.builder.ClassManager;
import io.je.utilities.exceptions.AddClassException;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.DataDefinitionUnreachableException;

public class BuildTest {

	public static void main(String[] args) throws IOException, AddClassException, ClassLoadException, DataDefinitionUnreachableException {
		 ClassManager.buildClass("123", "00fd4e5d-5f19-4b8a-9c89-66e05be497b4");
		//System.out.println(p);
		
		

		
	}

}
