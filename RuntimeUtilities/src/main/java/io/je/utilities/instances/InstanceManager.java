package io.je.utilities.instances;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.InstanceCreationFailedException;
import io.je.utilities.log.JELogger;
import io.je.utilities.mapping.InstanceModelMapping;
import io.je.utilities.models.InstanceModel;
import io.je.utilities.runtimeobject.JEObject;
import org.json.JSONObject;
import utils.comparator.Comparator;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Class responsible for handling data model instances
 */
public class InstanceManager {

    static ObjectMapper objectMapper = new ObjectMapper();

    // instanceId/Instance
    static ConcurrentHashMap<String, JEObject> instancesLastValue = new ConcurrentHashMap<>();

    private static InstanceModel getInstanceModel(String dataReceived) {

        JSONObject instanceJson = new JSONObject(dataReceived);
        InstanceModel instanceModel = new InstanceModel();
        instanceModel.setInstanceId(instanceJson.getString(InstanceModelMapping.INSTANCEID));
        instanceModel.setModelId(instanceJson.getString(InstanceModelMapping.MODELID));
        instanceModel.setModelName(instanceJson.getString(InstanceModelMapping.MODELNAME));
        instanceModel.setInstanceName(instanceJson.getString(InstanceModelMapping.INSTANCENAME));
        instanceModel.setPayload(instanceJson.getJSONObject(InstanceModelMapping.PAYLOAD));
        return instanceModel;
    }

    /*
     * create an instance from an InstanceModel
     */
    public static JEObject createInstance(String dataReceived) throws InstanceCreationFailedException {

        InstanceModel instanceModel = getInstanceModel(dataReceived);

        // Retrieve Instance Class
        Class<?> instanceClass = ClassRepository.getClassById(instanceModel.getModelId());
        if (instanceClass == null) {
            throw new InstanceCreationFailedException(JEMessages.CLASS_NOT_LOADED + instanceModel.getInstanceId());
        }

        objectMapper.setTypeFactory(
                objectMapper.getTypeFactory()
                        .withClassLoader(JEClassLoader.getDataModelInstance()));

        // create instance
        Object instance = null;
        // addInstanceId
        JSONObject payload = instanceModel.getPayload();

        payload.put("jobEngineElementID", instanceModel.getInstanceId());
        payload.put("className", instanceClass.getName());
        payload.put("jobEngineElementName", instanceModel.getInstanceName());
        // instanceJson.put("jobEngineElementName", instanceModel.getInstanceName());

        try {
            instance = objectMapper.readValue(instanceModel.getPayload()
                    .toString(), instanceClass);

        } catch (Exception e) {

            LoggerUtils.logException(e);

            throw new InstanceCreationFailedException(JEMessages.ADD_INSTANCE_FAILED + e.getMessage());

        }
        instancesLastValue.put(instanceModel.getInstanceId(), (JEObject) instance);
        return (JEObject) instance;
    }

    public static String getAttributeValue(String instanceId, String attributeName) {
        try {

            String dataReceived = DataModelRequester.getLastInstanceValue(instanceId);

            if (dataReceived != null) {
                var instance = objectMapper.readValue(dataReceived,
                        HashMap.class);
                var instanceAttributes = (HashMap) instance.get("Payload");
                //System.out.println(instanceAttributes);
                return instanceAttributes.get(attributeName)
                        .toString();
                //! old method not working anymore with new datamodel service changes
                //? HA: 13/06/2022
           /*     for (String attribute : (HashMap<String, Object>) ) {
                    if (attribute.get("attributeName")
                            .equals(attributeName)) {
                        return attribute.get("value")
                                .toString();
                    }
                }*/

            } else {
                JELogger.error(JEMessages.READ_INSTANCE_FAILED + instanceId, null, "", LogSubModule.JERUNNER,
                        instanceId);

            }

        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error(JEMessages.READ_INSTANCE_FAILED + instanceId + " " + e.getMessage(), null, "",
                    LogSubModule.JERUNNER, instanceId);
        }
        return null;

    }

    public static JEObject getInstance(String instanceId) {
        return instancesLastValue.contains(instanceId) ? instancesLastValue.get(instanceId)
                : getInstanceValueFromDataModel(instanceId);

    }

    private static JEObject getInstanceValueFromDataModel(String instanceId) {

        try {

            String data = DataModelRequester.getLastInstanceValue(instanceId);

            if (data != null && !data.isEmpty()) {
                return createInstance(data);
            }
            JELogger.error(JEMessages.READ_INSTANCE_FAILED + instanceId, null, "", LogSubModule.JERUNNER, instanceId);

        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error(JEMessages.READ_INSTANCE_FAILED + instanceId + " " + e.getMessage(), null, "",
                    LogSubModule.JERUNNER, instanceId);
        }
        return null;
    }


    public static void writeToDataModelInstance(String instanceId, String attributeName, Object attributeValue,
                                                boolean ignoreSameValue, boolean... numbers) {

        if (ignoreSameValue) {
            Object currentValue = getAttributeValue(instanceId, attributeName);
            if (Comparator.isSameValue(currentValue, attributeValue)) {
                return;
            }

        }
        String dataReceived = DataModelRequester.writeToInstance(instanceId, attributeName, attributeValue);
        if (dataReceived != null && !dataReceived.isEmpty()) {

            JSONObject response = new JSONObject(dataReceived);
            JELogger.trace("Data Model responded with : " + response, LogCategory.RUNTIME, null, LogSubModule.RULE,
                    null);
            if (!response.getJSONArray("Success")
                    .isEmpty()) {
                JELogger.trace(JEMessages.INSTANCE_UPDATE_SUCCESS, LogCategory.RUNTIME, null, LogSubModule.RULE, null);
            } else {
                JELogger.error(
                        JEMessages.WRITE_INSTANCE_FAILED + response.getJSONObject("Fail") + ". "
                                + JEMessages.CHECK_DM_FOR_DETAILS,
                        LogCategory.RUNTIME, "", LogSubModule.JERUNNER, instanceId);
            }

        } else {
            JELogger.error(JEMessages.WRITE_INSTANCE_FAILED, LogCategory.RUNTIME, null, LogSubModule.RULE, null);
        }

    }

}
