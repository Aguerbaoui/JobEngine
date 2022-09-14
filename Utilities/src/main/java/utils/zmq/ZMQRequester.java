package utils.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;
import utils.log.LoggerUtils;


public class ZMQRequester {

    private ZContext context = null;

    private String url;

    private int requesterPort;

    private String connectionAddress;
    private Socket socket = null;

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

            LoggerUtils.info("ZMQRequester : Attempting to connect to address : " + connectionAddress);

            context.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
            context.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);

            try {

                socket = context.createSocket(SocketType.REQ);

                socket.setHeartbeatTimeout(ZMQConfiguration.HEARTBEAT_TIMEOUT);
                socket.setHandshakeIvl(ZMQConfiguration.HANDSHAKE_INTERVAL);
                socket.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
                socket.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);
                socket.setReceiveTimeOut(ZMQConfiguration.RECEIVE_TIMEOUT);

                if (ZMQSecurity.isSecure()) {
                    // Client specify server key
                    socket.setCurveServerKey(ZMQSecurity.getServerPair().publicKey.getBytes());

                    socket.setCurveSecretKey(ZMQSecurity.getServerPair().secretKey.getBytes());
                    socket.setCurvePublicKey(ZMQSecurity.getServerPair().publicKey.getBytes());
                }

                socket.connect(connectionAddress);

                LoggerUtils.info("ZMQRequester : Connection succeeded to : " + connectionAddress);

            } catch (Exception e) {

                LoggerUtils.logException(e);

                this.closeSocket();

                LoggerUtils.error("ZMQRequester : Failed to connect to address : " + connectionAddress + " : " + e.getMessage());

                try {
                    int wait_ms = 15000;

                    LoggerUtils.info("ZMQRequester : Socket closed. Will wait in milliseconds for : " + wait_ms);

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
            socket.close();
            context.destroySocket(socket);
            socket = null;
        }
    }

    public String sendRequest(String request) {
        String reply = "";
        synchronized (context) {
            try {

                //System.err.println("Requests number : " + this.requestCounter++);

                LoggerUtils.trace("ZMQRequester : sending request : " + request);

                this.getRequesterSocket().send(request, 0);

                reply = this.getRequesterSocket().recvStr(0);

            } catch (Exception e) {
                LoggerUtils.logException(e);
            }
            return reply;
        }
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
