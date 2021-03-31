package io.je.classbuilder.builder;

import java.io.File;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.burningwave.core.classes.AnnotationSourceGenerator;
import org.burningwave.core.classes.ClassSourceGenerator;
import org.burningwave.core.classes.FunctionSourceGenerator;
import org.burningwave.core.classes.TypeDeclarationSourceGenerator;
import org.burningwave.core.classes.UnitSourceGenerator;
import org.burningwave.core.classes.VariableSourceGenerator;

import io.je.classbuilder.entity.ClassType;
import io.je.classbuilder.models.ClassModel;
import io.je.classbuilder.models.FieldModel;
import io.je.classbuilder.models.MethodModel;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.constants.ClassBuilderErrors;
import io.je.utilities.exceptions.AddClassException;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.runtimeobject.JEObject;
import io.je.utilities.string.JEStringUtils;

/*
 * build class from class definition
 */
public class ClassBuilder {


	/*
	 * build .java class/interface/enum from classModel
	 * returns  path where file was created
	 */
	public static String buildClass(ClassModel classModel, String generationPath) throws AddClassException, ClassLoadException {
		
		//check if class format is valid
		if(classModel.getName()==null)
		{
			JELogger.error(ClassBuilder.class, ClassBuilderErrors.classNameNull);
			throw new AddClassException(ClassBuilderErrors.classNameNull);
		}
		
		
	
		
		//generate class
		if (classModel.getIsClass()) {
			return  generateClass(classModel, generationPath);
			

		}

		//generate interface
		if (classModel.getIsInterface()) {
			return generateInterface(classModel, generationPath);
			
		}
		return null;

	}

	/* add imports */
	private static void addImports(List<String> imports, UnitSourceGenerator unitSG ) {
		//TODO : remove harcoded imports
		unitSG.addImport("com.fasterxml.jackson.annotation.JsonProperty");
		unitSG.addImport("io.je.utilities.logger.JELogger");
		unitSG.addImport("java.lang.*");
		unitSG.addImport("java.util.*");
		unitSG.addImport("java.sql.*");
		unitSG.addImport("javax.sql.*");
		if (imports != null && !imports.isEmpty()) {
			{
				for (String import_ : imports) {
					//unitSG.addImport(import_);
				}
			}
			}
	}
		

	/*
	 * generate an interface
	 */
	private static String generateInterface(ClassModel classModel, String generationPath) throws ClassLoadException {
		 UnitSourceGenerator unitSG = UnitSourceGenerator.create(ClassBuilderConfig.genrationPackageName);
			//add imports
			addImports(classModel.getImports(),unitSG);
		// class name
		String interfaceName = classModel.getName();
		TypeDeclarationSourceGenerator type = TypeDeclarationSourceGenerator.create(interfaceName);
		ClassSourceGenerator newInterface = ClassSourceGenerator.createInterface(type);
		// class modifier
		newInterface.addModifier(getModifier(classModel.getClassVisibility()));

		// TODO: add interface methods
		
		//add inheritance
		String inheritedClass = null;
		List<String> inheritedInterfaces = new ArrayList<String>();

		if (classModel.getBaseTypes() != null && !classModel.getBaseTypes().isEmpty()) {
			
			for(String classId : classModel.getBaseTypes())
			{
				if(ClassManager.getClassType(classId) == ClassType.CLASS) {
					inheritedClass=classId ;
					break;}
				if(ClassManager.getClassType(classId) == ClassType.INTERFACE) {
					inheritedInterfaces.add(classId);
				}
				
			}
			// extend class
			if(inheritedClass != null)
			{
				JELogger.error(ClassBuilder.class, ClassBuilderErrors.invalidClassFormat + "INTERFACE CAN'T INHERIT FROM CLASS");
				throw new ClassLoadException(ClassBuilderErrors.invalidClassFormat + "INTERFACE CAN'T INHERIT FROM CLASS");
				
			}
			 // implement interfaces
			for(String id:inheritedInterfaces)
			{
				newInterface.addConcretizedType(ClassManager.getClassById(id));
			}

		}
		
		// store class
				unitSG.addClass(newInterface);
				String filePath= generationPath + "\\" + ClassBuilderConfig.genrationPackageName  + "\\" + classModel.getName() +".java" ;
				File file = new File(generationPath);
				file.delete();
				unitSG.storeToClassPath(generationPath);
				
				
				

				 
				return filePath;
				
	}

	/*
	 * generate a class
	 */
	private static String generateClass(ClassModel classModel, String generationPath) throws ClassLoadException {
		 UnitSourceGenerator unitSG = UnitSourceGenerator.create(ClassBuilderConfig.genrationPackageName);
			//add imports
			addImports(classModel.getImports(),unitSG);
			

		// class name
		String className = classModel.getName();
		TypeDeclarationSourceGenerator type = TypeDeclarationSourceGenerator.create(className);
		ClassSourceGenerator newClass = ClassSourceGenerator.create(type);
		// class visibility
		newClass.addModifier(getModifier(classModel.getClassVisibility()));
		// inheritance semantics
		String inheritanceSemantics = classModel.getInheritanceSemantics();
		if(inheritanceSemantics!=null && !inheritanceSemantics.isEmpty())
		{
			newClass.addModifier(getModifier(inheritanceSemantics));
		}

		


		// attributes
		if (classModel.getAttributes() != null) {
			for (FieldModel field : classModel.getAttributes()) {
				//TODO: all attributes are public because the data def rest api doesn't provide the attribute's modifier
				newClass.addField(generateField(field).addModifier(getModifier(field.getFieldVisibility())));
				String attributeName = field.getName();
				String capitalizedAttributeName = JEStringUtils.capitalize(attributeName);
				Class<?> attributeType = getType(field.getType());
				
				//TODO: all attributes should have a getter ( 
				//adding setters
					FunctionSourceGenerator setter = FunctionSourceGenerator.create("set" + capitalizedAttributeName)
							.addParameter(VariableSourceGenerator.create(attributeType, attributeName))
							.setReturnType(TypeDeclarationSourceGenerator.create(void.class))
							.addModifier(Modifier.PUBLIC)
							.addAnnotation(new AnnotationSourceGenerator("JsonProperty(\""+attributeName+"\")"))
							.addBodyCodeLine("this." + attributeName + "=" + attributeName + ";");
					newClass.addMethod(setter);
				
				//adding getters
					FunctionSourceGenerator getter = FunctionSourceGenerator.create("get" + capitalizedAttributeName)
							.setReturnType(TypeDeclarationSourceGenerator.create(attributeType))
							.addAnnotation(new AnnotationSourceGenerator("JsonProperty(\""+attributeName+"\")"))
							.addModifier(Modifier.PUBLIC).addBodyCodeLine("return this." + attributeName + ";");
					newClass.addMethod(getter);
				}

			
		}
		// methods
		if (classModel.getMethods() != null) {
			for (MethodModel methodModel : classModel.getMethods()) {
				String methodName = methodModel.getMethodName();
				String methodReturnType = methodModel.getReturnType();
				String methodModifier = methodModel.getMethodVisibility();
				FunctionSourceGenerator method = FunctionSourceGenerator.create(methodName);
				method.addModifier(getModifier(methodModifier));
				method.setReturnType(getType(methodReturnType));
				if(methodModel.getMethodScope() != null) {
					method.addModifier(getModifier(methodModel.getMethodScope()));
				}
				method.addBodyCode(methodModel.getCode());
				if(methodModel.getInputs() != null) {
					for (FieldModel parameter : methodModel.getInputs()) {
						method.addParameter(generateField(parameter));
					}
				}
				newClass.addMethod(method);
			}
		}

		// add inherited classes
		List<String> inheritedClass = new ArrayList<String>();

		List<String> inheritedInterfaces = new ArrayList<String>();

		if (classModel.getBaseTypes() == null || classModel.getBaseTypes().isEmpty()) {
			newClass.expands(JEObject.class);
		} else {
			for(String classId : classModel.getBaseTypes())
			{
				if(ClassManager.getClassType(classId) == ClassType.CLASS) {
					inheritedClass.add(classId) ;
					break;}
				if(ClassManager.getClassType(classId) == ClassType.INTERFACE) {
					inheritedInterfaces.add(classId);
				}	//count number of inherited class
				
			    if(ClassManager.getClassType(classId) == ClassType.ENUM) {
					JELogger.error(ClassBuilder.class, ClassBuilderErrors.invalidClassFormat + " : " + ClassBuilderErrors.enumAsInheritedClass);
			    	throw new ClassLoadException(ClassBuilderErrors.invalidClassFormat + " : " + ClassBuilderErrors.enumAsInheritedClass); }
				
				
			}
			// extend class
			if(inheritedClass.isEmpty())
			{
				newClass.expands(JEObject.class);
				
			}
			//multiple inheritance not supported
			else if (inheritedClass.size()>1)
			{	
				JELogger.error(ClassBuilder.class, ClassBuilderErrors.invalidClassFormat + " : " + ClassBuilderErrors.multipleInheritance);
				throw new ClassLoadException(ClassBuilderErrors.invalidClassFormat + " : " + ClassBuilderErrors.multipleInheritance);
			}
			//if base types contains class 
			else if (inheritedClass.size()==1)
			{
				newClass.expands(ClassManager.getClassById(inheritedClass.get(0)));
			}
			 // implement interfaces
			for(String id:inheritedInterfaces)
			{
				newClass.addConcretizedType(ClassManager.getClassById(id));
			}

		}

		// store class
		unitSG.addClass(newClass);
		String filePath= generationPath + "\\" + ClassBuilderConfig.genrationPackageName  + "\\" + className +".java" ;
		File file = new File(generationPath);
		file.delete();
		unitSG.storeToClassPath(generationPath);
		
		
		

		 
		return filePath;
		
	}

	/*
	 * returns the class type based on a string defining the type
	 */
	private static Class<?> getType(String type) throws ClassLoadException {
		Class<?> classType = null;
		switch (type) {
		case "BYTE" :
			classType =  byte.class;
			break;
		case "SBYTE":
			classType =  byte.class;
			break;
		case "INT":
			classType =  int.class;
			break;
		case "SHORT":
			classType =  short.class;
			break;
		case "LONG":
			classType =  long.class;
			break;
		case "FLOAT":
			classType =  float.class;
			break;
		case "DOUBLE":
			classType =  double.class;
			break;
		case "CHAR":
			classType =  char.class;
			break;
		case "BOOL":
			classType =  boolean.class;
			break;
		case "OBJECT":
			classType =  Object.class;
			break;
		case "STRING":
			classType =  String.class;
			break;
		case "DATETIME":
			classType =  LocalDateTime.class;
			break;
		case "VOID":
			classType =  void.class;
			break;
		default:
			break; //add default value
		}
		if(classType==null)
		{
			if(ClassManager.classExistsByName(type))
			{
				classType = ClassManager.getClassByName(type);
			}
			else
			{
				// TODO: throw exception : type not found
				JELogger.error(ClassBuilder.class, "COULD NOT BUILD CLASS : UNKNOWN TYPE");
				throw new ClassLoadException("COULD NOT BUILD CLASS : UNKNOWN TYPE : " + type);
			}
		}
		
		return classType;
	}

	private static VariableSourceGenerator generateField(FieldModel fieldModel) throws ClassLoadException {
		// TODO: if field not valid throw exception

		return VariableSourceGenerator.create(getType(fieldModel.getType()), fieldModel.getName());

	}

	/*
	 * returns the modifier based on a string defining the modifier
	 * TODO: get list of all modifiers available in the data model api
	 */
	private static int getModifier(String modifier) {
		int value = Modifier.PUBLIC;
		switch (modifier) {
		case "PUBLIC":
			break;
		case "PRIVATE":
			value = Modifier.PRIVATE;
			break;
		case "PROTECTED":
			value = Modifier.PROTECTED;
			break;
		case "ABSTRACT":
			value = Modifier.ABSTRACT;
			break;
		case "STATIC":
			value = Modifier.STATIC;
			break;
		default:
			// TODO: throw except
		}
		return value;
	}

}
