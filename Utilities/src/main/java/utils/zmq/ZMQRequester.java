package utils.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;


public class ZMQRequester {

    private ZContext context = null;


    private String url;

    private int requestPort;

    private String connectionUrl;
    private ZMQ.Socket requestSocket = null;

    public ZMQRequester(String url, int requestPort) {
        connectionUrl = url + ":" + requestPort;

        this.context = new ZContext();

    }

    public ZMQRequester(String url) {
        connectionUrl = url;
        this.context = new ZContext();
     
    }


    public String sendRequest(String request, int timeout) {
        String reply = "";
        try {
            ZMQ.Socket requestSocket = context.createSocket(SocketType.REQ);
            if (ZMQSecurity.isSecure()) {
                requestSocket.setCurveServerKey(ZMQSecurity.getServerPair().publicKey.getBytes());
                requestSocket.setCurveSecretKey(ZMQSecurity.getServerPair().secretKey.getBytes());
                requestSocket.setCurvePublicKey(ZMQSecurity.getServerPair().publicKey.getBytes());
            }
            requestSocket.setReceiveTimeOut(timeout);

            requestSocket.connect(connectionUrl);
            requestSocket.send(request, 0);
            reply = requestSocket.recvStr(0);

            requestSocket.close();
            context.destroySocket(requestSocket);
            // requestSocket = null;

            return reply;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reply;
    }


    public String sendRequest(String request) {
        String reply = "";
        synchronized (context) {
            try {
                requestSocket = context.createSocket(SocketType.REQ);

                if (ZMQSecurity.isSecure()) {

                    requestSocket.setCurveServerKey(ZMQSecurity.getServerPair().publicKey.getBytes());
                    requestSocket.setCurveSecretKey(ZMQSecurity.getServerPair().secretKey.getBytes());
                    requestSocket.setCurvePublicKey(ZMQSecurity.getServerPair().publicKey.getBytes());
                }
                requestSocket.setRcvHWM(ZMQConfiguration.RECEIVE_HIGH_WATERMARK);
                requestSocket.setSndHWM(ZMQConfiguration.SEND_HIGH_WATERMARK);
                requestSocket.setReceiveTimeOut(-1);

                requestSocket.connect(connectionUrl);
                requestSocket.send(request, 0);
                reply = requestSocket.recvStr(0);

                closeSocket();
                // requestSocket = null;

                return reply;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSocket();
            }
            return reply;
        }

    }

    public void closeSocket() {
        if (requestSocket != null) {
            this.requestSocket.close();
            this.context.destroySocket(requestSocket);
            this.requestSocket = null;
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
