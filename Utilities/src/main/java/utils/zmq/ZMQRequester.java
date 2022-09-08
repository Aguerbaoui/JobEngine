package utils.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import utils.log.LoggerUtils;


public class ZMQRequester {

    private ZContext context = null;


    private String url;

    private int requestPort;

    private String connectionUrl;
    private ZMQ.Socket requestSocket = null;

    private int requestCounter = 0;

    public ZMQRequester(String url, int requestPort) {
        connectionUrl = url + ":" + requestPort;

        init();
    }

    public ZMQRequester(String url) {
        connectionUrl = url;

        init();
    }

    public void init() {

        this.context = new ZContext();

        context.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
        context.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);

        requestSocket = context.createSocket(SocketType.REQ);

        requestSocket.setHeartbeatTimeout(ZMQConfiguration.HEARTBEAT_TIMEOUT);
        requestSocket.setHandshakeIvl(ZMQConfiguration.HANDSHAKE_INTERVAL);
        requestSocket.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
        requestSocket.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);
        requestSocket.setReceiveTimeOut(-1);

        if (ZMQSecurity.isSecure()) {
            // Client specify server key
            requestSocket.setCurveServerKey(ZMQSecurity.getServerPair().publicKey.getBytes());

            requestSocket.setCurveSecretKey(ZMQSecurity.getServerPair().secretKey.getBytes());
            requestSocket.setCurvePublicKey(ZMQSecurity.getServerPair().publicKey.getBytes());
        }

        requestSocket.connect(connectionUrl);
    }

    public String sendRequest(String request) {
        String reply = "";
        synchronized (context) {
            try {

                //System.err.println("Request : " + request);
                //System.err.println("Requests number : " + this.requestCounter++);

                requestSocket.send(request, 0);
                reply = requestSocket.recvStr(0);

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


    public int getRequestPort() {
        return requestPort;
    }

    public void setRequestPort(int requestPort) {
        this.requestPort = requestPort;
    }

}
