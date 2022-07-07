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
import java.util.Set;

import static io.je.runtime.data.DataModelListener.requestInitialValues;

public class ZMQAgent extends ZMQSubscriber {

	public ZMQAgent(String url, int subPort, Set<String> topics) {
		super(url, subPort, topics);
	}

	@Override
	public void run() {

		synchronized (this) {

			for (String id : this.topics) {
				requestInitialValues(id);
			}

			final String ID_MSG = "ZMQAgent Subscriber : ";

			JELogger.debug(ID_MSG + "topics : " + this.topics + " : " + JEMessages.DATA_LISTENTING_STARTED,
					LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);

			String last_topic = null;

			while (this.listening) {
				String data = null;
				try {
					data = this.getSubSocket(ZMQBind.CONNECT).recvStr();
				} catch (Exception ex) {
					// FIXME could have a lot of : org.zeromq.ZMQException: Errno 4
					ex.printStackTrace();
					continue;
				}

				JELogger.trace(ID_MSG + JEMessages.DATA_RECEIVED + data,
						LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);

				try {
					if (data == null) continue;

					if (last_topic == null) {
						for (String topic : this.topics) {
							if (data.startsWith(topic)) {
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
