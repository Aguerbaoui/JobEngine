package io.je.Monitor.zmq;

import static io.je.utilities.constants.JEMessages.STARTED_LISTENING_FOR_MONITORING_DATA_FROM_THE_JOB_ENGINE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;

import io.je.Monitor.service.WebSocketService;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.zmq.ZMQBind;
import utils.zmq.ZMQSubscriber;

@Component
public class MonitoringSubscriber extends ZMQSubscriber {

	@Autowired
	WebSocketService service;

	public MonitoringSubscriber(String url, int subPort, String topic) {
		super(url, subPort, topic);
	}

	public void setConfig(String url, int subPort, String topic) {
		this.url = url;
		this.subPort = subPort;
		this.topic = topic;
	}

	public MonitoringSubscriber() {

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

		while (listening) {
			String data = null;

			try {
				data = this.getSubSocket(ZMQBind.BIND).recvStr();
				if (data != null && !data.equals(topic) && !data.startsWith(topic)) {
					JELogger.debug(data);
					service.sendUpdates(data);

				}
			} catch (Exception e) {
				JELogger.error(e.toString(), null, "", null, "");
				closeSocket();
			}

		}

	}

}
