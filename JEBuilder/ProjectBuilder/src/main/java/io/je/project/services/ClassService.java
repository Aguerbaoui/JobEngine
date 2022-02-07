package io.je.project.services;

import static io.je.classbuilder.builder.ClassManager.getClassModel;
import static io.je.classbuilder.builder.ClassManager.getLibModel;
import static io.je.utilities.constants.JEMessages.CLASS_LOAD_IN_RUNNER_FAILED;
import static io.je.utilities.constants.JEMessages.EMPTY_CODE;
import static io.je.utilities.constants.JEMessages.FAILED_TO_DELETE_FILES;
import static io.je.utilities.constants.JEMessages.PROCEDURE_SHOULD_CONTAIN_CODE;
import static io.je.utilities.constants.WorkflowConstants.EXECUTE_SCRIPT;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import io.je.utilities.beans.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.je.classbuilder.builder.ClassManager;
import io.je.classbuilder.entity.ClassType;
import io.je.classbuilder.entity.JEClass;
import io.je.classbuilder.models.ClassDefinition;
import io.je.classbuilder.models.FieldModel;
import io.je.classbuilder.models.MethodModel;
import io.je.project.listener.ClassUpdateListener;
import io.je.project.repository.ClassRepository;
import io.je.project.repository.LibraryRepository;
import io.je.project.repository.MethodRepository;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.AddClassException;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.LibraryException;
import io.je.utilities.exceptions.MethodException;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.LibModel;
import io.siothconfig.SIOTHConfigUtility;
import utils.files.FileUtilities;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.string.StringUtilities;

/*
 * Service class to handle classes
 */
@Service
public class ClassService {

    public static final String CLASS_NAME = "className";
    public static final String CLASS_PATH = "classPath";
    public static final String CLASS_ID = "classId";
    public static final String CLASS_AUTHOR = "classAuthor";

    @Autowired
    ClassRepository classRepository;

    @Autowired
    LibraryRepository libraryRepository;

    @Autowired
    MethodRepository methodRepository;

    @Autowired
    @Lazy
    ProjectService projectService;

    Map<String, JEClass> loadedClasses = new HashMap<String, JEClass>();

    @Autowired
    private HttpServletRequest request;

    /*****************************************************
     * Class Listener
     ***********************************************************************/

    /*
     * Init a thread that listens to the DataModelRestApi for class definition
     * updates
     */
    public void initClassUpdateListener() {
        // TODO make runnable static
        ClassUpdateListener runnable = new ClassUpdateListener(
                "tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(),
                SIOTHConfigUtility.getSiothConfig().getDataModelPORTS().getDmRestAPI_ConfigurationPubAddress(),
                "ModelTopic");
        runnable.setListening(true);
        Thread listener = new Thread(runnable);
        listener.start();

    }

    /*****************************************************
     * CRUD
     ***********************************************************************/

    /*
     * Add Class from Class definition
     */
    public void addClass(ClassDefinition classDefinition, boolean sendToRunner, boolean reloadClassDefinition)
            throws AddClassException, ClassLoadException {
        if (reloadClassDefinition) {
            JEClassLoader.overrideJeInstance();
        }
        List<JEClass> builtClasses = ClassManager.buildClass(classDefinition);
        for (JEClass _class : builtClasses) {
            if (sendToRunner) {
                addClassToJeRunner(_class, reloadClassDefinition);
                classRepository.save(_class);
            } else {
                try {
                    FileUtilities.deleteFileFromPath(_class.getClassPath());

                } catch (Exception e) {
                    JELogger.error(FAILED_TO_DELETE_FILES, LogCategory.DESIGN_MODE, "", LogSubModule.CLASS,
                            classDefinition.getName());
                }
            }

        }

    }

    /*
     * Add class to runner from datamodel
     */
    public void addClass(String workspaceId, String classId, boolean sendToRunner)
            throws ClassLoadException, AddClassException {
        ClassDefinition classDefinition = ClassManager.loadClassDefinition(workspaceId, classId);

        if (!loadedClasses.containsKey(classId) && classDefinition != null) {
            classDefinition.setWorkspaceId(workspaceId);
            addClass(classDefinition, sendToRunner, false);
            JELogger.info("Class " + classDefinition.getName() + " loaded successfully.", null, null, null,
                    classDefinition.getName());
        }

    }

    /*
     * Add Class from Class definition
     */
    /*
     * public List<JEClass> updateClass(ClassDefinition classDefinition, boolean
     * sendToRunner)
     * throws AddClassException, ClassLoadException {
     * List<JEClass> builtClasses = ClassManager.buildClass(classDefinition);
     * for (JEClass _class : builtClasses) {
     * if (sendToRunner) {
     * addClassToJeRunner(_class, false);
     * }
     * classRepository.save(_class);
     * loadedClasses.put(_class.getClassId(), _class);
     * }
     * return builtClasses;
     * 
     * }
     */
    /*
     * public void sendClassesToJeRunner(Collection<JEClass> collection)
     * throws AddClassException {
     * ArrayList<HashMap> classesList = new ArrayList<>();
     * JEResponse jeRunnerResp;
     * for (JEClass clazz : collection) {
     * HashMap<String, String> classMap = new HashMap<>();
     * classMap.put(CLASS_NAME, clazz.getClassName());
     * classMap.put(CLASS_PATH, clazz.getClassPath());
     * classMap.put(CLASS_ID, clazz.getClassId());
     * classesList.add(classMap);
     * }
     * JELogger.debug(JEMessages.ADDING_CLASSES_TO_RUNNER_FROM_BUILDER,
     * LogCategory.DESIGN_MODE, null, LogSubModule.CLASS, null);
     * 
     * try {
     * jeRunnerResp = JERunnerAPIHandler.addClasses(classesList);
     * } catch (JERunnerErrorException e) {
     * throw new AddClassException(CLASS_LOAD_IN_RUNNER_FAILED);
     * }
     * if (jeRunnerResp.getCode() != ResponseCodes.CODE_OK) {
     * throw new AddClassException(JEMessages.CLASS_LOAD_FAILED);
     * }
     * }
     */

    /*
     * send class to je runner to be loaded there
     *
     */
    public void addClassToJeRunner(JEClass clazz, boolean reloadClassDefinition)
            throws AddClassException {
        HashMap<String, String> classMap = new HashMap<>();
        classMap.put(CLASS_NAME, clazz.getClassName());
        classMap.put(CLASS_PATH, clazz.getClassPath());
        classMap.put(CLASS_ID, clazz.getClassId());
        classMap.put(CLASS_AUTHOR, clazz.getClassAuthor().toString());
        JELogger.debug("[class=" + clazz.getClassName() + "]" + JEMessages.ADDING_CLASS_TO_RUNNER_FROM_BUILDER,
                LogCategory.DESIGN_MODE, null, LogSubModule.CLASS, clazz.getClassName());
        JEResponse jeRunnerResp;
        if (reloadClassDefinition) {
            try {
                jeRunnerResp = JERunnerAPIHandler.updateClass(classMap);
            } catch (JERunnerErrorException e) {
                throw new AddClassException(CLASS_LOAD_IN_RUNNER_FAILED);
            }

        } else {
            try {
                jeRunnerResp = JERunnerAPIHandler.addClass(classMap);
            } catch (JERunnerErrorException e) {
                throw new AddClassException(CLASS_LOAD_IN_RUNNER_FAILED);
            }
        }
        if (jeRunnerResp.getCode() != ResponseCodes.CODE_OK) {
            throw new AddClassException(JEMessages.CLASS_LOAD_FAILED);
        }
        loadedClasses.put(clazz.getClassId(), clazz);

    }

    public void loadAllClasses() {
        List<JEClass> classes = classRepository.findAll();
        JELogger.debug(JEMessages.LOADING_ALL_CLASSES_FROM_DB,
                LogCategory.DESIGN_MODE, null,
                LogSubModule.CLASS, null);
        JEClass jeClass = getNewJEProcedureClass();
        try {

            loadProcedures(jeClass);
            ClassDefinition c = getClassModel(jeClass);
            addClass(c, true, true);

        } catch (Exception e) {
            JELogger.error(JEMessages.FAILED_TO_LOAD_CLASS + " " + jeClass.getClassId(), LogCategory.DESIGN_MODE,
                    null, LogSubModule.CLASS, null);
        }
        for (JEClass clazz : classes) {
            try {
                if (clazz.getWorkspaceId() != null) {
                    addClass(clazz.getWorkspaceId(), clazz.getClassId(), true);
                } else {
                    addClassToJeRunner(clazz, true);
                }
                // loadedClasses.put(clazz.getClassId(), clazz);

            } catch (Exception e) {
                JELogger.error(JEMessages.FAILED_TO_LOAD_CLASS + " " + clazz.getClassName(), LogCategory.DESIGN_MODE,
                        null, LogSubModule.CLASS, null);
            }
        }

    }

    private void loadProcedures(JEClass c) {
        List<JEMethod> methods = methodRepository.findAll();
        HashMap<String, JEMethod> methodHashMap = new HashMap<>();
        for (JEMethod m : methods) {
            if (m.isCompiled()) {
                methodHashMap.put(m.getJobEngineElementID(), m);
            }
        }
        c.setMethods(methodHashMap);

    }

    public void loadAllClassesToBuilder() {
        List<JEClass> classes = classRepository.findAll();
        JELogger.debug(JEMessages.LOADING_ALL_CLASSES_FROM_DB,
                LogCategory.DESIGN_MODE, null,
                LogSubModule.CLASS, null);
        for (JEClass clazz : classes) {
            try {
                if (clazz.getWorkspaceId() != null) {
                    addClass(clazz.getWorkspaceId(), clazz.getClassId(), false);
                } else {
                    addClassToJeRunner(clazz, true);
                }

            } catch (Exception e) {
                JELogger.error(JEMessages.FAILED_TO_LOAD_CLASS + " " + clazz.getClassName(), LogCategory.DESIGN_MODE,
                        null, LogSubModule.CLASS, null);
            }
        }

    }

    public Map<String, JEClass> getLoadedClasses() {
        return loadedClasses;
    }

    public void setLoadedClasses(Map<String, JEClass> loadedClasses) {
        this.loadedClasses = loadedClasses;
    }

    /*
     * Get a script task class
     */
    public ClassDefinition getScriptTaskClassModel(String script) {
        MethodModel m = new MethodModel();
        m.setMethodName(EXECUTE_SCRIPT);
        m.setCode(script);
        return getTempClassFromMethod(m);
    }

    /*
     * Get a temporary class for compilation purposes
     */
    public ClassDefinition getTempClassFromMethod(MethodModel methodModel) {
        ClassDefinition c = new ClassDefinition();
        c.setClass(true);
        if (StringUtilities.isEmpty(methodModel.getId())) {
            methodModel.setId(StringUtilities.generateUUID());
        }
        if (StringUtilities.isEmpty(methodModel.getMethodName())) {
            methodModel.setMethodName(StringUtilities.generateRandomAlphabeticString(4));
        }
        if (StringUtilities.isEmpty(methodModel.getMethodScope())) {
            methodModel.setMethodScope(WorkflowConstants.STATIC);
        }
        if (StringUtilities.isEmpty(methodModel.getMethodVisibility())) {
            methodModel.setMethodVisibility(WorkflowConstants.PUBLIC);
        }
        if (StringUtilities.isEmpty(methodModel.getReturnType())) {
            methodModel.setReturnType(WorkflowConstants.VOID);
        }
        c.setClassId(methodModel.getId());
        c.setName(methodModel.getMethodName());
        c.setClassVisibility(WorkflowConstants.PUBLIC);
        List<MethodModel> methodModels = new ArrayList<>();
        methodModels.add(methodModel);
        c.setMethods(methodModels);
        c.setClassAuthor(ClassAuthor.SCRIPT);
        return c;
    }

    /*
     * Return method by name from database
     */
    public MethodModel getMethodModelByName(String name) throws MethodException {
        // HashMap<String, JEMethod> methods =
        // loadedClasses.get(WorkflowConstants.JEPROCEDURES).getMethods();
        JEMethod method = methodRepository.findByJobEngineElementName(name);
        if (method == null) {
            method = methodRepository.findById(name).isPresent() ? methodRepository.findById(name).get() : null;
        }
        if (method != null) {
            return ClassManager.getMethodModel(method);
        } 
            throw new MethodException(JEMessages.METHOD_MISSING);

    }

    /*
     * Return JEMethod object from MethodModel
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
        method.setScope(WorkflowConstants.STATIC);
        if (!m.getInputs().isEmpty()) {
            for (FieldModel f : m.getInputs()) {
                method.getInputs().add(getFieldFromModel(f));
            }
        }
        method.setImports(m.getImports());
        return method;
    }

    /*
     * Compile code before injecting it to the JVM
     */
    public void compileCode(MethodModel m) throws ClassLoadException, AddClassException {
        // create a temp class
        if (StringUtilities.isEmpty(m.getCode())) {
            throw new ClassLoadException(EMPTY_CODE);
        }
        ClassDefinition tempClass = getTempClassFromMethod(m);
        tempClass.setImports(m.getImports());
        // compile without saving or sending to the runner
        addClass(tempClass, false, true);
    }

    /*
     * try to compile code before saving
     */
    private boolean tryCompileMethod(MethodModel m) {
        boolean compiled = true;
        try {
            compileCode(m);
        } catch (Exception e) {
            compiled = false;
        }
        return compiled;
    }

    /*
     * Create new procedure
     */
    public void addProcedure(MethodModel m) throws ClassLoadException, AddClassException, MethodException {

        if (m.getCode().isEmpty())
            throw new MethodException(PROCEDURE_SHOULD_CONTAIN_CODE);

        JEMethod method = methodRepository.findByJobEngineElementName(m.getMethodName());
        if (method != null) {
            boolean sameInputs = true;
            if (m.getInputs() != null) {
                if (method.getInputs().size() != m.getInputs().size()) {
                    sameInputs = false;
                } else {
                    for (int i = 0; i < method.getInputs().size(); i++) {
                        if (!method.getInputs().get(i).equals(m.getInputs().get(i))) {
                            sameInputs = false;
                        }
                    }

                }
            }
            if (sameInputs) {
                throw new MethodException(JEMessages.METHOD_EXISTS);
            }
        }
        boolean compiled = tryCompileMethod(m);
        // TODO cleanup classes
        method = getMethodFromModel(m);
        method.setCompiled(compiled);
        if (compiled) {
            JEClass clazz = classRepository.findById(WorkflowConstants.JEPROCEDURES).get();
            if (clazz == null) {
                clazz = getNewJEProcedureClass();
            }
            clazz.getMethods().put(m.getId(), method);
            ClassDefinition c = getClassModel(clazz);
            c.setImports(m.getImports());
            addClass(c, true, true);
            
            classRepository.save(clazz);
        }
        methodRepository.save(method);
        /*
         * } catch (ClassLoadException | AddClassException e) {
         * e.printStackTrace();
         * }
         */
    }

    /*
     * Create new SIOTHProcedure class
     */
    private JEClass getNewJEProcedureClass() {
        JEClass c = new JEClass(null, WorkflowConstants.JEPROCEDURES,
                WorkflowConstants.JEPROCEDURES,
                ConfigurationConstants.BUILDER_CLASS_LOAD_PATH, ClassType.CLASS);
        c.setClassAuthor(ClassAuthor.PROCEDURE);
        return c;
    }

    /*
     * Return JEField object from FieldModel
     */
    public static JEField getFieldFromModel(FieldModel f) {
        JEField field = new JEField();
        field.setComment("");
        field.setVisibility(f.getFieldVisibility());
        field.setType(f.getType());
        field.setName(f.getName());
        return field;
    }

    /*
     * Add new jar library to projects
     */
    public void addJarToProject(LibModel libModel) throws LibraryException {
        JELib jeLib = projectService.addFile(libModel);
        if(jeLib != null) {
            jeLib.setFileType(FileType.JAR);
            libraryRepository.save(jeLib);
        }
    }

    /*
     * return all methods
     */
    public List<MethodModel> getAllMethods() {
        List<JEMethod> methods = methodRepository.findAll();
        List<MethodModel> response = new ArrayList<>();
        if (!methods.isEmpty()) {
            for (JEMethod m : methods) {
                response.add(ClassManager.getMethodModel(m));
            }
        }
        return response;
    }

    /*
     * return all libraries
     */
    public List<LibModel> getAllLibs() {
        List<JELib> libraries = libraryRepository.findAll();
        List<LibModel> models = new ArrayList<>();
        for (JELib lib : libraries) {
            if(lib.getFileType().equals(FileType.JAR))
                models.add(getLibModel(lib));
        }
        return models;
    }

    /*
     * return library by id
     */
    public LibModel getLibraryById(String id) {
        Optional<JELib> lib = libraryRepository.findById(id);
        return lib.map(ClassManager::getLibModel).orElse(null);
    }

    /*
     * Remove library
     */
    public void removeLibrary(String id) throws LibraryException {
        // delete library from db
        Optional<JELib> lib = libraryRepository.findById(id);
        if (lib.isPresent()) {
            try {
                FileUtilities.deleteFileFromPath(lib.get().getFilePath());
            } catch (Exception e) {
                JELogger.error(JEMessages.FAILED_TO_DELETE_FILES + ":"+e.getMessage(), LogCategory.DESIGN_MODE, "", LogSubModule.CLASS, id);
            }

            libraryRepository.deleteById(id);
        } else {
            JELogger.debug(JEMessages.ERROR_REMOVING_LIBRARY, LogCategory.DESIGN_MODE, "", LogSubModule.CLASS, id);
            throw new LibraryException(JEMessages.ERROR_REMOVING_LIBRARY);
        }
    }

    /*
     * Remove procedure from SIOTHProcedures
     */
    public void removeProcedure(String name) throws MethodException {
        try {
            // delete method from DB
            JEMethod method = methodRepository.findByJobEngineElementName(name);
            if (method == null) {
            	Optional<JEMethod> m = methodRepository.findById(name); 
               if(!m.isPresent())
               {
            	   JELogger.error(JEMessages.ERROR_REMOVING_LIBRARY + ": Not Found.\n " ,
                           LogCategory.DESIGN_MODE, "", LogSubModule.CLASS, name);
            	   return;
               }
            	   
               method=m.get();
            }
                methodRepository.delete(method);
            
            // updated existent SIOTHProcedures
                JEClass clazz = classRepository.findById(WorkflowConstants.JEPROCEDURES).get();
                clazz.getMethods().remove(method.getJobEngineElementID());
                ClassDefinition c = getClassModel(clazz);
                addClass(c, true, true);
                classRepository.save(clazz);


        } catch (Exception e) {
            JELogger.error(JEMessages.ERROR_REMOVING_LIBRARY + "\n" + Arrays.toString(e.getStackTrace()),
                    LogCategory.DESIGN_MODE, "", LogSubModule.CLASS, name);
            throw new MethodException(JEMessages.ERROR_REMOVING_METHOD);
        }
    }

    /*
     * Update SIOTH procedures
     */
    public void updateProcedure(MethodModel m) throws MethodException, AddClassException, ClassLoadException {

        if (m.getCode().isEmpty())
            throw new MethodException(PROCEDURE_SHOULD_CONTAIN_CODE);

        Optional<JEMethod> methodOptional = methodRepository.findById(m.getId());
        if (methodOptional.isEmpty()) {
            throw new MethodException(JEMessages.METHOD_MISSING);
        }

        boolean compiled = tryCompileMethod(m);
        JEMethod method = getMethodFromModel(m);
        method.setCompiled(compiled);
        JEClass clazz = classRepository.findById(WorkflowConstants.JEPROCEDURES).get();
        if (clazz == null) {
            clazz = getNewJEProcedureClass();
        }
        if (!compiled) {
            clazz.getMethods().remove(m.getId());
        } else {
            clazz.getMethods().put(m.getId(), method);
        }

        // try {
        ClassDefinition c = getClassModel(clazz);
        c.setImports(m.getImports());
        // load new SIOTHProcedures in runner and in Db
        addClass(c, true, true);
        // save updated method in db
        methodRepository.save(method);
        classRepository.save(clazz);
    }

    /*
     * Remove a class from the job engine database
     */
    public void removeClass(String className) {
        classRepository.deleteByClassName(className);
    }

    public void cleanUpHouse() {
        try {
            classRepository.deleteAll();
            methodRepository.deleteAll();
            libraryRepository.deleteAll();
            FileUtilities.deleteDirectory(ConfigurationConstants.JAVA_GENERATION_PATH);
            FileUtilities.deleteDirectory(ConfigurationConstants.EXTERNAL_LIB_PATH);

        }
        catch (Exception e) {
            e.printStackTrace();}
    }

    /*
     * public void updateClass(ClassDefinition classDefinition, boolean
     * sendToRunner)
     * throws AddClassException, JERunnerErrorException, InterruptedException,
     * ExecutionException,
     * DataDefinitionUnreachableException, ClassLoadException, IOException {
     * 
     * if (classRepository.findById(classDefinition.getIdClass()).isPresent()) {
     * List<JEClass> builtClasses = ClassManager.buildClass(classDefinition);
     * for (JEClass _class : builtClasses) {
     * if (sendToRunner) {
     * addClassToJeRunner(_class);
     * }
     * loadedClasses.put(_class.getClassId(), _class);
     * }
     * 
     * }
     * 
     * }
     */

}
