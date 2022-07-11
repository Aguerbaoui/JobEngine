package io.je.runtime.data;

import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.beans.JEData;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.zmq.ZMQBind;
import utils.zmq.ZMQSubscriber;

import java.util.Arrays;


public class DataZMQSubscriber extends ZMQSubscriber {

	public DataZMQSubscriber(String url, int subPort) {
		super(url, subPort);
	}

	@Override
	public void run() {

		synchronized (this) {

			final String ID_MSG = "DataZMQSubscriber : ";

			JELogger.debug(ID_MSG + "topics : " + this.topics + " : " + JEMessages.DATA_LISTENTING_STARTED,
					LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);

			String last_topic = null;

			while (this.listening) {
				String data = null;
				try {
					data = this.getSubSocket(ZMQBind.CONNECT).recvStr();
				} catch (Exception ex) {
					ex.printStackTrace();
					continue;
				}

				JELogger.trace(ID_MSG + JEMessages.DATA_RECEIVED + data,
						LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);

				try {
					if (data == null) continue;

					// FIXME waiting to have topic in the same response message
					if (last_topic == null) {

						for (String topic : this.topics) {
							// Instance case or Class case
							if (data.equals(topic) || data.split("#")[0].equals(topic)) {
								last_topic = topic;
								break;
							}
						}

					} else {

						RuntimeDispatcher.injectData(new JEData(last_topic, data));

						last_topic = null;
					}

				} catch (Exception e) {
					JELogger.error(ID_MSG + JEMessages.UKNOWN_ERROR + Arrays.toString(e.getStackTrace()),
							LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);
				}

			}

			JELogger.debug(ID_MSG + JEMessages.CLOSING_SOCKET,
					LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);

			this.closeSocket();

		}

	}

}
