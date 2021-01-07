package io.je.classbuilder.builder;

import java.lang.reflect.Modifier;
import java.util.List;

import org.burningwave.core.classes.ClassSourceGenerator;
import org.burningwave.core.classes.FunctionSourceGenerator;
import org.burningwave.core.classes.TypeDeclarationSourceGenerator;
import org.burningwave.core.classes.UnitSourceGenerator;
import org.burningwave.core.classes.VariableSourceGenerator;

import io.je.classbuilder.config.ClassBuilderConfig;
import io.je.classbuilder.models.ClassModel;
import io.je.classbuilder.models.FieldModel;
import io.je.classbuilder.models.MethodModel;
import io.je.utilities.constants.ClassBuilderErrors;
import io.je.utilities.exceptions.ClassFormatInvalidException;
import io.je.utilities.runtimeobject.JEObject;
import io.je.utilities.string.JEStringUtils;

/*
 * build class from class definition
 */
public class ClassBuilder {

	private static UnitSourceGenerator unitSG = UnitSourceGenerator.create(ClassBuilderConfig.genrationFolderName);

	/*
	 * build .java class/interface/enum from classModel
	 * returns  path where file was created
	 */
	public static String buildClass(ClassModel classModel, String generationPath) throws ClassFormatInvalidException {
		
		//check if class format is valid
		if(classModel.getName()==null)
		{
			throw new ClassFormatInvalidException(ClassBuilderErrors.classNameNull);
		}
		
		//load inherited classes
		if(classModel.getBaseTypes()!=null && !classModel.getBaseTypes().isEmpty())
		{
			//if class not found in unit , throw exception
		}
		
		//load dependent classes
		if(classModel.getDependentEntities()!=null && !classModel.getDependentEntities().isEmpty())
		{
			//if class not found in unit , throw exception
		}
		
		
		
		//add imports
		if (classModel.getImports() != null && !classModel.getImports().isEmpty()) {
			addImports(classModel.getImports());
		}

		
		//generate class
		if (classModel.getIsClass()) {
			return  generateClass(classModel, generationPath);
			

		}

		//generate interface
		if (classModel.getIsInterface()) {
			generateInterface(classModel, generationPath);
			
		}
		return null;

	}

	/* add imports */
	private static void addImports(List<String> imports) {
		for (String import_ : imports) {
			unitSG.addImport(import_);
		}
	}

	/*
	 * generate an interface
	 */
	private static void generateInterface(ClassModel classModel, String generationPath) {
		// class name
		String interfaceName = classModel.getName();
		TypeDeclarationSourceGenerator type = TypeDeclarationSourceGenerator.create(interfaceName);
		ClassSourceGenerator newInterface = ClassSourceGenerator.createInterface(type);
		// class modifier
		newInterface.addModifier(getModifier(classModel.getClassVisibility()));

		// TODO: add interface methods
	}

	/*
	 * generate a class
	 */
	private static String generateClass(ClassModel classModel, String generationPath) {

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
				newClass.addField(generateField(field));
				String attributeName = field.getName();
				String capitalizedAttributeName = JEStringUtils.capitalize(attributeName);
				Class<?> attributeType = getType(field.getType());
				
				if (field.getHasSetter()) {
					FunctionSourceGenerator setter = FunctionSourceGenerator.create("set" + capitalizedAttributeName)
							.addParameter(VariableSourceGenerator.create(attributeType, attributeName))
							.setReturnType(TypeDeclarationSourceGenerator.create(void.class))
							.addModifier(Modifier.PUBLIC)
							.addBodyCodeLine("this." + attributeName + "=" + attributeName + ";");
					newClass.addMethod(setter);
				}
				if (field.getHasGetter()) {
					FunctionSourceGenerator getter = FunctionSourceGenerator.create("get" + capitalizedAttributeName)
							.setReturnType(TypeDeclarationSourceGenerator.create(attributeType))
							.addModifier(Modifier.PUBLIC).addBodyCodeLine("return this." + attributeName + ";");
					newClass.addMethod(getter);
				}

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
				method.addBodyCode(methodModel.getCode());
				for (FieldModel parameter : methodModel.getInputs()) {
					method.addParameter(generateField(parameter));
				}
			}
		}

		// add inherited classes
		if (classModel.getBaseTypes() == null || classModel.getBaseTypes().isEmpty()) {
			newClass.expands(JEObject.class);
		} else {
			// if all base types are interfaces: add them + add JEObject
			// if more than 1 base type class, throw exception

		}

		// store class
		unitSG.addClass(newClass);

		unitSG.storeToClassPath(generationPath);

		String filePath= generationPath + "\\" + ClassBuilderConfig.genrationFolderName  + "\\" + className +".java" ;
		return filePath;
		
	}

	/*
	 * returns the class type based on a string defining the type
	 */
	private static Class<?> getType(String type) {
		Class<?> classType = Integer.class;
		switch (type) {
		// TODO : assign type
		}
		return classType;
	}

	private static VariableSourceGenerator generateField(FieldModel fieldModel) {
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
		case "public":
			break;
		case "private":
			value = Modifier.PRIVATE;
			break;
		case "protected":
			value = Modifier.PROTECTED;
			break;
		default:
			// TODO: throw except
		}
		return value;
	}

}
