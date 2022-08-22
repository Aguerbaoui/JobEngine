package utils.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import utils.log.LoggerUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class ZMQSubscriber implements Runnable {

	protected ZContext context = null;

	protected ZMQ.Socket socket = null;

	protected String url = null;

	protected int port = 0;

	protected Set<String> topics = Collections.synchronizedSet(new HashSet());

	protected volatile boolean listening = true;

	public ZMQSubscriber(String url, int subPort) {
		init();
		this.url = url;
		this.port = subPort;
	}

	public ZMQSubscriber() {
		super();
	}

	protected void init() {
		this.context = new ZContext();
		this.context.setRcvHWM(0);
		this.context.setSndHWM(0);

		if (socket == null) {
			socket = this.context.createSocket(SocketType.SUB);
			socket.setHeartbeatTimeout(ZMQConfiguration.HEARTBEAT_TIMEOUT);
			socket.setHandshakeIvl(ZMQConfiguration.HANDSHAKE_INTERVAL);
			socket.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
			socket.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);
			socket.setReceiveTimeOut(-1);

			if (ZMQSecurity.isSecure()) {
				socket.setCurveServerKey(ZMQSecurity.getServerPair().publicKey.getBytes());
				socket.setCurveSecretKey(ZMQSecurity.getServerPair().secretKey.getBytes());
				socket.setCurvePublicKey(ZMQSecurity.getServerPair().publicKey.getBytes());
			}
		}
	}

	public void connectToAddress(ZMQBind bindType) throws ZMQConnectionFailedException {
		try {

			// FIXME check if we need to disconnect or unbind before
			if (bindType == ZMQBind.CONNECT) {
				socket.connect(url + ":" + port);
			} else if (bindType == ZMQBind.BIND) {
				socket.bind(url + ":" + port);
			}

		} catch (Exception exp) {
			LoggerUtils.logException(exp);
			closeSocket();
			throw new ZMQConnectionFailedException(0, "Failed to connect to address [ " + url + ":" + port + "]: " + exp);
		}
	}

	public ZMQ.Socket getSubSocket(ZMQBind bindType) throws ZMQConnectionFailedException {

		connectToAddress(bindType);

		return socket;
	}

	public void closeSocket() {

		if (socket != null) {
			socket.close();
			context.destroySocket(socket);
			socket = null;
		}

	}

	public void addTopic(String topic) {
		if (!topics.contains(topic)) {
			topics.add(topic);
			socket.subscribe(topic.getBytes());
		}
	}

	public void removeTopic(String topic) {
		if (topics.contains(topic)) {
			topics.remove(topic);
			socket.unsubscribe(topic.getBytes());
		}
	}

	public boolean hasTopic(String topic) {
		return topics.contains(topic);
	}

	/*

	public boolean isListening() {
		return listening;
	}

	public void stopListening() {
		this.listening = false;
	}

	public Set<String> getTopics() {
		return topics;
	}

	public void setTopics(Set<String> topics) {
		this.topics = topics;
	}

	public ZContext getContext() {
		return context;
	}

	public void setContext(ZContext context) {
		this.context = context;
	}

	public void setSubSocket(ZMQ.Socket _subSocket) {
		subSocket = _subSocket;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getSubPort() {
		return subPort;
	}

	public void setSubPort(int subPort) {
		this.subPort = subPort;
	}
*/

	// public abstract void handleConnectionFail();

}
