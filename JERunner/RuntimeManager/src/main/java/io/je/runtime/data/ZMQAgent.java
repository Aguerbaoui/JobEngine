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

		for (String id : topics)
		{
			requestInitialValues(id);
		}

		JELogger.control("ZMQAgent : topics : " + topics + " : " + JEMessages.DATA_LISTENTING_STARTED,
				LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);

		String last_topic = null;

		while (listening) {
			String data = null;
			try {
				data = this.getSubSocket(ZMQBind.CONNECT).recvStr();
			} catch (Exception ex) {
				//JELogger.trace(ex.getMessage(), LogCategory.RUNTIME, null, LogSubModule.JERUNNER, "topics");
				ex.printStackTrace(); // FIXME could have a lot of :
				/*
				org.zeromq.ZMQException: Errno 4 : errno 4
				at org.zeromq.ZMQ$Socket.mayRaise(ZMQ.java:3546)
				at org.zeromq.ZMQ$Socket.recv(ZMQ.java:3377)
				at org.zeromq.ZMQ$Socket.recvStr(ZMQ.java:3463)
				at org.zeromq.ZMQ$Socket.recvStr(ZMQ.java:3444)
				at io.je.runtime.data.ZMQAgent.run(Unknown Source)
				at java.base/java.lang.Thread.run(Thread.java:834)
				*/
				continue;
			}

			JELogger.trace(JEMessages.DATA_RECEIVED + data,
					LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);

			try {
				if (data == null) continue;

				if (last_topic == null) {
					for (String topic : topics) {
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
				JELogger.error(JEMessages.UKNOWN_ERROR + Arrays.toString(e.getStackTrace()), LogCategory.RUNTIME, null,
						LogSubModule.JERUNNER, null);
			}

		}

		closeSocket();

	}

}
