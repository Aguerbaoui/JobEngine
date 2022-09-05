package utils.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import utils.log.LoggerUtils;

public abstract class ZMQResponder implements Runnable {

    protected ZContext context = null;
    protected String url;
    protected int repPort;
    protected volatile boolean listening = false;
    protected ZMQBind bindType;
    private ZMQ.Socket repSocket = null;

    public ZMQResponder(String url, int subPort, ZMQBind bindType) {
        this.url = url;
        this.repPort = subPort;
        this.context = new ZContext();
        this.bindType = bindType;
    }

    protected ZMQResponder() {
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
            // TODO check if config OK for ZMQ responder
            this.context.setRcvHWM(0);
            this.context.setSndHWM(0);

            this.repSocket = this.context.createSocket(SocketType.REP);
            this.repSocket.setReceiveTimeOut(30000);

            this.repSocket.setHeartbeatTimeout(ZMQConfiguration.HEARTBEAT_TIMEOUT);
            this.repSocket.setHandshakeIvl(ZMQConfiguration.HANDSHAKE_INTERVAL);
            this.repSocket.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
            this.repSocket.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);

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
            LoggerUtils.logException(e);
            closeSocket();
            throw new ZMQConnectionFailedException(0, "Failed to connect to address [ " + url + ":" + repPort + "]: " + e.toString());
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


}
