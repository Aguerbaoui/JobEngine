package utils.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;
import utils.log.LoggerUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class ZMQSubscriber implements Runnable {

    protected ZContext context = null;

    protected Socket socket = null;

    protected String url = null;

    protected int port = 0;

    protected Set<String> topics = Collections.synchronizedSet(new HashSet());

    protected volatile boolean listening = true;


    public ZMQSubscriber(String url, int subPort) {
        this.url = url;
        this.port = subPort;
        this.context = new ZContext();
    }

    public ZMQSubscriber() {
        super();
        this.context = new ZContext();
    }

    protected Socket getSubSocket() throws ZMQConnectionFailedException {
        return getSubSocket(null);
    }

    protected Socket getSubSocket(ZMQBind bindType) throws ZMQConnectionFailedException {

        if (socket == null) {
            try {
                this.context.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
                this.context.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);

                socket = this.context.createSocket(SocketType.SUB);

                socket.setHeartbeatTimeout(ZMQConfiguration.HEARTBEAT_TIMEOUT);
                socket.setHandshakeIvl(ZMQConfiguration.HANDSHAKE_INTERVAL);
                socket.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
                socket.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);
                socket.setReceiveTimeOut(-1);

                if (ZMQSecurity.isSecure()) {
                    // Client specify server key
                    socket.setCurveServerKey(ZMQSecurity.getServerPair().publicKey.getBytes());

                    socket.setCurveSecretKey(ZMQSecurity.getServerPair().secretKey.getBytes());
                    socket.setCurvePublicKey(ZMQSecurity.getServerPair().publicKey.getBytes());
                }

                if (bindType == ZMQBind.BIND) {
                    socket.bind(url + ":" + port);
                } else {
                    socket.connect(url + ":" + port);
                }

            } catch (Exception exp) {
                LoggerUtils.logException(exp);
                closeSocket();
                throw new ZMQConnectionFailedException(0, "Failed to connect to address [ " + url + ":" + port + "]: " + exp);
            }
        }

        return socket;
    }

    public void closeSocket() {
        if (socket != null) {
            socket.close();
            context.destroySocket(socket);
            socket = null;
        }
    }

    public void addTopic(String topic) throws ZMQConnectionFailedException {
        if (!topics.contains(topic)) {
            topics.add(topic);
            getSubSocket().subscribe(topic.getBytes());
        }
    }

    public void removeTopic(String topic) throws ZMQConnectionFailedException {
        if (topics.contains(topic)) {
            topics.remove(topic);
            getSubSocket().unsubscribe(topic.getBytes());
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
