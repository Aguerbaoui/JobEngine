package io.je.ruleengine.data;

import io.je.utilities.beans.JEData;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;
import utils.zmq.ZMQSubscriber;

import java.util.Iterator;
import java.util.Set;


public class DataZMQSubscriber extends ZMQSubscriber {

    public DataZMQSubscriber(String url, int subPort) {
        super(url, subPort);
    }

    @Override
    public void run() {

        final String ID_MSG = "DataZMQSubscriber : ";

        try {

            // Reset if existing socket
            this.closeSocket();

            Thread.sleep(100);

            // Re-subscribe if Thread was restarted
            for (String topic : this.topics) {

                this.addTopic(topic);

            }

            JELogger.debug(ID_MSG + "topics : " + this.topics + " : " + JEMessages.STARTED_LISTENING_FOR_DATA,
                    LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);

            String data, last_topic = null;

            while (this.listening) {

                data = this.getSubscriberSocket().recvStr();

                // This trace serve to detect if this thread is still running or interrupted
                LoggerUtils.trace(ID_MSG + JEMessages.DATA_RECEIVED + data);

                if (data == null) {
                    continue;
                }

                // FIXME waiting to have topic in the same response message
                if (last_topic == null) {

                    Set<String> _topics = Set.copyOf(this.topics);

                    Iterator<String> iterator = _topics.iterator();
                    while (iterator.hasNext()) {
                        String topic = iterator.next();
                        // Instance case or Class case
                        if (data.equals(topic) || data.split("#")[0].equals(topic)) {
                            last_topic = topic;
                            break;
                        }
                    }

                } else {

                    DataModelListener.injectData(new JEData(last_topic, data));

                    last_topic = null;
                }

            }


        } catch (Exception exp) {

            LoggerUtils.logException(exp);

            JELogger.error(ID_MSG + JEMessages.UKNOWN_ERROR + exp.getMessage(),
                    LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);

        } finally {

            JELogger.debug(ID_MSG + JEMessages.CLOSING_SOCKET,
                    LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);

            try {

                Set<String> _topics = Set.copyOf(this.topics);

                Iterator<String> iterator = _topics.iterator();
                while (iterator.hasNext()) {
                    this.removeTopic(iterator.next());
                }

            } catch (Exception e) {
                LoggerUtils.logException(e);
            }

            this.closeSocket();

            // Restart DataModelListener Thread if end was due to exception
            if (this.listening) {
                DataModelListener.initThreadDataZMQSubscriber();
            }

        }

    }

}
