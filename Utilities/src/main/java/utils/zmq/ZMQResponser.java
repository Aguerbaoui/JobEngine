package utils.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public abstract class ZMQResponser implements Runnable {

	protected ZContext context = null;

	private ZMQ.Socket repSocket = null;

	protected String url;

	protected int repPort;

	protected volatile boolean listening = false;
	
	protected ZMQBind bindType;

	public ZMQResponser(String url, int subPort,ZMQBind bindType) {
		this.url = url;
		this.repPort = subPort;
		this.context = new ZContext();
		this.bindType = bindType;
	}

	protected ZMQResponser() {
		super();
		this.context = new ZContext();
	}

	public void closeSocket() {
		if (this.repSocket != null) {
			this.repSocket.close();
			this.context.destroySocket(repSocket);
			this.repSocket = null;
		}
	}

	public void connectToAddress() throws ZMQConnectionFailedException {
		try {
			this.repSocket = this.context.createSocket(SocketType.REP);
			//this.repSocket.setReceiveTimeOut(100);

			if (ZMQSecurity.isSecure()) {
				this.repSocket.setCurveServer(true);
				this.repSocket.setCurveSecretKey(ZMQSecurity.getServerPair().secretKey.getBytes());
				this.repSocket.setCurvePublicKey(ZMQSecurity.getServerPair().publicKey.getBytes());
			}
			if (bindType == ZMQBind.CONNECT) {
				this.repSocket.connect(url + ":" + repPort);
			} else if (bindType == ZMQBind.BIND) {
				this.repSocket.bind(url + ":" + repPort);

			}
		} catch (Exception e) {
			closeSocket();
			throw new ZMQConnectionFailedException(0,"Failed to connect to address [ "+url + ":" + repPort+"]: "+ e.toString());
		}
	}

	public ZMQ.Socket getRepSocket(ZMQBind bindType) throws ZMQConnectionFailedException {
		if (repSocket == null) {

			connectToAddress();

		}
		return repSocket;
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

	public void setRepSocket(ZMQ.Socket subSocket) {
		this.repSocket = subSocket;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getRepPort() {
		return repPort;
	}

	public void setRepPort(int repPort) {
		this.repPort = repPort;
	}

	

	


	// public abstract void handleConnectionFail();

}
