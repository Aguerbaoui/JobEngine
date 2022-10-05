package utils.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;
import utils.log.LoggerUtils;


public class ZMQRequester {

    private ZContext context = null;
    private Socket socket = null;

    private String url;

    private int requesterPort;

    private String connectionAddress;

    //private int requestCounter = 0;

    public ZMQRequester(String url, int requesterPort) {
        this.url = url;
        this.requesterPort = requesterPort;
        this.connectionAddress = url + ":" + requesterPort;

        this.context = new ZContext();
    }

    public ZMQRequester(String url) {
        this.url = url;
        this.connectionAddress = url;
        this.context = new ZContext();
    }

    public Socket getRequesterSocket() throws ZMQConnectionFailedException {

        if (socket == null) {

            LoggerUtils.info("ZMQ requester : Attempting to connect to address : " + connectionAddress);

            context.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
            context.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);

            try {

                socket = context.createSocket(SocketType.REQ);

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

                socket.connect(connectionAddress);

                LoggerUtils.info("ZMQ requester : Connection succeeded to : " + connectionAddress);

            } catch (Exception e) {

                LoggerUtils.error("ZMQ requester : Failed to connect to address : " + connectionAddress + " : " + e.getMessage());

                LoggerUtils.logException(e);

                try {
                    this.closeSocket();

                    int wait_ms = 15000;

                    LoggerUtils.info("ZMQ requester : Socket closed. Will wait in milliseconds for : " + wait_ms);

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

    public void closeSocket() {
        if (socket != null) {

            socket.disconnect(connectionAddress);
            LoggerUtils.info("ZMQ responder : Disconnection succeeded from : " + connectionAddress);

            socket.close();
            context.destroySocket(socket);
            socket = null;
        }
    }

    public String sendRequest(String request) {
        String reply = "";

        try {

            //System.err.println("Requests number : " + this.requestCounter++);

            LoggerUtils.trace("ZMQ requester : sending request : " + request);

            synchronized (this.getRequesterSocket()) {

                this.getRequesterSocket().send(request, 0);

                reply = this.getRequesterSocket().recvStr(0);

            }

        } catch (Exception e) {
            LoggerUtils.logException(e);
        }

        return reply;

    }

    public ZContext getContext() {
        return context;
    }

    public void setContext(ZContext context) {
        this.context = context;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public int getRequesterPort() {
        return requesterPort;
    }

    public void setRequesterPort(int requesterPort) {
        this.requesterPort = requesterPort;
    }

}
