package utils.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public abstract class ZMQSubscriber implements Runnable {

	protected ZContext context = null;

	protected ZMQ.Socket subSocket = null;

	protected String url;

	protected int subPort;

	protected String topic;

	protected boolean listening = false;

	public ZMQSubscriber(String url, int subPort, String topic) {
		this.url = url;
		this.subPort = subPort;
		this.topic = topic;
		this.context = new ZContext();
	}

	public ZMQSubscriber() {
		super();
		this.context = new ZContext();
	}

	public void closeSocket() {
		if (this.subSocket != null) {
			this.subSocket.close();
			this.context.destroySocket(subSocket);
			this.subSocket = null;
		}
	}

	public void connectToAddress(ZMQBind bindType) throws ZMQConnectionFailedException {
		try {
			this.subSocket = this.context.createSocket(SocketType.SUB);
			this.subSocket.setReceiveTimeOut(60000);
			if (bindType == ZMQBind.CONNECT) {
				this.subSocket.connect(url + ":" + subPort);
			} else if (bindType == ZMQBind.BIND) {
				this.subSocket.bind(url + ":" + subPort);

			}
			this.subSocket.subscribe(topic.getBytes());
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
		if (subSocket == null) {

			connectToAddress(bindType);

		}
		return subSocket;
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

	public void setSubSocket(ZMQ.Socket subSocket) {
		this.subSocket = subSocket;
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

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	// public abstract void handleConnectionFail();

}
