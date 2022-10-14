package io.je.ruleengine.data;

import io.je.ruleengine.impl.RuleEngine;
import io.je.utilities.beans.JEData;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.InstanceCreationFailedException;
import io.je.utilities.instances.DataModelRequester;
import io.je.utilities.instances.InstanceManager;
import io.je.utilities.log.JELogger;
import io.je.utilities.runtimeobject.JEObject;
import io.siothconfig.SIOTHConfigUtility;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;
import utils.zmq.ZMQConnectionFailedException;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

public class DataModelListener {

    /*
     * Map of topic-listener
     * */

    static Map<String, DMTopic> allDMTopics = new HashMap<>();
    private static DataZMQSubscriber dataZMQSubscriber = null;
    private static Thread threadDataZMQSubscriber = null;

    // FIXME check if ok
    public static Set<String> getTopicsByProjectId(String projectId) {
        Set<String> topics = new HashSet<>();
        for (DMTopic topic : allDMTopics.values()) {
            if (topic.getProjects().contains(projectId)) {
                topics.add(topic.getId());
            }
        }
        return topics;
    }

    // FIXME check if ok
    public static Set<String> getRuleTopicsByProjectId(String projectId) {
        Set<String> topics = new HashSet<>();
        for (DMTopic topic : allDMTopics.values()) {
            if (topic.getProjects().contains(projectId)) {
                for (DMListener subscriber : topic.getListeners().values()) {
                    if (subscriber.getType().equals("rule")) {
                        topics.add(topic.getId());
                    }
                }
            }
        }
        return topics;
    }

    public static void stopListening(Set<String> topics) {

        for (String topic : topics) {
            stopListening(topic);
        }

    }

    public static void stopListening(String topic) {

        if (getDataZMQSubscriber().hasTopic(topic)) {

            JELogger.debug(JEMessages.STOPPING_LISTENING_TO_TOPIC + topic, LogCategory.RUNTIME,
                    null, LogSubModule.JERUNNER, null);

            try {

                getDataZMQSubscriber().removeTopic(topic);

            } catch (ZMQConnectionFailedException exp) {
                JELogger.logException(exp);

                JELogger.error(exp.getMessage(),
                        LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);
            }

        }

    }

    /*
         Only one DataZMQSubscriber for all data topics
     */
    private static DataZMQSubscriber getDataZMQSubscriber() {

        if (dataZMQSubscriber == null) {

            dataZMQSubscriber = new DataZMQSubscriber("tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(),
                    SIOTHConfigUtility.getSiothConfig().getDataModelPORTS().getDmService_PubAddress());

            initThreadDataZMQSubscriber();

        }

        return dataZMQSubscriber;
    }

    public static void initThreadDataZMQSubscriber() {

        interruptThreadDataZMQSubscriber();

        threadDataZMQSubscriber = new Thread(dataZMQSubscriber);

        threadDataZMQSubscriber.setName("threadDataZMQSubscriber");

        threadDataZMQSubscriber.start();

    }

    private static void interruptThreadDataZMQSubscriber() {

        if (threadDataZMQSubscriber != null) {
            if (threadDataZMQSubscriber.isAlive()) {
                threadDataZMQSubscriber.interrupt();
            }
            threadDataZMQSubscriber = null;
        }

    }

    public static void close() {

        interruptThreadDataZMQSubscriber();

        if (dataZMQSubscriber != null) {
            LoggerUtils.trace("Setting dataZMQSubscriber listening to false.");
            dataZMQSubscriber.setListening(false);
            dataZMQSubscriber.closeSocket();
            dataZMQSubscriber = null;
        }

    }

    public static void updateDMListener(DMListener dMListener, Set<String> topics) {

        for (String topic : topics) {
            if (!allDMTopics.containsKey(topic)) {
                allDMTopics.put(topic, new DMTopic(topic));
            }
            allDMTopics.get(topic).addListener(dMListener);
        }

        startListening(topics);

    }

    public static void startListening(Set<String> topics) {

        for (String topic : topics) {

            startListening(topic);

        }

    }

    public static void startListening(String topic) {

        if (!getDataZMQSubscriber().hasTopic(topic)) {

            JELogger.debug(JEMessages.LAUNCHING_LISTENING_TO_TOPIC + topic, LogCategory.RUNTIME,
                    null, LogSubModule.JERUNNER, null);

            try {

                getDataZMQSubscriber().addTopic(topic);

            } catch (ZMQConnectionFailedException exp) {
                JELogger.logException(exp);

                JELogger.error(exp.getMessage(),
                        LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);
            }

            Thread thread = new Thread(() -> requestInitialValue(topic));
            thread.start();
        }

    }

    /*
        ZMQ is missing last value caching (LVC)
        https://zguide.zeromq.org/docs/chapter5/

        We do it by requestInitialValues
     */
    // TODO check if it is better to request initial values in single request by sending all topics
    public static void requestInitialValue(String topic) {
        String initialValue = null;

        String[] splittedTopic = topic.split("#");
        if (splittedTopic.length > 1) {
            // Case topic containing instance Id
            initialValue = DataModelRequester.getLastInstanceValue(splittedTopic[1], false);
        } else if (splittedTopic.length == 1) {
            // Case topic containing just modelId (Class)
            initialValue = DataModelRequester.readInitialValues(splittedTopic[0]);
        }

        try {

            injectData(new JEData(topic, initialValue));

        } catch (Exception e) {
            LoggerUtils.logException(e);
        }

    }

    /**
     * Inject data into the rule/workflow engine according to the topics they are subscribed to (to prevent duplication)
     */
    public static void injectData(JEData jeData) {
        JELogger.trace(JEMessages.INJECTING_DATA, LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);
        try {
            CompletableFuture.runAsync(() -> {
                JEObject instanceData;
                String projectId = null;

                try {

                    instanceData = InstanceManager.createInstance(jeData.getData());

                    for (String id : DataModelListener.getProjectsSubscribedToTopic(jeData.getTopic())) {
                        projectId = id;

                        RuleEngine.insertFact(projectId, instanceData);

                    }
                } catch (InstanceCreationFailedException e) {
                    LoggerUtils.logException(e);
                    JELogger.warn(JEMessages.ADD_INSTANCE_FAILED + e.getMessage(),
                        LogCategory.RUNTIME, projectId,
                        LogSubModule.RULE, null);
                }

            });
        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error(JEMessages.FAILED_TO_INJECT_DATA + e.getMessage(), LogCategory.RUNTIME, null,
                    LogSubModule.JERUNNER, null);
        }

    }

    public static void removeDMListener(String listenerId) {

        for (Entry<String, DMTopic> topic : allDMTopics.entrySet()) {

            if (topic.getValue().hasListener(listenerId)) {
                topic.getValue().removeListener(listenerId);
            }

            stopListeningOnTopicIfNoSubscribers(topic.getValue());

        }

    }

    public static void stopListeningOnTopicIfNoSubscribers(DMTopic topic) {
        if (!topic.hasListeners()) {
            stopListening(topic.getId());
        }
    }

    public static void removeListenersByProjectId(String projectId) {
        // FIXME check if ok
        for (Entry<String, DMTopic> topic : allDMTopics.entrySet()) {
            topic.getValue().removeAllProjectListeners(projectId);
            stopListeningOnTopicIfNoSubscribers(topic.getValue());
        }

    }

    public static List<String> getProjectsSubscribedToTopic(String topic) {

        return allDMTopics.get(topic).getProjects();

    }

}
