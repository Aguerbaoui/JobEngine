package io.je.runtime.data;

import java.util.Arrays;

import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.beans.JEData;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.zmq.ZMQBind;
import utils.zmq.ZMQSubscriber;

public class ZMQAgent extends ZMQSubscriber {

	public ZMQAgent(String url, int subPort, String topic) {
		super(url, subPort, topic);
	}

	@Override
	public void run() {
		JELogger.control("[topic = " + topic + "]" + JEMessages.DATA_LISTENTING_STARTED, LogCategory.RUNTIME, null,
				LogSubModule.JERUNNER, null);
		while (listening) {
			String data = null;
			try {
				data = this.getSubSocket(ZMQBind.CONNECT).recvStr();
			} catch (Exception ex) {
				closeSocket();
				JELogger.trace(ex.getMessage(), LogCategory.RUNTIME, null, LogSubModule.JERUNNER, topic);
				continue;
			}

			try {
				if (data != null && !data.equals(topic) && !data.startsWith(topic)) {
					JELogger.trace(JEMessages.DATA_RECEIVED + data, LogCategory.RUNTIME, null, LogSubModule.JERUNNER,
							null);
					RuntimeDispatcher.injectData(new JEData(this.topic, data));

				}
			} catch (Exception e) {
				JELogger.error(JEMessages.UKNOWN_ERROR + Arrays.toString(e.getStackTrace()), LogCategory.RUNTIME, null,
						LogSubModule.JERUNNER, null);
			}

		}

		closeSocket();

	}

}
