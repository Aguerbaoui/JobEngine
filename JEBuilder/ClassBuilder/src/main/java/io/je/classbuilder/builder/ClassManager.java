package io.je.classbuilder.builder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.classbuilder.models.ClassDefinition;
import io.je.classbuilder.models.FieldModel;
import io.je.classbuilder.models.GetModelObject;
import io.je.classbuilder.models.MethodModel;
import io.je.utilities.beans.*;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.execution.CommandExecutioner;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.LibModel;
import io.siothconfig.SIOTHConfigUtility;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;
import utils.zmq.ZMQRequester;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Class to manage user defined Models from datamodel ClassDefinition
 */
public class ClassManager {

    static Map<String, JEClass> jeClasses = new ConcurrentHashMap<>(); // key = is, value = jeclass
    static Map<String, Class<?>> builtClasses = new ConcurrentHashMap<>(); // key = id , value = class

    /* FIXME not used?
    static ZMQPublisher publisher = new ZMQPublisher("tcp://" + SIOTHConfigUtility.getSiothConfig()
            .getNodes()
            .getSiothMasterNode(),
            SIOTHConfigUtility.getSiothConfig()
                    .getPorts()
                    .getTrackingPort());

     */

    static ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // TODO: see with islem if possible to change field type to class id instead of name
    static Map<String, String> classNames = new ConcurrentHashMap<>(); // key = name, value = classid
    //static ClassLoader classLoader =   //ClassManager.class.getClassLoader();
    //static String loadPath = ConfigurationConstants.JAVA_GENERATION_PATH;
    static String generationPath = ConfigurationConstants.JAVA_GENERATION_PATH;

    /**
     * build class (generate .java then load Class )
     */
    public static List<JEClass> buildClass(ClassDefinition classDefinition, String packageName)
            throws Exception {

        JELogger.debug(JEMessages.BUILDING_CLASS + "[className = " + classDefinition.getName() + "]",
                LogCategory.DESIGN_MODE, null,
                LogSubModule.CLASS, null);
        ArrayList<JEClass> classes = new ArrayList<>();

        /*
         * if(!builtClasses.containsKey(classModel.getIdClass()) ||
         * classModel.getWorkspaceId() == null) {
         */
        // load class type ( interface/enum or class)
        ClassType classType = getClassType(classDefinition);

        // build inherited classes
  /*      if (classDefinition.getBaseTypes() != null && !classDefinition.getBaseTypes()
                .isEmpty()) {
            for (String baseTypeId : classDefinition.getBaseTypes()) {
                try {
                    ClassDefinition inheritedClassModel = loadClassDefinition(classDefinition.getWorkspaceId(), baseTypeId);
                    // load inherited class
                    for (JEClass _class : ClassManager.buildClass(inheritedClassModel, packageName)) {
                        classes.add(_class);
                    }
                } catch (Exception e) {
                    JELogger.debug(JEMessages.CLASS_LOAD_FAILED + "[" + classDefinition.getName() + "] : " + e.getMessage(), LogCategory.RUNTIME, "", LogSubModule.CLASS,
                            baseTypeId);
                    throw new ClassLoadException(
                            JEMessages.CLASS_LOAD_FAILED + "[" + classDefinition.getName() + "]" + e.getMessage() + e.getMessage());

                }
            }

        }*/

        // build dependent classes
        if (classDefinition.getDependentEntities() != null && !classDefinition.getDependentEntities()
                .isEmpty()) {
            for (String baseTypeId : classDefinition.getDependentEntities()) {
                try {
                    ClassDefinition dependentClass = loadClassDefinition(classDefinition.getWorkspaceId(), baseTypeId);
                    for (JEClass _class : ClassManager.buildClass(dependentClass, packageName)) {
                        classes.add(_class);
                    }
                } catch (Exception e) {
                    JELogger.debug(JEMessages.CLASS_LOAD_FAILED + "[" + classDefinition.getName() + "]", LogCategory.RUNTIME, "", LogSubModule.CLASS,
                            baseTypeId);
                    throw new ClassLoadException(
                            JEMessages.CLASS_LOAD_FAILED + "[" + classDefinition.getName() + "]" + e.getMessage());

                }
            }
        }

        // create .java
        String filePath = ClassBuilder.buildClass(classDefinition, generationPath, JEClassLoader.getJobEnginePackageName(packageName));

        // load .java -> .class
        //JEClassCompiler.compileClass(filePath, FileUtilities.getPathPrefix(generationPath));
        JELogger.debug(JEMessages.BUILDING_CLASS + "Compiling Code " + "[className = " + classDefinition.getName() + "]",
                LogCategory.DESIGN_MODE, null,
                LogSubModule.CLASS, null);

        CommandExecutioner.compileCode(filePath, true);

        JELogger.debug(JEMessages.BUILDING_CLASS + "JAR " + "[className = " + classDefinition.getName() + "]",
                LogCategory.DESIGN_MODE, null,
                LogSubModule.CLASS, null);

        CommandExecutioner.buildJar();

        // Load the target class using its binary name
        Class<?> loadedClass;
        String className = JEClassLoader.getJobEnginePackageName(ClassBuilderConfig.CLASS_PACKAGE) + "." + classDefinition.getName();
        try {
            JEClassLoader.overrideDataModelInstance(className);
            JEClassLoader.addClassToDataModelClassesSet(className);
            loadedClass = JEClassLoader.getDataModelInstance()
                    .loadClass(className);
        } catch (ClassNotFoundException e) {

            LoggerUtils.logException(e);

            JEClassLoader.removeClassFromDataModelClassesSet(className);
            throw new ClassLoadException(
                    JEMessages.CLASS_LOAD_FAILED + "[" + classDefinition.getName() + "]" + e.getMessage());
        }

        builtClasses.put(classDefinition.getClassId(), loadedClass);
        classNames.put(classDefinition.getName(), classDefinition.getClassId());
        JEClass jeClass = new JEClass(classDefinition.getWorkspaceId(), classDefinition.getClassId(),
                classDefinition.getName(), filePath, classType);
        jeClass.setClassAuthor(classDefinition.getClassAuthor());
        if (classDefinition.getMethods() != null) {
            for (MethodModel m : classDefinition.getMethods()) {
                jeClass.getMethods()
                        .put(m.getId() == null
                                ? m.getMethodName() : m.getId(), getMethodFromModel(m));
            }
        }
        jeClasses.put(classDefinition.getClassId(), jeClass);
        classes.add(jeClass);
        // }

        return classes;

    }

    /**
     * Get method model from bean
     */
    public static JEMethod getMethodFromModel(MethodModel m) {
        JEMethod method = new JEMethod();
        method.setCode(m.getCode());
        method.setReturnType(m.getReturnType());
        method.setJobEngineElementID(m.getId());
        method.setJobEngineElementName(m.getMethodName());
        method.setJeObjectCreatedBy(m.getCreatedBy());
        method.setJeObjectModifiedBy(m.getModifiedBy());
        method.setJeObjectLastUpdate(Instant.now());
        method.setJeObjectCreationDate(Instant.now());
        method.setInputs(new ArrayList<>());
        if (m.getInputs() != null) {
            for (FieldModel f : m.getInputs()) {
                method.getInputs()
                        .add(getFieldFromModel(f));
            }
        }
        method.setImports(m.getImports());
        return method;
    }

    /**
     * Get bean from model
     */
    public static JEField getFieldFromModel(FieldModel f) {
        JEField field = new JEField();
        field.setComment("");
        field.setVisibility(f.getFieldVisibility());
        field.setType(f.getType());
        field.setName(f.getName());
        return field;
    }

    /**
     * load class definition from data definition API
     */
    public static ClassDefinition loadClassDefinition(String workspaceId, String classId)
            throws ClassLoadException {

        ClassDefinition jeClass = null;

        String response = requestClassDefinition(workspaceId, classId);
        // create class model from response
        if (response != null && !response.equals("null")) {
            try {
                jeClass = objectMapper.readValue(response, ClassDefinition.class);
                jeClass.setClassAuthor(ClassAuthor.DATA_MODEL);
                // jeClass.setWorkspaceId(workspaceId);

            } catch (Exception e) {
                LoggerUtils.logException(e);
                throw new ClassLoadException(JEMessages.FAILED_TO_SEND_LOG_MESSAGE_TO_THE_LOGGING_SYSTEM + response);
            }


        } else {
            JELogger.warn("[class=" + classId + "]" + JEMessages.CLASS_NOT_FOUND, null, null, LogSubModule.CLASS, classId);
        }

        // set workspace id

        return jeClass;
    }


    /*
     * Request class definition from Data Model RESTAPI via ZMQ
     */
    private static String requestClassDefinition(String workspaceId, String classId) {

        String response = null;
        try {
            GetModelObject request = new GetModelObject(classId, workspaceId);
            String jsonMsg = objectMapper.writeValueAsString(request);

            JELogger.debug(JEMessages.SENDING_REQUEST_TO_DATA_MODEL + "[" + "tcp://" + SIOTHConfigUtility.getSiothConfig()
                            .getNodes()
                            .getSiothMasterNode() + ":" + SIOTHConfigUtility.getSiothConfig()
                            .getDataModelPORTS()
                            .getDmRestAPI_ReqAddress() + " ] : " + request.toString(),
                    LogCategory.DESIGN_MODE, null,
                    LogSubModule.JEBUILDER, null);

            ZMQRequester requester = new ZMQRequester("tcp://" + SIOTHConfigUtility.getSiothConfig()
                    .getNodes()
                    .getSiothMasterNode(), SIOTHConfigUtility.getSiothConfig()
                    .getDataModelPORTS()
                    .getDmRestAPI_ReqAddress());

            response = requester.sendRequest(jsonMsg);
            if (response == null) {

                //Data Model RESTAPI unreachable
                JELogger.error(JEMessages.DATAMODELAPI_UNREACHABLE,
                        LogCategory.DESIGN_MODE, null,
                        LogSubModule.JEBUILDER, null);
                throw new ClassNotFoundException(JEMessages.DATAMODELAPI_UNREACHABLE);
            } else if (response.isEmpty()) {
                //Data Model RESTAPI Error -> check its logs for more details
                JELogger.error(JEMessages.CLASS_NOT_FOUND,
                        LogCategory.DESIGN_MODE, null,
                        LogSubModule.JEBUILDER, null);
                throw new ClassNotFoundException(JEMessages.CLASS_NOT_FOUND + classId);
            } else {
                JELogger.debug(JEMessages.DMAPI_RESPONSE + response,
                        LogCategory.DESIGN_MODE, null,
                        LogSubModule.JEBUILDER, null);
            }

        } catch (Exception e) {
            JELogger.error(JEMessages.FAILED_TO_SEND_LOG_MESSAGE_TO_THE_LOGGING_SYSTEM + e.getMessage(),
                    LogCategory.DESIGN_MODE, null,
                    LogSubModule.JEBUILDER, null);
        }
        return response;
    }

    /*
     * Check if class exists
     * */
    public static boolean classExistsByName(String className) {
        return classNames.containsKey(className);
    }

    /*
     * Get class by name
     * */
    public static Class<?> getClassByName(String className) {
        return builtClasses.get(classNames.get(className));
    }

    /*
     * check if class model is a class, interface or enum
     */
    private static ClassType getClassType(ClassDefinition classDefinition) throws ClassLoadException {
        if (classDefinition.getIsClass()) {
            if (classDefinition.getIsInterface() || classDefinition.getIsEnum()) {
                throw new ClassLoadException("[" + classDefinition.getName() + "]:" + JEMessages.INVALID_CLASS_FORMAT
                        + "\n" + JEMessages.UNKNOW_CLASS_TYPE);
            }
            return ClassType.CLASS;

        } else if (classDefinition.getIsInterface()) {
            if (classDefinition.getIsClass() || classDefinition.getIsEnum()) {
                throw new ClassLoadException("[" + classDefinition.getName() + "]:" + JEMessages.INVALID_CLASS_FORMAT
                        + "\n" + JEMessages.UNKNOW_CLASS_TYPE);
            }
            return ClassType.INTERFACE;

        } else if (classDefinition.getIsEnum()) {
            if (classDefinition.getIsInterface() || classDefinition.getIsClass()) {
                throw new ClassLoadException("[" + classDefinition.getName() + "]:" + JEMessages.INVALID_CLASS_FORMAT
                        + "\n" + JEMessages.UNKNOW_CLASS_TYPE);
            }
            return ClassType.ENUM;

        } else {
            throw new ClassLoadException("[" + classDefinition.getName() + "]:" + JEMessages.INVALID_CLASS_FORMAT + "\n"
                    + JEMessages.UNKNOW_CLASS_TYPE);

        }

    }

    /*
     * Get class type
     * */
    public static ClassType getClassType(String classId) {
        return jeClasses.get(classId)
                .getClassType();

    }

    /*
     * Get class by id
     * */
    public static Class<?> getClassById(String id) {
        return builtClasses.get(id);
    }

    /*
     * Get ClassDefinition model from JEClass bean
     * */
    public static ClassDefinition getClassModel(JEClass clazz) {
        ClassDefinition c = new ClassDefinition();
        c.setClass(true);
        c.setClassId(clazz.getClassId());
        c.setName(clazz.getClassName());
        c.setClassVisibility("public");
        c.setClassAuthor(clazz.getClassAuthor());
        List<MethodModel> methodModels = new ArrayList<>();
        for (JEMethod method : clazz.getMethods()
                .values()) {
            if (method.isCompiled())
                methodModels.add(getMethodModel(method));
            if (method.getImports() != null && !method.getImports()
                    .isEmpty()) {
                c.getImports()
                        .addAll(method.getImports());
            }
        }
        c.setMethods(methodModels);

        //Compile class first
        return c;
    }

    /*
     * Get Method model from JEMethod bean
     * */
    public static MethodModel getMethodModel(JEMethod method) {
        MethodModel m = new MethodModel();
        m.setMethodVisibility(WorkflowConstants.PUBLIC);
        m.setReturnType(method.getReturnType());
        m.setCode(method.getCode());
        m.setMethodName(method.getJobEngineElementName());
        m.setMethodScope(WorkflowConstants.STATIC);
        m.setId(method.getJobEngineElementID());
        m.setModifiedAt(method.getJeObjectLastUpdate()
                .toString());
        m.setCreatedBy(method.getJeObjectCreatedBy());
        m.setCreatedAt(method.getJeObjectCreationDate()
                .toString());
        m.setModifiedBy(method.getJeObjectModifiedBy());
        List<FieldModel> fieldModels = new ArrayList<>();
        for (JEField f : method.getInputs()) {
            fieldModels.add(getFieldModel(f));
        }
        m.setInputs(fieldModels);
        m.setImports(method.getImports());
        return m;
    }

    /*
     * Get Field model from JEField bean
     * */
    public static FieldModel getFieldModel(JEField f) {
        FieldModel fieldModel = new FieldModel();
        fieldModel.setFieldVisibility(WorkflowConstants.PUBLIC);
        fieldModel.setName(f.getName());
        fieldModel.setType(f.getType());
        return fieldModel;
    }

    /*
     * Get LibModel model from JELib bean
     * */
    public static LibModel getLibModel(JELib lib) {
        LibModel model = new LibModel();
        model.setFileName(lib.getJobEngineElementName());
        model.setCreatedBy(lib.getJeObjectCreatedBy());
        model.setFilePath(lib.getFilePath());
        model.setCreatedAt(lib.getJeObjectCreationDate()
                .toString());
        model.setId(lib.getJobEngineElementID());
        return model;
    }

}
