package utils.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import utils.log.LoggerUtils;

public abstract class ZMQResponder implements Runnable {

    protected ZContext context = null;
    protected String url = null;
    protected int responderPort = 0;
    protected volatile boolean listening = true;
    protected ZMQType bindType;
    private String connectionAddress = null;
    private Socket socket = null;

    public ZMQResponder(String url, int responderPort, ZMQType bindType) {
        this.url = url;
        this.responderPort = responderPort;
        this.connectionAddress = url + ":" + responderPort;

        this.bindType = bindType;

        this.context = new ZContext();
    }


    public Socket getResponderSocket(ZMQType bindType) throws ZMQConnectionFailedException {

        if (this.socket == null) {

            LoggerUtils.info("ZMQResponder : Attempting to connect to address : " + connectionAddress);

            this.context.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
            this.context.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);

            try {

                this.socket = this.context.createSocket(SocketType.REP);

                this.socket.setHeartbeatTimeout(ZMQConfiguration.HEARTBEAT_TIMEOUT);
                this.socket.setHandshakeIvl(ZMQConfiguration.HANDSHAKE_INTERVAL);
                this.socket.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
                this.socket.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);
                // TODO check if config OK for ZMQ responder
                this.socket.setReceiveTimeOut(ZMQConfiguration.RECEIVE_TIMEOUT);

                if (ZMQSecurity.isSecure()) {
                    this.socket.setCurveServer(true);
                    this.socket.setCurveSecretKey(ZMQSecurity.getServerPair().secretKey.getBytes());
                    this.socket.setCurvePublicKey(ZMQSecurity.getServerPair().publicKey.getBytes());
                }

                if (bindType == ZMQType.BIND) {
                    this.socket.bind(connectionAddress);
                    LoggerUtils.info("ZMQResponder : Bind succeeded to : " + connectionAddress);
                } else {
                    this.socket.connect(connectionAddress);
                    LoggerUtils.info("ZMQResponder : Connection succeeded to : " + connectionAddress);
                }

            } catch (Exception e) {

                LoggerUtils.logException(e);

                this.closeSocket();

                LoggerUtils.error("ZMQResponder : Failed to connect to address : " + connectionAddress + " : " + e.getMessage());

                try {
                    int wait_ms = 15000;

                    LoggerUtils.info("ZMQResponder : Socket closed. Will wait in milliseconds for : " + wait_ms);

                    Thread.sleep(wait_ms);
                } catch (InterruptedException ie) {
                    LoggerUtils.logException(ie);
                    Thread.currentThread().interrupt();
                }

                throw new ZMQConnectionFailedException(0, "Failed to connect to address [ " + connectionAddress + " ] : " + e.getMessage());
            }

        }

        return socket;
    }

    public Socket getResponderSocket() throws ZMQConnectionFailedException {

        return getResponderSocket(null);

    }

    public void closeSocket() {
        if (socket != null) {
            socket.close();
            context.destroySocket(socket);
            socket = null;
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

    public void setSocket(ZMQ.Socket subSocket) {
        this.socket = subSocket;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getResponderPort() {
        return responderPort;
    }

    public void setResponderPort(int responderPort) {
        this.responderPort = responderPort;
    }

}
