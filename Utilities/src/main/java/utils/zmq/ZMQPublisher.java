package utils.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;
import utils.log.LoggerUtils;

public class ZMQPublisher {

    protected ZContext context;
    protected Socket socket;
    protected String url;
    protected int publisherPort;
    private String connectionAddress;


    public ZMQPublisher(String url, int publisherPort) {

        this.url = url;
        this.publisherPort = publisherPort;
        this.connectionAddress = url + ":" + publisherPort;

    }

    protected Socket getPublisherSocket() {

        if (socket == null) {

            LoggerUtils.info("ZMQ publisher : Attempting to connect to address : " + connectionAddress);

            try {

                context = new ZContext();
                context.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
                context.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);

                socket = context.createSocket(SocketType.PUB);

                socket.setHeartbeatTimeout(ZMQConfiguration.HEARTBEAT_TIMEOUT);
                socket.setHandshakeIvl(ZMQConfiguration.HANDSHAKE_INTERVAL);
                socket.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
                socket.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);
                socket.setReceiveTimeOut(ZMQConfiguration.RECEIVE_TIMEOUT);
                socket.setSendTimeOut(ZMQConfiguration.SEND_TIMEOUT);

                if (ZMQSecurity.isSecure()) {
                    socket.setCurveServer(true);
                    socket.setCurveSecretKey(ZMQSecurity.getServerPair().secretKey.getBytes());
                    socket.setCurvePublicKey(ZMQSecurity.getServerPair().publicKey.getBytes());
                }

                socket.connect(connectionAddress);

                LoggerUtils.info("ZMQ publisher : Connection succeeded to : " + connectionAddress);

            } catch (Exception e) {

                LoggerUtils.logException(e);

                this.closeSocket();

                LoggerUtils.error("ZMQ publisher : Failed to connect to address : " + connectionAddress + " : " + e.getMessage());

                try {
                    int wait_ms = 15000;

                    LoggerUtils.info("ZMQ publisher : Socket closed. Will wait in milliseconds for : " + wait_ms);

                    Thread.sleep(wait_ms);
                } catch (InterruptedException ie) {
                    LoggerUtils.logException(ie);
                    Thread.currentThread().interrupt();
                }

                // FIXME throw new ZMQConnectionFailedException(0, "Failed to connect to address : " + connectionUrl + " : " + e.getMessage());
            }

        }

        return socket;
    }

    public void publish(String msgToBePublished, String topic) {
        synchronized (getPublisherSocket()) {
            getPublisherSocket().sendMore(topic);
            getPublisherSocket().send(msgToBePublished, 0);
        }
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

    public String getConnectionAddress() {
        return connectionAddress;
    }

}
