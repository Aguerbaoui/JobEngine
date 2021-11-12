package io.je.project.services;

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
import io.je.utilities.beans.*;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.*;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.LibModel;
import io.siothconfig.SIOTHConfigUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import utils.files.FileUtilities;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.string.StringUtilities;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static io.je.classbuilder.builder.ClassManager.getClassModel;
import static io.je.classbuilder.builder.ClassManager.getLibModel;
import static io.je.utilities.constants.JEMessages.CLASS_LOAD_IN_RUNNER_FAILED;
import static io.je.utilities.constants.JEMessages.PROCEDURE_SHOULD_CONTAIN_CODE;
import static io.je.utilities.constants.WorkflowConstants.EXECUTE_SCRIPT;

/*
 * Service class to handle classes
 */
@Service
public class ClassService {

    public static final String CLASS_NAME = "className";
    public static final String CLASS_PATH = "classPath";
    public static final String CLASS_ID = "classId";


    @Autowired
    ClassRepository classRepository;

    @Autowired
    LibraryRepository libraryRepository;

    @Autowired
    MethodRepository methodRepository;

    Map<String, JEClass> loadedClasses = new HashMap<String, JEClass>();

    @Autowired
    private HttpServletRequest request;

    /*****************************************************  Class Listener  ***********************************************************************/


    /*
     * Init a thread that listens to the DataModelRestApi for class definition updates
     */
    public void initClassUpdateListener() {
        // TODO make runnable static
        ClassUpdateListener runnable = new ClassUpdateListener("tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(),
                SIOTHConfigUtility.getSiothConfig().getDataModelPORTS().getDmRestAPI_ConfigurationPubAddress(), "ModelTopic");
        runnable.setListening(true);
        Thread listener = new Thread(runnable);
        listener.start();

    }


    /*****************************************************  CRUD  ***********************************************************************/


    /*
     * Add Class from Class definition
     */
    public void addClass(ClassDefinition classDefinition, boolean sendToRunner, boolean reloadClassDefinition)
            throws AddClassException, ClassLoadException {
        if(reloadClassDefinition) {
            JEClassLoader.overrideInstance();
        }
        List<JEClass> builtClasses = ClassManager.buildClass(classDefinition);
        for (JEClass _class : builtClasses) {
            if (sendToRunner) {
                addClassToJeRunner(_class, reloadClassDefinition);
                classRepository.save(_class);
            }

        }

    }


    /*
    * Add class to runner from datamodel
    * */
    public void addClass(String workspaceId, String classId, boolean sendToRunner)
            throws ClassLoadException, AddClassException {
        ClassDefinition classDefinition = ClassManager.loadClassDefinition(workspaceId, classId);


        if (!loadedClasses.containsKey(classId) && classDefinition != null) {
            classDefinition.setWorkspaceId(workspaceId);
            addClass(classDefinition, sendToRunner, false);
            JELogger.info("Class " + classDefinition.getName() + " loaded successfully.", null, null, null, classDefinition.getName());
        }


    }


    /*
     * Add Class from Class definition
     */
   /* public List<JEClass> updateClass(ClassDefinition classDefinition, boolean sendToRunner)
            throws AddClassException, ClassLoadException {
        List<JEClass> builtClasses = ClassManager.buildClass(classDefinition);
        for (JEClass _class : builtClasses) {
            if (sendToRunner) {
                addClassToJeRunner(_class, false);
            }
            classRepository.save(_class);
            loadedClasses.put(_class.getClassId(), _class);
        }
        return builtClasses;

    }*/
/*
    public void sendClassesToJeRunner(Collection<JEClass> collection)
            throws AddClassException {
        ArrayList<HashMap> classesList = new ArrayList<>();
        JEResponse jeRunnerResp;
        for (JEClass clazz : collection) {
            HashMap<String, String> classMap = new HashMap<>();
            classMap.put(CLASS_NAME, clazz.getClassName());
            classMap.put(CLASS_PATH, clazz.getClassPath());
            classMap.put(CLASS_ID, clazz.getClassId());
            classesList.add(classMap);
        }
        JELogger.debug(JEMessages.ADDING_CLASSES_TO_RUNNER_FROM_BUILDER, LogCategory.DESIGN_MODE, null, LogSubModule.CLASS, null);

        try {
            jeRunnerResp = JERunnerAPIHandler.addClasses(classesList);
        } catch (JERunnerErrorException e) {
            throw new AddClassException(CLASS_LOAD_IN_RUNNER_FAILED);
        }
        if (jeRunnerResp.getCode() != ResponseCodes.CODE_OK) {
            throw new AddClassException(JEMessages.CLASS_LOAD_FAILED);
        }
    }*/


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
        JELogger.debug(JEMessages.ADDING_CLASSES_TO_RUNNER_FROM_BUILDER, LogCategory.DESIGN_MODE, null, LogSubModule.CLASS, clazz.getClassName());
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
                //loadedClasses.put(clazz.getClassId(), clazz);

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
            methodHashMap.put(m.getJobEngineElementID(), m);
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
     * */
    public ClassDefinition getScriptTaskClassModel(String script) {
        MethodModel m = new MethodModel();
        //m.setId(name);
        m.setMethodName(EXECUTE_SCRIPT);
        m.setCode(script);
        return getTempClassFromMethod(m);
    }

    /*
    * Get a temporary class for compilation purposes
    * */
    public ClassDefinition getTempClassFromMethod(MethodModel methodModel) {
        ClassDefinition c = new ClassDefinition();
        c.setClass(true);
        if(methodModel.getId() == null) {
            methodModel.setId(StringUtilities.generateUUID());
        }
        if(methodModel.getMethodName() == null) {
            methodModel.setMethodName(StringUtilities.generateRandomAlphabeticString(4));
        }
        if(methodModel.getMethodScope() == null) {
            methodModel.setMethodScope(WorkflowConstants.STATIC);
        }
        if(methodModel.getMethodVisibility() == null) {
            methodModel.setMethodVisibility(WorkflowConstants.PUBLIC);
        }
        if(methodModel.getReturnType() == null) {
            methodModel.setReturnType(WorkflowConstants.VOID);
        }
        c.setClassId(methodModel.getId());
        c.setName(methodModel.getMethodName());
        c.setClassVisibility(WorkflowConstants.PUBLIC);
        List<MethodModel> methodModels = new ArrayList<>();
        methodModels.add(methodModel);
        c.setMethods(methodModels);
        return c;
    }

    /*
     * Return method by name from database
     * */
    public MethodModel getMethodModelByName(String name) throws MethodException {
        //HashMap<String, JEMethod> methods = loadedClasses.get(WorkflowConstants.JEPROCEDURES).getMethods();
        JEMethod method = methodRepository.findByJobEngineElementName(name);
        if (method == null) {
            method = methodRepository.findById(name).isPresent() ? methodRepository.findById(name).get() : null;
        }
        if (method != null) {
            return ClassManager.getMethodModel(method);
        } else throw new MethodException(JEMessages.METHOD_MISSING);

    }

    /*
     * Return JEMethod object from MethodModel
     * */
    public JEMethod getMethodFromModel(MethodModel m) {
        JEMethod method = new JEMethod();
        method.setCode(m.getCode());
        method.setReturnType(m.getReturnType());
        method.setJobEngineElementID(m.getId());
        method.setJobEngineElementName(m.getMethodName());
        method.setJeObjectCreatedBy(m.getCreatedBy());
        method.setJeObjectModifiedBy(m.getModifiedBy());
        method.setJeObjectLastUpdate(LocalDateTime.now());
        method.setJeObjectCreationDate(LocalDateTime.now());
        method.setInputs(new ArrayList<>());
        if (!m.getInputs().isEmpty()) {
            for (FieldModel f : m.getInputs()) {
                method.getInputs().add(getFieldFromModel(f));
            }
        }
        method.setImports(m.getImports());
        return method;
    }

    /*
     * Create new procedure
     * */
    public void addProcedure(MethodModel m) throws ClassLoadException, AddClassException, MethodException {

        if(m.getCode().isEmpty()) throw new MethodException(PROCEDURE_SHOULD_CONTAIN_CODE);
        ClassDefinition tempClass = getTempClassFromMethod(m);
        tempClass.setImports(m.getImports());
        addClass(tempClass, false, true);

        //TODO cleanup classes

        JEClass clazz = loadedClasses.get(WorkflowConstants.JEPROCEDURES);
        if (clazz == null) {
            clazz = getNewJEProcedureClass();
        }
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


        method = getMethodFromModel(m);
        method.setScope(WorkflowConstants.STATIC);
        clazz.getMethods().put(m.getId(), method);
        //try {
        ClassDefinition c = getClassModel(clazz);
        c.setImports(m.getImports());
        addClass(c, true, true);
        classRepository.save(clazz);
        methodRepository.save(method);
  /*      } catch (ClassLoadException | AddClassException e) {
            e.printStackTrace();
        }*/
    }

    /*
     * Create new SIOTHProcedure class
     * */
    private JEClass getNewJEProcedureClass() {
        return new JEClass(null, WorkflowConstants.JEPROCEDURES,
                WorkflowConstants.JEPROCEDURES,
                ConfigurationConstants.BUILDER_CLASS_LOAD_PATH
                , ClassType.CLASS);
    }

    /*
    * Return JEField object from FieldModel
    * */
    public JEField getFieldFromModel(FieldModel f) {
        JEField field = new JEField();
        field.setComment("");
        field.setVisibility(f.getFieldVisibility());
        field.setType(f.getType());
        field.setName(f.getName());
        return field;
    }

    /*
    * Add new jar library to projects
    * */
    public void addJarToProject(LibModel libModel) throws LibraryException {
        JELogger.info(JEMessages.ADDING_JAR_TO_PROJECT,
                LogCategory.DESIGN_MODE, null, LogSubModule.JEBUILDER, libModel.getFileName());
        try {
            MultipartFile file = libModel.getFile();
            String orgName = file.getOriginalFilename();
            if(file.getSize() > SIOTHConfigUtility.getSiothConfig().getJobEngine().getLibraryMaxFileSize()) {
                JELogger.debug("File size = " + file.getSize());
                throw new LibraryException(JEMessages.FILE_TOO_LARGE);
            }
            if (!file.isEmpty()) {
                if(!FileUtilities.fileIsJar(orgName)) {
                    throw new LibraryException(JEMessages.JOB_ENGINE_ACCEPTS_JAR_FILES_ONLY);
                }
                String uploadsDir = ConfigurationConstants.EXTERNAL_LIB_PATH;
                //TODO change to the path set by the user for classes in sioth
                String realPathtoUploads = request.getServletContext().getRealPath(uploadsDir);
                if (!new File(uploadsDir).exists()) {
                    new File(realPathtoUploads).mkdir();
                }


                String filePath = uploadsDir + orgName;
                File dest = new File(filePath);
                if (dest.exists()) {
                    throw new LibraryException(JEMessages.LIBRARY_EXISTS);
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
                jeLib.setJeObjectCreationDate(LocalDateTime.now());
                jeLib.setJobEngineElementID(libModel.getId());
                HashMap<String, String> payload = new HashMap<>();
                payload.put("name", file.getOriginalFilename());
                payload.put("path", dest.getAbsolutePath());
                JERunnerAPIHandler.addJarToRunner(payload);
                libraryRepository.save(jeLib);

            }
        } catch (JERunnerErrorException | IOException e) {
            throw new LibraryException(JEMessages.ERROR_IMPORTING_FILE);
        }
    }

    /*
     * return all methods
     * */
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
    * */
    public List<LibModel> getAllLibs() {
        List<JELib> libraries = libraryRepository.findAll();
        List<LibModel> models = new ArrayList<>();
        for (JELib lib : libraries) {
            models.add(getLibModel(lib));
        }
        return models;
    }

    /*
    * return library by id
    * */
    public LibModel getLibraryById(String id) {
        Optional<JELib> lib = libraryRepository.findById(id);
        return lib.map(ClassManager::getLibModel).orElse(null);
    }

    /*
    * Remove library
    * */
    public void removeLibrary(String id) throws LibraryException {
        //delete library from db
        Optional<JELib> lib = libraryRepository.findById(id);
        if (lib.isPresent()) {
            libraryRepository.deleteById(id);
        } else {
            JELogger.debug(JEMessages.ERROR_REMOVING_LIBRARY, LogCategory.DESIGN_MODE, "", LogSubModule.CLASS, id);
            throw new LibraryException(JEMessages.ERROR_REMOVING_LIBRARY);
        }
    }

    /*
    * Remove procedure from SIOTHProcedures
    * */
    public void removeProcedure(String name) throws MethodException {
        try {
            //delete method from DB
            JEMethod method = methodRepository.findByJobEngineElementName(name);
            if (method == null) {
                method = methodRepository.findById(name).isPresent() ? methodRepository.findById(name).get() : null;
            }
            if (method != null) {
                methodRepository.delete(method);
            }
            //updated existent SIOTHProcedures
            loadedClasses.get(WorkflowConstants.JEPROCEDURES).getMethods().remove(method.getJobEngineElementID());
            ClassDefinition c = getClassModel(loadedClasses.get(WorkflowConstants.JEPROCEDURES));
            addClass(c, true, true);

        } catch (Exception e) {
            JELogger.debug(JEMessages.ERROR_REMOVING_LIBRARY, LogCategory.DESIGN_MODE, "", LogSubModule.CLASS, name);
            throw new MethodException(JEMessages.ERROR_REMOVING_METHOD);
        }
    }

    /*
    * Update SIOTH procedures
    * */
    public void updateProcedure(MethodModel m) throws MethodException, AddClassException, ClassLoadException {
        JEClass clazz = loadedClasses.get(WorkflowConstants.JEPROCEDURES);

        if (clazz == null) {
            clazz = getNewJEProcedureClass();
        }
        //check if method exists in DB
        Optional<JEMethod> methodOptional = methodRepository.findById(m.getId());
        if (methodOptional.isEmpty()) {
            throw new MethodException(JEMessages.METHOD_MISSING);
        }

    //   update method in SIOTHProcedures class

        JEMethod method = getMethodFromModel(m);
        method.setScope(WorkflowConstants.STATIC);
        clazz.getMethods().put(m.getId(), method);
        //try {
        ClassDefinition c = getClassModel(clazz);
        c.setImports(m.getImports());
        //load new SIOTHProcedures in runner and in Db
        addClass(c, true, true);
        //save updated method in db
        methodRepository.save(method);
    }

    /*
    * Compile code before injecting it to the JVM
    * */
    public void compileCode(MethodModel m) throws ClassLoadException, AddClassException {
        //create a temp class
        ClassDefinition tempClass = getTempClassFromMethod(m);
        tempClass.setImports(m.getImports());
        //compile without saving or sending to the runner
        addClass(tempClass, false, true);
    }

	/*public void updateClass(ClassDefinition classDefinition, boolean sendToRunner)
			throws AddClassException, JERunnerErrorException, InterruptedException, ExecutionException,
			DataDefinitionUnreachableException, ClassLoadException, IOException {

		if (classRepository.findById(classDefinition.getIdClass()).isPresent()) {
			List<JEClass> builtClasses = ClassManager.buildClass(classDefinition);
			for (JEClass _class : builtClasses) {
				if (sendToRunner) {
					addClassToJeRunner(_class);
				}
				loadedClasses.put(_class.getClassId(), _class);
			}

		}

	} */


}
