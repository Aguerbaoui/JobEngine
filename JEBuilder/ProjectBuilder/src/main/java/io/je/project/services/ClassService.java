package io.je.project.services;

import io.je.classbuilder.builder.ClassManager;
import io.je.classbuilder.entity.JEClass;
import io.je.classbuilder.models.ClassDefinition;
import io.je.project.listener.ClassUpdateListener;
import io.je.project.repository.ClassRepository;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEResponse;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.AddClassException;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.DataDefinitionUnreachableException;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static io.je.utilities.constants.JEMessages.CLASS_LOAD_IN_RUNNER_FAILED;

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

    Map<String, JEClass> loadedClasses = new HashMap<String, JEClass>();


    /*****************************************************  Class Listener  ***********************************************************************/


    /*
     * Init a thread that listens to the DataModelRestApi for class definition updates
     */
    public void initClassUpdateListener() {
        // TODO make runnable static
        ClassUpdateListener runnable = new ClassUpdateListener("tcp://" + SIOTHConfigUtility.getSiothConfig().getMachineCredentials().getIpAddress(),
                SIOTHConfigUtility.getSiothConfig().getDataModelPORTS().getDmRestAPI_ConfigurationPubAddress(), "ModelTopic");
        runnable.setListening(true);
        Thread listener = new Thread(runnable);
        listener.start();

    }


    /*****************************************************  CRUD  ***********************************************************************/


    /*
     * Add Class from Class definition
     */
    public List<JEClass> addClass(ClassDefinition classDefinition, boolean sendToRunner, boolean reloadClassDefinition)
            throws AddClassException, DataDefinitionUnreachableException, ClassLoadException, IOException,
            JERunnerErrorException, InterruptedException, ExecutionException {
        List<JEClass> builtClasses = ClassManager.buildClass(classDefinition);
        for (JEClass _class : builtClasses) {
            if (sendToRunner) {
                addClassToJeRunner(_class, reloadClassDefinition);
            }
            classRepository.save(_class);
            loadedClasses.put(_class.getClassId(), _class);
        }
        return builtClasses;

    }


    public void addClass(String workspaceId, String classId, boolean sendToRunner)
            throws DataDefinitionUnreachableException, ClassLoadException, IOException, AddClassException,
            JERunnerErrorException, InterruptedException, ExecutionException {
        ClassDefinition classDefinition = ClassManager.loadClassDefinition(workspaceId, classId);


        if (!loadedClasses.containsKey(classId) && classDefinition != null) {
            classDefinition.setWorkspaceId(workspaceId);
            addClass(classDefinition, sendToRunner, false);
            JELogger.info("Class " + classDefinition.getName() + " loaded successfully.", null, null, null, classDefinition.getIdClass());
        }


    }


    /*
     * Add Class from Class definition
     */
    public List<JEClass> updateClass(ClassDefinition classDefinition, boolean sendToRunner)
            throws AddClassException, DataDefinitionUnreachableException, ClassLoadException, IOException,
            JERunnerErrorException, InterruptedException, ExecutionException {
        List<JEClass> builtClasses = ClassManager.buildClass(classDefinition);
        for (JEClass _class : builtClasses) {
            if (sendToRunner) {
                addClassToJeRunner(_class, false);
            }
            classRepository.save(_class);
            loadedClasses.put(_class.getClassId(), _class);
        }
        return builtClasses;

    }

    public void sendClassesToJeRunner(Collection<JEClass> collection)
            throws InterruptedException, JERunnerErrorException, ExecutionException, AddClassException {
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
    }


    /*
     * send class to je runner to be loaded there
     *
     */
    public void addClassToJeRunner(JEClass clazz, boolean reloadClassDefinition)
            throws AddClassException, JERunnerErrorException, InterruptedException, ExecutionException {
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

    }


    public void loadAllClasses() {
        List<JEClass> classes = classRepository.findAll();
        JELogger.debug(JEMessages.LOADING_ALL_CLASSES_FROM_DB,
                LogCategory.DESIGN_MODE, null,
                LogSubModule.CLASS, null);
        for (JEClass clazz : classes) {
            try {
                if (clazz.getWorkspaceId() != null) {
                    addClass(clazz.getWorkspaceId(), clazz.getClassId(), true);
                } else {
                    addClassToJeRunner(clazz, true);
                }

            } catch (Exception e) {
                JELogger.error(JEMessages.FAILED_TO_LOAD_CLASS + " " + clazz.getClassName(), LogCategory.DESIGN_MODE,
                        null, LogSubModule.CLASS, null);
            }
        }

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
