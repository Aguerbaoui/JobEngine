package utils.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.List;
import java.util.Set;

public abstract class ZMQSubscriber implements Runnable {

	protected ZContext context = null;

	protected ZMQ.Socket subSocket = null;

	protected String url;

	protected int subPort;

	protected Set<String> topics;

	protected volatile boolean listening = false;

	public ZMQSubscriber(String url, int subPort, Set<String> topics) {
		this.url = url;
		this.subPort = subPort;
		this.topics = topics;
		this.context = new ZContext();
	}

	public ZMQSubscriber() {
		super();
		this.context = new ZContext();
	}

	public void closeSocket() {

		if (subSocket != null) {
			subSocket.close();
			context.destroySocket(subSocket);
			subSocket = null;
		}

	}

	public void connectToAddress(ZMQBind bindType) throws ZMQConnectionFailedException {
		try {
			if (subSocket == null) {
				subSocket = this.context.createSocket(SocketType.SUB);
				subSocket.setHeartbeatTimeout(ZMQConfiguration.HEARTBEAT_TIMEOUT);
				subSocket.setHandshakeIvl(ZMQConfiguration.HANDSHAKE_INTERVAL);
				subSocket.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
				subSocket.setReceiveTimeOut(-1);

				if (bindType == ZMQBind.CONNECT) {
					subSocket.connect(url + ":" + subPort);
				} else if (bindType == ZMQBind.BIND) {
					subSocket.bind(url + ":" + subPort);
				}
			}

			for (String topic : topics) {
				subSocket.subscribe(topic.getBytes());
			}

			if (ZMQSecurity.isSecure()) {
				subSocket.setCurveServerKey(ZMQSecurity.getServerPair().publicKey.getBytes());
				subSocket.setCurveSecretKey(ZMQSecurity.getServerPair().secretKey.getBytes());
				subSocket.setCurvePublicKey(ZMQSecurity.getServerPair().publicKey.getBytes());
			}
		} catch (Exception e) {
			closeSocket();
			throw new ZMQConnectionFailedException(0,"Failed to connect to address [ "+url + ":" + subPort+"]: "+ e.toString());
		}
	}

	public ZMQ.Socket getSubSocket(ZMQBind bindType) throws ZMQConnectionFailedException {
		//if (subSocket == null) {

			connectToAddress(bindType);

		//}
		return subSocket;
	}

	public void removeTopics(List<String> topics) {
		for (String topic : topics) {
			subSocket.unsubscribe(topic.getBytes());
			this.topics.remove(topic);
		}
		if (this.topics.isEmpty()) {
			setListening(false);
		}
	}

	public boolean isListening() {
		return listening;
	}

	public void setListening(boolean listening) {
		this.listening = listening;
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

	public Set<String> getTopics() {
		return topics;
	}

	public void setTopics(Set<String> topics) {
		this.topics = topics;
	}

	// public abstract void handleConnectionFail();

}
