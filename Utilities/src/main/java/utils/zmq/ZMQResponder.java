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

            this.bindType = bindType;

            LoggerUtils.info("ZMQ responder : Create socket for address : " + connectionAddress + ", type : " + this.bindType);

            this.context.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
            this.context.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);

            try {

                this.socket = this.context.createSocket(SocketType.REP);

                this.socket.setHeartbeatTimeout(ZMQConfiguration.HEARTBEAT_TIMEOUT);
                this.socket.setHandshakeIvl(ZMQConfiguration.HANDSHAKE_INTERVAL);
                this.socket.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
                this.socket.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);
                this.socket.setReceiveTimeOut(ZMQConfiguration.RECEIVE_TIMEOUT);
                this.socket.setSendTimeOut(ZMQConfiguration.SEND_TIMEOUT);

                if (ZMQSecurity.isSecure()) {
                    this.socket.setCurveServer(true);
                    this.socket.setCurveSecretKey(ZMQSecurity.getServerPair().secretKey.getBytes());
                    this.socket.setCurvePublicKey(ZMQSecurity.getServerPair().publicKey.getBytes());
                }

                if (bindType == ZMQType.BIND) {
                    this.socket.bind(connectionAddress);
                    LoggerUtils.info("ZMQ responder : Bind succeeded to : " + connectionAddress);
                } else {
                    this.socket.connect(connectionAddress);
                    LoggerUtils.info("ZMQ responder : Connection succeeded to : " + connectionAddress);
                }

            } catch (Exception e) {

                LoggerUtils.error("ZMQ responder : Failed to connect to address : " + connectionAddress + " : " + e.getMessage());

                LoggerUtils.logException(e);

                try {
                    this.closeSocket();

                    int wait_ms = 15000;

                    LoggerUtils.info("ZMQ responder : Socket closed. Will wait in milliseconds for : " + wait_ms);

                    Thread.sleep(wait_ms);
                } catch (InterruptedException ie) {
                    LoggerUtils.logException(ie);
                    Thread.currentThread().interrupt();
                }

                throw new ZMQConnectionFailedException(0, "Failed to connect to address : " + connectionAddress + " : " + e.getMessage());
            }

        }

        return socket;
    }

    public Socket getResponderSocket() throws ZMQConnectionFailedException {

        return getResponderSocket(ZMQType.BIND);

    }

    public void closeSocket() {
        if (socket != null) {

            if (bindType == ZMQType.BIND) {
                this.socket.unbind(connectionAddress);
                LoggerUtils.info("ZMQ responder : Unbind succeeded from : " + connectionAddress);
            } else {
                this.socket.disconnect(connectionAddress);
                LoggerUtils.info("ZMQ responder : Disconnection succeeded from : " + connectionAddress);
            }

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
