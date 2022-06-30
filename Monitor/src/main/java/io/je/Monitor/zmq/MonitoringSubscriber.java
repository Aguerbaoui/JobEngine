package io.je.Monitor.zmq;

import io.je.Monitor.service.WebSocketService;
import io.je.utilities.log.JELogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.zmq.ZMQBind;
import utils.zmq.ZMQSubscriber;

import java.util.Set;

import static io.je.utilities.constants.JEMessages.STARTED_LISTENING_FOR_MONITORING_DATA_FROM_THE_JOB_ENGINE;

@Component
public class MonitoringSubscriber extends ZMQSubscriber {

	@Autowired
	WebSocketService service;


	public void setConfig(String url, int subPort, Set<String> topics) {
		this.url = url;
		this.subPort = subPort;
		this.topics = topics;
	}

	@Override
	public ZMQ.Socket getSubSocket(ZMQBind bindType) {
		boolean connectionSucceeded = false;
		if (subSocket == null) {

			try {
				JELogger.info("Attempting to connect to address: " + url + ":" + subPort + "...", null, "", null, "");

				connectToAddress(bindType);
				JELogger.info("connection succeeded.", null, "", null, "");

				connectionSucceeded = true;

			} catch (Exception e) {
				connectionSucceeded = false;
				JELogger.error(e.getMessage(), null, "", null, "");

			}
			if (!connectionSucceeded) {
				JELogger.info(" Trying to establish connection with address: " + url + ":" + subPort + "...", null, "",
						null, "");
			}
			while (!connectionSucceeded) {
				try {
					connectToAddress(bindType);
					connectionSucceeded = true;
					JELogger.info("Connection succeeded.", null, "", null, "");

				} catch (Exception e) {
					connectionSucceeded = false;

				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}

			}

			JELogger.control(STARTED_LISTENING_FOR_MONITORING_DATA_FROM_THE_JOB_ENGINE, LogCategory.MONITOR, null,
					LogSubModule.JEMONITOR, null);
		}

		return subSocket;
	}

	@Override
	public void run() {

		String last_topic = null;

		while (listening) {
			String data = null;

			try {
				data = this.getSubSocket(ZMQBind.BIND).recvStr();

				if (data == null) continue;

				if (last_topic == null) {
					for (String topic : topics) {
						if (data.startsWith(topic)) {
							last_topic = topic;
							break;
						}
					}
				} else {

					JELogger.debug(data);
					service.sendUpdates(data);

					last_topic = null;
				}
			} catch (Exception e) {
				JELogger.error(e.toString(), null, "", null, "");
			}

		}

		closeSocket();

	}

}
