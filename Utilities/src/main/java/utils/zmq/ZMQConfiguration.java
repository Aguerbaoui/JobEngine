package utils.zmq;

public class ZMQConfiguration {
    public static int HEARTBEAT_TIMEOUT = 60000;
    public static int HANDSHAKE_INTERVAL = 60000;
    public static int RECEIVE_TIMEOUT = 30000;
    public static int RECEIVE_HIGH_WATERMARK = 0; // 0 for unlimited messages
    public static int SEND_HIGH_WATERMARK = 0; // 0 for unlimited messages

    public static void setHeartbeatTimeout(int heartbeatTimeout) {
        HEARTBEAT_TIMEOUT = heartbeatTimeout;
    }

    public static void setHandshakeInterval(int handshakeInterval) {
        HANDSHAKE_INTERVAL = handshakeInterval;
    }

    public static void setReceiveTimeout(int receiveTimeout) {
        RECEIVE_TIMEOUT = receiveTimeout;
    }

    public static void setReceiveHighWatermark(int receiveHighWatermark) {
        RECEIVE_HIGH_WATERMARK = receiveHighWatermark;
    }

    public static void setSendHighWatermark(int sendHighWatermark) {
        SEND_HIGH_WATERMARK = sendHighWatermark;
    }
}
