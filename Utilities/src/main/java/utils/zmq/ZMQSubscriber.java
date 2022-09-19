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
    protected ZMQType bindType = null;

    protected String url = null;

    protected int subscriberPort = 0;

    protected String connectionAddress = null;

    protected Set<String> topics = Collections.synchronizedSet(new HashSet());

    protected volatile boolean listening = true;


    public ZMQSubscriber(String url, int subPort) {
        this.url = url;
        this.subscriberPort = subPort;
        this.connectionAddress = url + ":" + subscriberPort;

        this.context = new ZContext();
    }

    public ZMQSubscriber() {
        this.context = new ZContext();
    }

    protected Socket getSubscriberSocket() throws ZMQConnectionFailedException {
        return getSubscriberSocket(null);
    }

    protected Socket getSubscriberSocket(ZMQType bindType) throws ZMQConnectionFailedException {

        if (socket == null) {

            this.bindType = bindType;

            LoggerUtils.info("ZMQ subscriber : Create socket for address : " + connectionAddress);

            this.context.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
            this.context.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);

            try {

                socket = this.context.createSocket(SocketType.SUB);

                socket.setHeartbeatTimeout(ZMQConfiguration.HEARTBEAT_TIMEOUT);
                socket.setHandshakeIvl(ZMQConfiguration.HANDSHAKE_INTERVAL);
                socket.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
                socket.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);
                socket.setReceiveTimeOut(ZMQConfiguration.RECEIVE_TIMEOUT);
                socket.setSendTimeOut(ZMQConfiguration.SEND_TIMEOUT);

                if (ZMQSecurity.isSecure()) {
                    // Client specify server key
                    socket.setCurveServerKey(ZMQSecurity.getServerPair().publicKey.getBytes());

                    socket.setCurveSecretKey(ZMQSecurity.getServerPair().secretKey.getBytes());
                    socket.setCurvePublicKey(ZMQSecurity.getServerPair().publicKey.getBytes());
                }

                if (bindType == ZMQType.BIND) {
                    socket.bind(connectionAddress);
                    LoggerUtils.info("ZMQ subscriber : Bind succeeded to : " + connectionAddress);
                } else {
                    socket.connect(connectionAddress);
                    LoggerUtils.info("ZMQ subscriber : Connection succeeded to : " + connectionAddress);
                }

            } catch (Exception e) {

                LoggerUtils.error("ZMQ subscriber : Failed to connect to address : " + connectionAddress + " : " + e.getMessage());

                LoggerUtils.logException(e);

                try {
                    this.closeSocket();

                    int wait_ms = 15000;

                    LoggerUtils.info("ZMQ subscriber : Socket closed. Will wait in milliseconds for : " + wait_ms);

                    Thread.sleep(wait_ms);
                } catch (InterruptedException ie) {
                    LoggerUtils.logException(ie);
                    Thread.currentThread().interrupt();
                }

                throw new ZMQConnectionFailedException(0, "Failed to connect to address " + connectionAddress + " : " + e.getMessage());
            }

        }

        return socket;
    }

    public void closeSocket() {
        if (socket != null) {

            if (bindType == ZMQType.BIND) {
                this.socket.unbind(connectionAddress);
                LoggerUtils.info("ZMQ subscriber : Unbind succeeded from : " + connectionAddress);
            } else {
                this.socket.disconnect(connectionAddress);
                LoggerUtils.info("ZMQ subscriber : Disconnection succeeded from : " + connectionAddress);
            }

            socket.close();
            context.destroySocket(socket);
            socket = null;
        }
    }

    public void addTopic(String topic, ZMQType bindType) throws ZMQConnectionFailedException {
        if (!topics.contains(topic)) {
            topics.add(topic);
        }
        // FIXME could be dangerous re-subscribing
        getSubscriberSocket(bindType).subscribe(topic.getBytes());
    }

    public void addTopic(String topic) throws ZMQConnectionFailedException {
        if (!topics.contains(topic)) {
            topics.add(topic);
        }
        // FIXME could be dangerous re-subscribing
        getSubscriberSocket().subscribe(topic.getBytes());
    }

    public void removeTopic(String topic) throws ZMQConnectionFailedException {
        if (topics.contains(topic)) {
            topics.remove(topic);
        }
        // FIXME could be dangerous re-unsubscribing
        getSubscriberSocket().unsubscribe(topic.getBytes());
    }

    public boolean hasTopic(String topic) {
        return topics.contains(topic);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSubscriberPort() {
        return subscriberPort;
    }

    public void setSubscriberPort(int subscriberPort) {
        this.subscriberPort = subscriberPort;
    }

    public String getConnectionAddress() {
        return connectionAddress;
    }

    public void setConnectionAddress(String connectionAddress) {
        this.connectionAddress = connectionAddress;
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
*/


    // public abstract void handleConnectionFail();

}
