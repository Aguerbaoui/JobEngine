package io.je.classbuilder.builder;

import io.je.classbuilder.models.ClassDefinition;
import io.je.classbuilder.models.FieldModel;
import io.je.classbuilder.models.MethodModel;
import io.je.utilities.beans.ClassAuthor;
import io.je.utilities.beans.ClassType;
import io.je.utilities.beans.UnifiedType;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.AddClassException;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.log.JELogger;
import io.je.utilities.runtimeobject.JEObject;
import org.burningwave.core.assembler.ComponentContainer;
import org.burningwave.core.assembler.ComponentSupplier;
import org.burningwave.core.classes.*;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.string.StringUtilities;

import java.io.File;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static io.je.utilities.config.ConfigurationConstants.getJobEngineCustomImport;
import static io.je.utilities.constants.ClassBuilderConfig.CLASS_PACKAGE;
import static io.je.utilities.constants.ClassBuilderConfig.SCRIPTS_PACKAGE;
import static utils.files.FileUtilities.getPathWithSeparator;
import static utils.files.FileUtilities.getSeparator;

/**
 * build java class from class definition using burning wave
 *
 * @see <a href="https://github.com/burningwave/core/blob/master/src/test/java/org/burningwave/core/UnitSourceGeneratorTest.java#L153">Burning Wave example</a>
 */
public class ClassBuilder {


    /*
     * build .java class/interface/enum from classModel
     * returns  path where file was created
     */
    public static String buildClass(ClassDefinition classDefinition, String generationPath, String packageName) throws AddClassException, ClassLoadException {

        //check if class format is valid
        if (classDefinition.getName() == null) {
            JELogger.error(JEMessages.CLASS_NAME_NULL, LogCategory.DESIGN_MODE, null, LogSubModule.CLASS, classDefinition.getClassId());

            throw new AddClassException(JEMessages.CLASS_NAME_NULL);
        }


        //generate class
        if (classDefinition.getIsClass()) {
            return generateClass(classDefinition, generationPath, packageName);


        }

        //generate interface
        if (classDefinition.getIsInterface()) {
            return generateInterface(classDefinition, generationPath);

        }
        return null;

    }

    /**
     * generate a class
     */
    private static String generateClass(ClassDefinition classDefinition, String generationPath, String packageName) throws ClassLoadException {
        UnitSourceGenerator unitSG = UnitSourceGenerator.create(packageName);
        //add imports
        addImports(classDefinition.getImports(), unitSG, classDefinition.getClassAuthor(), classDefinition.isImportDataModelClasses());


        // class name
        String className = classDefinition.getName();
        TypeDeclarationSourceGenerator type = TypeDeclarationSourceGenerator.create(className);
        ClassSourceGenerator newClass = ClassSourceGenerator.create(type);
        // class visibility
        newClass.addModifier(getModifier(classDefinition.getClassVisibility()));
        // inheritance semantics
        String inheritanceSemantics = classDefinition.getInheritanceSemantics();
        if (inheritanceSemantics != null && !inheritanceSemantics.isEmpty()) {
            newClass.addModifier(getModifier(inheritanceSemantics));
        }


        // attributes
        if (classDefinition.getAttributes() != null) {
            for (FieldModel field : classDefinition.getAttributes()) {
                //TODO: all attributes are public because the data def rest api doesn't provide the attribute's modifier
                VariableSourceGenerator newField = generateField(field).addModifier(getModifier(field.getFieldVisibility()));
                if (field.getType() == UnifiedType.DATETIME
                ) {
                    newField.addAnnotation(new AnnotationSourceGenerator("JsonDeserialize(using = LocalDateTimeDeserializer.class)"));
                    newField.addAnnotation(new AnnotationSourceGenerator("JsonSerialize(using = LocalDateTimeSerializer.class)"));
                    //newField.addAnnotation(new AnnotationSourceGenerator("JsonFormat (shape = JsonFormat.Shape.STRING, pattern ="+dataModelDateFormat+")"));
                }
                newClass.addField(newField);
                String attributeName = field.getName();
                String capitalizedAttributeName = StringUtilities.capitalize(attributeName);
                Class<?> attributeType = getType(field.getType());


                //TODO: all attributes should have a getter (
                //adding setters
                FunctionSourceGenerator setter = FunctionSourceGenerator.create("set" + capitalizedAttributeName)
                        .addParameter(VariableSourceGenerator.create(attributeType, attributeName))
                        .setReturnType(TypeDeclarationSourceGenerator.create(void.class))
                        .addModifier(Modifier.PUBLIC)
                        .addAnnotation(new AnnotationSourceGenerator("JsonProperty(\"" + attributeName + "\")"))
                        .addBodyCodeLine("this." + attributeName + "=" + attributeName + ";");
                newClass.addMethod(setter);

                //adding getters
                FunctionSourceGenerator getter = FunctionSourceGenerator.create("get" + capitalizedAttributeName)
                        .setReturnType(TypeDeclarationSourceGenerator.create(attributeType))
                        .addAnnotation(new AnnotationSourceGenerator("JsonProperty(\"" + attributeName + "\")"))
                        .addModifier(Modifier.PUBLIC)
                        .addBodyCodeLine("return this." + attributeName + ";");
                newClass.addMethod(getter);
            }


        }
        // methods
        if (classDefinition.getMethods() != null) {
            for (MethodModel methodModel : classDefinition.getMethods()) {
                String methodName = methodModel.getMethodName();
                String methodReturnType = methodModel.getReturnType();
                String methodModifier = methodModel.getMethodVisibility();
                FunctionSourceGenerator method = FunctionSourceGenerator.create(methodName);
                method.addModifier(getModifier(methodModifier));
                method.setReturnType(TypeDeclarationSourceGenerator.create(getType(UnifiedType.valueOf(methodReturnType))));
                if (methodModel.getMethodScope() != null) {
                    method.addModifier(getModifier(methodModel.getMethodScope()));
                }
                method.addThrowable(TypeDeclarationSourceGenerator.create("Exception"));
                method.addBodyCode(methodModel.getCode());
                if (methodModel.getInputs() != null) {
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

        if (classDefinition.getBaseTypes() == null || classDefinition.getBaseTypes()
                .isEmpty()) {
            newClass.expands(JEObject.class);
        } else {
            for (String classId : classDefinition.getBaseTypes()) {
                if (ClassManager.getClassType(classId) == ClassType.CLASS) {
                    inheritedClass.add(classId);
                    break;
                }
                if (ClassManager.getClassType(classId) == ClassType.INTERFACE) {
                    inheritedInterfaces.add(classId);
                }    //count number of inherited class

                if (ClassManager.getClassType(classId) == ClassType.ENUM) {
                    JELogger.error(JEMessages.INVALID_CLASS_FORMAT + " : " + JEMessages.INHERITED_CLASS_ENUM, LogCategory.DESIGN_MODE, null, LogSubModule.CLASS, classDefinition.getClassId());

                    throw new ClassLoadException(JEMessages.INVALID_CLASS_FORMAT + " : " + JEMessages.INHERITED_CLASS_ENUM);
                }


            }
            // extend class
            if (inheritedClass.isEmpty()) {
                newClass.expands(JEObject.class);

            }
            //multiple inheritance not supported
            else if (inheritedClass.size() > 1) {
                JELogger.error(JEMessages.INVALID_CLASS_FORMAT + " : " + JEMessages.INHERITED_CLASS_ENUM, LogCategory.DESIGN_MODE, null,
                        LogSubModule.CLASS, classDefinition.getClassId());
                throw new ClassLoadException(JEMessages.INVALID_CLASS_FORMAT + " : " + JEMessages.MULTIPLE_INHERITANCE);
            }
            //if base types contains class
            else if (inheritedClass.size() == 1) {
                newClass.expands(ClassManager.getClassById(inheritedClass.get(0)));
            }
            // implement interfaces
            for (String id : inheritedInterfaces) {
                newClass.addConcretizedType(ClassManager.getClassById(id));
            }

        }

        // store class
        unitSG.addClass(newClass);
        ComponentSupplier componentSupplier = ComponentContainer.getInstance();
        ClassFactory classFactory = componentSupplier.getClassFactory();
        ClassFactory.ClassRetriever classRetriever = classFactory.loadOrBuildAndDefine(
                unitSG
        );

        String targetFolder = packageName.contains(CLASS_PACKAGE) ? CLASS_PACKAGE : SCRIPTS_PACKAGE;

        String filePath = getPathWithSeparator(generationPath) + getSeparator() + targetFolder + getSeparator() + className + ".java";

        File file = new File(generationPath);
        file.delete();
        unitSG.storeToClassPath(generationPath);

        return filePath;

    }

    /**
     * generate an interface
     */
    private static String generateInterface(ClassDefinition classDefinition, String generationPath) throws ClassLoadException {
        UnitSourceGenerator unitSG = UnitSourceGenerator.create(CLASS_PACKAGE);
        //add imports
        addImports(classDefinition.getImports(), unitSG, classDefinition.getClassAuthor(), classDefinition.isImportDataModelClasses());
        // class name
        String interfaceName = classDefinition.getName();
        TypeDeclarationSourceGenerator type = TypeDeclarationSourceGenerator.create(interfaceName);
        ClassSourceGenerator newInterface = ClassSourceGenerator.createInterface(type);
        // class modifier
        newInterface.addModifier(getModifier(classDefinition.getClassVisibility()));

        // TODO: add interface methods

        //add inheritance
        String inheritedClass = null;
        List<String> inheritedInterfaces = new ArrayList<String>();

        if (classDefinition.getBaseTypes() != null && !classDefinition.getBaseTypes()
                .isEmpty()) {

            for (String classId : classDefinition.getBaseTypes()) {
                if (ClassManager.getClassType(classId) == ClassType.CLASS) {
                    inheritedClass = classId;
                    break;
                }
                if (ClassManager.getClassType(classId) == ClassType.INTERFACE) {
                    inheritedInterfaces.add(classId);
                }

            }
            // extend class
            if (inheritedClass != null) {
                JELogger.error(JEMessages.INVALID_CLASS_FORMAT, LogCategory.DESIGN_MODE, null, LogSubModule.CLASS, classDefinition.getClassId());
                throw new ClassLoadException(JEMessages.INVALID_CLASS_FORMAT);

            }
            // implement interfaces
            for (String id : inheritedInterfaces) {
                newInterface.addConcretizedType(ClassManager.getClassById(id));
            }

        }

        // store class
        unitSG.addClass(newInterface);
        String filePath = generationPath + "\\" + CLASS_PACKAGE + "\\" + classDefinition.getName() + ".java";
        File file = new File(generationPath);
        file.delete();
        unitSG.storeToClassPath(generationPath);


        return filePath;

    }

    /* add imports */
    private static void addImports(List<String> imports, UnitSourceGenerator unitSG, ClassAuthor classAuthor, Boolean importDataModelClasses) {
        //TODO : remove harcoded imports
        unitSG.addImport("com.fasterxml.jackson.annotation.JsonProperty");
        unitSG.addImport("com.fasterxml.jackson.annotation.JsonFormat");
        unitSG.addImport("com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer");
        unitSG.addImport("com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer");
        unitSG.addImport("com.fasterxml.jackson.databind.annotation.JsonSerialize");
        unitSG.addImport("com.fasterxml.jackson.databind.annotation.JsonDeserialize");
        unitSG.addImport("io.je.utilities.log.JELogger");
        unitSG.addImport("io.je.utilities.execution.*");
        unitSG.addImport("java.lang.*");
        unitSG.addImport("java.util.*");
        unitSG.addImport("java.sql.*");
        unitSG.addImport("javax.sql.*");
        unitSG.addImport("io.je.utilities.execution.*");
        unitSG.addImport("io.je.utilities.models.*");
        if (!classAuthor.equals(ClassAuthor.DATA_MODEL) && Boolean.TRUE.equals(importDataModelClasses)) {
            unitSG.addImport(getJobEngineCustomImport());
        }
        //TODO: add job engine api
        if (imports != null && !imports.isEmpty()) {

            for (String import_ : imports) {
                if (!import_.isEmpty()) {
                    unitSG.addImport(import_);
                }
            }
        }

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

    /*
     * Generate field
     * */
    private static VariableSourceGenerator generateField(FieldModel fieldModel) throws ClassLoadException {
        // TODO: if field not valid throw exception

        return VariableSourceGenerator.create(getType(fieldModel.getType()), fieldModel.getName());

    }

    /**
     * returns the class type based on a string defining the type
     */
    private static Class<?> getType(UnifiedType type) throws ClassLoadException {
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
            case STRING_LIST:
                classType = String[].class;
                break;
            case OBJECT_LIST:
                classType = Object[].class;
                break;
            case UINT16_LIST:
            case INT32_LIST:
            case INT_LIST:
                classType = Integer[].class;
                break;
            case UINT64_LIST:
            case FLOAT_LIST:
            case SINGLE_LIST:
                classType = Float[].class;
                break;
            case UINT32_LIST:
            case INT64_LIST:
            case LONG_LIST:
                classType = Long[].class;
                break;
            case DOUBLE_LIST:
                classType = Double[].class;
                break;
            case SBYTE_LIST:
                classType = Byte[].class;
                break;
            case CHAR_LIST:
                classType = char[].class;
                break;
            case BOOL_LIST:
                classType = Boolean[].class;
                break;
            case BYTE_LIST:
            case INT16_LIST:
            case SHORT_LIST:
                classType = Short[].class;
                break;

            case DATETIME_LIST:
                classType = LocalDateTime[].class;
                break;
            case LIST:
                classType = ArrayList.class;
                break;
            case VOID:
                classType = void.class;
                break;
            default:
                break; //add default value
        }
        if (classType == null) {
            if (ClassManager.classExistsByName(String.valueOf(type))) {
                classType = ClassManager.getClassByName(String.valueOf(type));
            } else {
                // TODO: throw exception : type not found
                JELogger.error(JEMessages.TYPE_UNKNOWN, LogCategory.DESIGN_MODE, null,
                        LogSubModule.CLASS, null);
                throw new ClassLoadException(JEMessages.CLASS_BUILD_FAILED + ":" + JEMessages.UNKNOW_CLASS_TYPE + type);
            }
        }

        return classType;
    }


}
