package io.je.utilities.instances;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.zmq.ZMQRequester;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataModelRequester {

    //private static ZMQRequester requester = new ZMQRequester("tcp://"+SIOTHConfigUtility.getSiothConfig().getMachineCredentials().getIpAddress(), SIOTHConfigUtility.getSiothConfig().getDataModelPORTS().getDmService_ReqAddress());
    private static ZMQRequester requester = new ZMQRequester("tcp://" + SIOTHConfigUtility.getSiothConfig()
            .getNodes()
            .getSiothMasterNode(), SIOTHConfigUtility.getSiothConfig()
            .getDataModelPORTS()
            .getDmService_ReqAddress());
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static TypeFactory typeFactory = objectMapper.getTypeFactory();


    /*
     * request to write in an instance's attribute
     */
    public static String writeToInstance(String instanceId, String attributeName, Object attributeNewValue) {

        //Generate request
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("Type", "Write");

        payload.put("InstanceId", instanceId);
        List<HashMap<String, Object>> attributesList = new ArrayList<>();
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("Name", attributeName);
        attributes.put("Value", attributeNewValue);
        attributesList.add(attributes);
        payload.put("Attributes", attributesList);
        String request = "";
        try {
            request = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {

        }

        String response = requester.sendRequest(request);
        return response;
    }

    /*
     * request to get last values by class Id
     */
    public static List<Object> readInitialValues(String topic) {
        JELogger.trace("Loading last values for topic = " + topic, LogCategory.RUNTIME,
                null, LogSubModule.JERUNNER, null);
        List<Object> values = new ArrayList<Object>();

        try {
            HashMap<String, String> requestMap = new HashMap<>();
            requestMap.put("Type", "ReadInitialValues");
            requestMap.put("ModelId", topic);
            String data = requester.sendRequest(objectMapper.writeValueAsString(requestMap));
            JELogger.trace(JEMessages.DATA_RECEIVED + data, LogCategory.RUNTIME,
                    null, LogSubModule.JERUNNER, null);
            if (data != null) {
                values = objectMapper.readValue(data, typeFactory.constructCollectionType(List.class, Object.class));

            }
        } catch (IOException e) {
            JELogger.error(JEMessages.FAILED_INIT_DATAMODEL + topic, null, "", LogSubModule.JERUNNER, topic);
        }
        return values;

    }

    /*
     * ZMQ Request to DataModel to read last values for specific instance(by InstanceId)
     */
    public static String getLastInstanceValue(String instanceId, Boolean... isName) {

        try {


            //ZMQRequester requester = new ZMQRequester("tcp://192.168.4.169"/*+SIOTHConfigUtility.getSiothConfig().getMachineCredentials().getIpAddress()*/, SIOTHConfigUtility.getSiothConfig().getDataModelPORTS().getDmService_ReqAddress());
            HashMap<String, String> requestMap = new HashMap<>();
            requestMap.put("Type", "ReadInstance");

            requestMap.put(isName.length > 0 && Boolean.TRUE.equals(isName[0]) ? "InstanceName" : "InstanceId", instanceId);
            String data = requester.sendRequest(objectMapper.writeValueAsString(requestMap));
            JELogger.trace(JEMessages.DATA_RECEIVED + " : " + data, LogCategory.RUNTIME,
                    null, LogSubModule.JERUNNER, null);
            if (data != null) {
                return data;

            }
        } catch (JsonProcessingException e) {
            JELogger.error(JEMessages.FAILED_INIT_DATAMODEL + instanceId, null, "", LogSubModule.JERUNNER, instanceId);
        }
        return null;
    }

}
