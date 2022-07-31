package io.je.project.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.classbuilder.builder.ClassBuilder;
import io.je.classbuilder.builder.ClassManager;
import io.je.classbuilder.models.*;
import io.je.project.repository.ClassRepository;
import io.je.project.repository.LibraryRepository;
import io.je.project.repository.MethodRepository;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.*;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.*;
import io.je.utilities.execution.CommandExecutioner;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.LibModel;
import io.siothconfig.SIOTHConfigUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import utils.files.FileUtilities;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;
import utils.string.StringUtilities;
import utils.zmq.ZMQBind;
import utils.zmq.ZMQSubscriber;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static io.je.classbuilder.builder.ClassManager.getClassModel;
import static io.je.classbuilder.builder.ClassManager.getLibModel;
import static io.je.utilities.beans.ClassAuthor.DATA_MODEL;
import static io.je.utilities.constants.ClassBuilderConfig.CLASS_PACKAGE;
import static io.je.utilities.constants.JEMessages.*;

/**
 * Service class to handle classes from datamodel
 */
@Service
public class ClassService {

    public static final String CLASS_NAME = "className";
    public static final String CLASS_PATH = "classPath";
    public static final String CLASS_ID = "classId";
    public static final String CLASS_AUTHOR = "classAuthor";
    public static final String MAIN = "main";
    public static final String MODEL_TOPIC = "ModelTopic";

    @Autowired
    ClassRepository classRepository;

    @Autowired
    LibraryRepository libraryRepository;

    @Autowired
    MethodRepository methodRepository;

    @Autowired
    @Lazy
    ProjectService projectService;

    @Autowired
    ConfigurationService configurationService;

    Map<String, JEClass> loadedClasses = new HashMap<String, JEClass>();

    @Autowired
    private HttpServletRequest request;

    /*****************************************************
     * Class Listener
     ***********************************************************************/

    /**
     * Init a thread that listens to the DataModelRestApi for class definition updates
     */
    public void initClassZMQSubscriber() {
        // TODO make runnable static
        ClassZMQSubscriber runnable = new ClassZMQSubscriber(
                "tcp://" + SIOTHConfigUtility.getSiothConfig()
                        .getNodes()
                        .getSiothMasterNode(),
                    SIOTHConfigUtility.getSiothConfig()
                        .getDataModelPORTS()
                        .getDmRestAPI_ConfigurationPubAddress());

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
        try {
            List<JEClass> builtClasses = ClassManager.buildClass(classDefinition, CLASS_PACKAGE);
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
        } catch (IOException | InterruptedException e) {
            LoggerUtils.logException(e);
            JELogger.error(CLASS_LOAD_DENIED_ACCESS, LogCategory.DESIGN_MODE, "", LogSubModule.CLASS, "");
        }
    }

    /*
     * Add class to runner from datamodel
     */
    public void loadClassFromDataModel(String workspaceId, String classId, boolean sendToRunner)
            throws ClassLoadException, AddClassException {

        ClassDefinition classDefinition = ClassManager.loadClassDefinition(workspaceId, classId);

        if (classDefinition != null) {
            classDefinition.setWorkspaceId(workspaceId);
            addClass(classDefinition, sendToRunner, (loadedClasses.containsKey(classId)));
            JELogger.info("Class " + classDefinition.getName() + " loaded successfully.", null, null, null,
                    classDefinition.getName());
        }

    }

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
        classMap.put(CLASS_AUTHOR, clazz.getClassAuthor()
                .toString());
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

    /*
     * Load all classes
     * */
    public void loadAllClasses() {
        JELogger.debug(JEMessages.LOADING_ALL_CLASSES_FROM_DB,
                LogCategory.DESIGN_MODE, null,
                LogSubModule.CLASS, null);

        loadSIOTHProcedures();

        loadDataModelClasses();

    }

    /*
     * Load DM classes
     * */
    private void loadDataModelClasses() {
        List<JEClass> classes = classRepository.findByClassAuthor(DATA_MODEL.toString());
        for (JEClass clazz : classes) {
            try {
                loadClassFromDataModel(clazz.getWorkspaceId(), clazz.getClassId(), true);
            } catch (Exception e) {
                JELogger.error(JEMessages.FAILED_TO_LOAD_CLASS + " " + clazz.getClassName(), LogCategory.DESIGN_MODE,
                        null, LogSubModule.CLASS, null);
            }
        }
    }

    /*
     * Load SIOTHProcedure class
     * */
    private void loadSIOTHProcedures() {
        JEClass jeClass = getNewJEProcedureClass();
        try {

            loadMethods(jeClass);
            ClassDefinition c = getClassModel(jeClass);
            String filePath = ClassBuilder.buildClass(c, ConfigurationConstants.JAVA_GENERATION_PATH, JEClassLoader.getJobEnginePackageName(CLASS_PACKAGE));
            c.setClassAuthor(ClassAuthor.PROCEDURE);
            jeClass.setClassPath(filePath);
            classRepository.save(jeClass);
            CommandExecutioner.compileCode(filePath, ConfigurationConstants.isDev());
            CommandExecutioner.buildJar();
            //addClass(c, true, true);

        } catch (Exception e) {
            JELogger.error(JEMessages.FAILED_TO_LOAD_CLASS + " " + jeClass.getClassId(), LogCategory.DESIGN_MODE,
                    null, LogSubModule.CLASS, null);
        }
    }

    /*
     * Load user defined methods
     * */
    private void loadMethods(JEClass c) {
        List<JEMethod> methods = methodRepository.findAll();
        HashMap<String, JEMethod> methodHashMap = new HashMap<>();
        for (JEMethod m : methods) {
            if (m.isCompiled()) {
                methodHashMap.put(m.getJobEngineElementID(), m);
            }
        }
        c.setMethods(methodHashMap);

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
        m.setMethodName(MAIN);
        m.setCode(script);
        FieldModel fieldModel = new FieldModel();
        fieldModel.setType("String[]");
        fieldModel.setName("args");
        List<FieldModel> list = new ArrayList<>();
        list.add(fieldModel);
        m.setInputs(list);
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
            method = methodRepository.findById(name)
                    .isPresent() ? methodRepository.findById(name)
                    .get() : null;
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
        if (!m.getInputs()
                .isEmpty()) {
            for (FieldModel f : m.getInputs()) {
                method.getInputs()
                        .add(getFieldFromModel(f));
            }
        }
        method.setImports(m.getImports());
        return method;
    }

    /*
     * Compile code before injecting it to the JVM
     */
    public void compileCode(MethodModel m, String packageName) throws ClassLoadException, AddClassException, IOException, InterruptedException {
        // create a temp class
        if (StringUtilities.isEmpty(m.getCode())) {
            throw new ClassLoadException(EMPTY_CODE);
        }
        ClassDefinition tempClass = getTempClassFromMethod(m);
        tempClass.setImports(m.getImports());
        // compile without saving or sending to the runner
        //addClass(tempClass, false, true);
        compileCode(tempClass, packageName);
    }

    public void compileCode(ClassDefinition c, String packageName) throws ClassLoadException, AddClassException, IOException, InterruptedException {
        String filePath = ClassBuilder.buildClass(c, ConfigurationConstants.JAVA_GENERATION_PATH, JEClassLoader.getJobEnginePackageName(packageName));
        CommandExecutioner.compileCode(filePath, ConfigurationConstants.isDev());
    }

    /*
     * Create new procedure
     */
    public void addProcedure(MethodModel m) throws ClassLoadException, AddClassException, MethodException, IOException, InterruptedException {

        if (m.getCode()
                .isEmpty())
            throw new MethodException(PROCEDURE_SHOULD_CONTAIN_CODE);

        JEMethod method = methodRepository.findByJobEngineElementName(m.getMethodName());
        if (method != null) {
            boolean sameInputs = true;
            if (m.getInputs() != null) {
                if (method.getInputs()
                        .size() != m.getInputs()
                        .size()) {
                    sameInputs = false;
                } else {
                    for (int i = 0; i < method.getInputs()
                            .size(); i++) {
                        if (!method.getInputs()
                                .get(i)
                                .equals(m.getInputs()
                                        .get(i))) {
                            sameInputs = false;
                        }
                    }

                }
            }
            if (sameInputs) {
                throw new MethodException(JEMessages.METHOD_EXISTS);
            }
        }
        boolean compiled = true;
        try {
            compileCode(m, CLASS_PACKAGE);
        } catch (Exception e) {
            compiled = false;
        }
        // TODO cleanup classes
        method = getMethodFromModel(m);
        method.setCompiled(compiled);
        if (compiled) {
            JEClass clazz = classRepository.findById(WorkflowConstants.JEPROCEDURES).get();
            if (clazz == null) {
                clazz = getNewJEProcedureClass();
            }
            clazz.getMethods()
                    .put(m.getId(), method);
            ClassDefinition c = getClassModel(clazz);
            c.getImports()
                    .addAll(m.getImports());

            //addClass(c, true, true);
            String filePath = ClassBuilder.buildClass(c, ConfigurationConstants.JAVA_GENERATION_PATH, JEClassLoader.getJobEnginePackageName(CLASS_PACKAGE));
            CommandExecutioner.compileCode(clazz.getClassPath(), ConfigurationConstants.isDev());
            CommandExecutioner.buildJar();
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
                ConfigurationConstants.JAVA_GENERATION_PATH, ClassType.CLASS);
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
        JELogger.control(JEMessages.ADDING_JAR_TO_PROJECT,
                LogCategory.DESIGN_MODE, null, LogSubModule.JEBUILDER, libModel.getFileName());
        try {
            MultipartFile file = libModel.getFile();
            String orgName = file.getOriginalFilename();
            if (file.getSize() > SIOTHConfigUtility.getSiothConfig()
                    .getJobEngine()
                    .getLibraryMaxFileSize()) {
                JELogger.trace("File size = " + file.getSize());
                throw new LibraryException(JEMessages.FILE_TOO_LARGE);
            }
            if (!file.isEmpty()) {
                if (!FileUtilities.fileIsJar(orgName)) {
                    throw new LibraryException(JEMessages.JOB_ENGINE_ACCEPTS_JAR_FILES_ONLY);
                }
                String uploadsDir = ConfigurationConstants.EXTERNAL_LIB_PATH;
                if (!new File(uploadsDir).exists()) {
                    new File(uploadsDir).mkdir();
                }

                String filePath = uploadsDir + orgName;
                File dest = new File(filePath);
                if (dest.exists()) {
                    FileUtilities.deleteFileFromPath(filePath);
                    //throw new LibraryException(JEMessages.LIBRARY_EXISTS);
                }
                file.transferTo(dest);
                JELogger.debug(JEMessages.UPLOADED_JAR_TO_PATH + dest,
                        LogCategory.DESIGN_MODE, null, LogSubModule.JEBUILDER, null);
                JELib jeLib = new JELib();
                jeLib.setFilePath(dest.getAbsolutePath());
                jeLib.setJobEngineElementName(orgName);
                jeLib.setScope(LibScope.JOBENGINE);
                jeLib.setJeObjectCreatedBy(libModel.getCreatedBy());
                jeLib.setJeObjectModifiedBy(libModel.getCreatedBy());
                jeLib.setJeObjectCreationDate(Instant.now());
                jeLib.setJobEngineElementID(libModel.getId());
                jeLib.setFileType(FileType.JAR);
                HashMap<String, String> payload = new HashMap<>();
                payload.put("name", file.getOriginalFilename());
                payload.put("path", dest.getAbsolutePath());
                //JERunnerAPIHandler.addJarToRunner(payload);
                libraryRepository.save(jeLib);

            }
        } catch (IOException e) {

            throw new LibraryException(JEMessages.ERROR_IMPORTING_FILE + ":" + e.getMessage());
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
            if (lib.getFileType() != null && lib.getFileType()
                    .equals(FileType.JAR))
                models.add(getLibModel(lib));
        }
        return models;
    }

    /*
     * return library by id
     */
    public LibModel getLibraryById(String id) {
        Optional<JELib> lib = libraryRepository.findById(id);
        return lib.map(ClassManager::getLibModel)
                .orElse(null);
    }

    /*
     * Remove library
     */
    public void removeLibrary(String id) throws LibraryException {
        // delete library from db
        Optional<JELib> lib = libraryRepository.findById(id);
        if (lib.isPresent()) {
            try {
                FileUtilities.deleteFileFromPath(lib.get()
                        .getFilePath());
            } catch (Exception e) {
                JELogger.error(JEMessages.FAILED_TO_DELETE_FILES + ":" + e.getMessage(), LogCategory.DESIGN_MODE, "", LogSubModule.CLASS, id);
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
                if (!m.isPresent()) {
                    JELogger.error(JEMessages.ERROR_REMOVING_LIBRARY + ": Not Found.\n ",
                            LogCategory.DESIGN_MODE, "", LogSubModule.CLASS, name);
                    return;
                }

                method = m.get();
            }
            methodRepository.delete(method);

            // updated existent SIOTHProcedures
            JEClass clazz = classRepository.findById(WorkflowConstants.JEPROCEDURES)
                    .get();
            clazz.getMethods()
                    .remove(method.getJobEngineElementID());
            ClassDefinition c = getClassModel(clazz);
            try {
                CommandExecutioner.compileCode(clazz.getClassPath(), ConfigurationConstants.isDev());
                CommandExecutioner.buildJar();
            } catch (Exception e) {
            }
            //addClass(c, true, true);
            classRepository.save(clazz);


        } catch (Exception exception) {
            JELogger.logException(exception);
            JELogger.error(JEMessages.ERROR_REMOVING_LIBRARY + " : " + exception.getMessage(),
                    LogCategory.DESIGN_MODE, "", LogSubModule.CLASS, name);
            throw new MethodException(JEMessages.ERROR_REMOVING_METHOD);
        }
    }

    /*
     * Update SIOTH procedures
     */
    public void updateProcedure(MethodModel m) throws MethodException, AddClassException, ClassLoadException, IOException, InterruptedException {

        if (m.getCode()
                .isEmpty())
            throw new MethodException(PROCEDURE_SHOULD_CONTAIN_CODE);

        Optional<JEMethod> methodOptional = methodRepository.findById(m.getId());
        if (methodOptional.isEmpty()) {
            throw new MethodException(JEMessages.METHOD_MISSING);
        }

        boolean compiled = true;
        try {
            compileCode(m, CLASS_PACKAGE);
        } catch (Exception e) {
            compiled = false;
        }

        JEMethod method = getMethodFromModel(m);
        method.setCompiled(compiled);

        JEClass clazz = classRepository.findById(WorkflowConstants.JEPROCEDURES).get();
        if (clazz == null) {
            clazz = getNewJEProcedureClass();
        }

        if (!compiled) {
            clazz.getMethods()
                    .remove(m.getId());
        } else {
            clazz.getMethods()
                    .put(m.getId(), method);
        }

        // try {
        ClassDefinition c = getClassModel(clazz);
        c.getImports()
                .addAll(m.getImports());
        // load new SIOTHProcedures in runner and in Db
        String filePath = ClassBuilder.buildClass(c, ConfigurationConstants.JAVA_GENERATION_PATH, JEClassLoader.getJobEnginePackageName(CLASS_PACKAGE));
        try {
            CommandExecutioner.compileCode(clazz.getClassPath(), ConfigurationConstants.isDev());
            CommandExecutioner.buildJar();
        } catch (Exception e) {
            LoggerUtils.logException(e);
        }
        //addClass(c, true, true);
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

    /*
     * Delete all classes
     * */
    public void cleanUpHouse() {
        try {
            classRepository.deleteAll();
            methodRepository.deleteAll();
            libraryRepository.deleteAll();
            FileUtilities.deleteDirectory(ConfigurationConstants.JAVA_GENERATION_PATH);
            //FileUtilities.deleteDirectory(ConfigurationConstants.EXTERNAL_LIB_PATH);

        } catch (Exception e) {
            LoggerUtils.logException(e);
        }
    }


    class ClassZMQSubscriber extends ZMQSubscriber {

        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        public ClassZMQSubscriber(String url, int subPort) {
            super(url, subPort);
            addTopic(MODEL_TOPIC);
        }

        @Override
        public void run() {

             synchronized (this) {

                final String ID_MSG = "ClassZMQSubscriber : ";

                JELogger.debug(ID_MSG + "topics : " + this.topics + " : " + JEMessages.DATA_LISTENTING_STARTED,
                        LogCategory.DESIGN_MODE, null, LogSubModule.CLASS, null);

                String last_topic = null;

                while (this.listening) {

                    //  JELogger.info(ClassUpdateListener.class, "--------------------------------------");

                    String data = null;
                    try {
                        data = this.getSubSocket(ZMQBind.CONNECT).recvStr();
                    } catch (Exception e) {
                        LoggerUtils.logException(e);
                        continue;
                    }

                    try {
                        if (data == null) continue;

                        // FIXME waiting to have topic in the same response message
                        if (last_topic == null) {
                            for (String topic : this.topics) {
                                // Received Data should be equal topic
                                if (data.equals(topic)) {
                                    last_topic = topic;
                                    break;
                                }
                            }
                        } else {
                            JELogger.debug(ID_MSG + JEMessages.DATA_RECEIVED + data, LogCategory.RUNTIME,
                                    null, LogSubModule.CLASS, last_topic);

                            List<ModelUpdate> updates = null;
                            try {
                                updates = Arrays.asList(objectMapper.readValue(data, ModelUpdate[].class));
                            } catch (JsonProcessingException e) {

                                LoggerUtils.logException(e);

                                throw new InstanceCreationFailedException("Failed to parse model update : " + e.getMessage());

                            }

                            for (ModelUpdate update : updates) {
                                update.getModel()
                                        .setClassAuthor(ClassAuthor.DATA_MODEL);
                                if (update.getAction() == DataModelAction.UPDATE || update.getAction() == DataModelAction.ADD) {
                                    addClass(update.getModel(), true, true);
                                }
                                if (update.getAction() == DataModelAction.DELETE) {
                                    removeClass(update.getModel()
                                            .getName());
                                }
                            }

                            last_topic = null;
                        }

                    } catch (Exception e) {
                        LoggerUtils.logException(e);
                        JELogger.error(JEMessages.ERROR_GETTING_CLASS_UPDATES, LogCategory.DESIGN_MODE, null,
                                LogSubModule.CLASS, e.getMessage());
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        JELogger.error(JEMessages.THREAD_INTERRUPTED, LogCategory.DESIGN_MODE, null,
                                LogSubModule.CLASS, null);
                    }
                }

                JELogger.debug( ID_MSG + JEMessages.CLOSING_SOCKET, LogCategory.DESIGN_MODE,
                        null, LogSubModule.CLASS, last_topic);

                this.closeSocket();

            }

        }

    }

}
