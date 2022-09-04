package utils.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

public class ZMQPublisher {

    protected ZContext context;
    protected Socket socket;
    protected String url;
    protected int publishPort;
    private String connectionUrl;

    public ZMQPublisher(String url, int publishPort) {

        this.url = url;
        this.publishPort = publishPort;
        this.connectionUrl = url + ":" + publishPort;

        context = new ZContext();
        context.setRcvHWM(0);
        context.setSndHWM(0);

        socket = context.createSocket(SocketType.PUB);

        socket.setHeartbeatTimeout(ZMQConfiguration.HEARTBEAT_TIMEOUT);
        socket.setHandshakeIvl(ZMQConfiguration.HANDSHAKE_INTERVAL);
        socket.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
        socket.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);

        if (ZMQSecurity.isSecure()) {
            socket.setCurveServer(true);
            socket.setCurveSecretKey(ZMQSecurity.getServerPair().secretKey.getBytes());
            socket.setCurvePublicKey(ZMQSecurity.getServerPair().publicKey.getBytes());
        }

        socket.connect(connectionUrl);
    }

    public void publish(String msgToBePublished, String topic) {

        synchronized (socket) {
            socket.sendMore(topic);
            socket.send(msgToBePublished, 0);
        }
    }

    public void open() {
        context = new ZContext();
        socket = context.createSocket(SocketType.PUB);
        socket.connect(connectionUrl);
    }

    public void close() {
        socket.close();
        context.close();
    }
}
