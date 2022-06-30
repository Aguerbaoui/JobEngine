package io.je.runtime.data;

import java.util.Arrays;
import java.util.Set;

import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.beans.JEData;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.zmq.ZMQBind;
import utils.zmq.ZMQSubscriber;

public class ZMQAgent extends ZMQSubscriber {

	public ZMQAgent(String url, int subPort, Set<String> topics) {
		super(url, subPort, topics);
	}

	@Override
	public void run() {
		JELogger.control("[topics = " + topics + "]" + JEMessages.DATA_LISTENTING_STARTED, LogCategory.RUNTIME, null,
				LogSubModule.JERUNNER, null);

		String last_topic = null;

		while (listening) {
			String data = null;
			try {
				data = this.getSubSocket(ZMQBind.CONNECT).recvStr();
				//System.err.println("ZMQAgent data : " + data);
			} catch (Exception ex) {
				// Do not close socket on exceptions
				//closeSocket();
				JELogger.trace(ex.getMessage(), LogCategory.RUNTIME, null, LogSubModule.JERUNNER, "topics");
				continue;
			}

			try {
				if (data == null) continue;

				if (last_topic == null) {
					for (String topic : topics) {
						if (data.equals(topic)) {
							last_topic = topic;
							break;
						}
					}
				} else {
					JELogger.trace(JEMessages.DATA_RECEIVED + data,
							LogCategory.RUNTIME, null, LogSubModule.JERUNNER, last_topic);

					RuntimeDispatcher.injectData(new JEData(last_topic, data));

					last_topic = null;
				}

			} catch (Exception e) {
				JELogger.error(JEMessages.UKNOWN_ERROR + Arrays.toString(e.getStackTrace()), LogCategory.RUNTIME, null,
						LogSubModule.JERUNNER, null);
			}

		}

		closeSocket();

	}

}
