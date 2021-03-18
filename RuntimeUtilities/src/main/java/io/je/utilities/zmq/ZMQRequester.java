package io.je.utilities.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ZMQRequester {

    private ZContext context = null;


    private ZMQ.Socket requestSocket = null;

    private String url;

    private int requestPort;

    private boolean listening = true;

    public ZMQRequester(String url, int requestPort) {
        this.url = url;
        this.requestPort = requestPort;
        this.context = new ZContext();
    }

    
    public String sendRequest(String request) {
        String reply = "";
        try {
            requestSocket = context.createSocket(SocketType.REQ);
            requestSocket.setReceiveTimeOut(1000);
            String cnxUrl = url+":"+requestPort;
            requestSocket.connect(cnxUrl);
            requestSocket.send(request, 0);
            reply = requestSocket.recvStr(0);
            context.close();
            requestSocket.close();
            requestSocket = null;
            return reply;
        } catch (Exception e) {
        	System.out.println( e.getMessage());
        }
        return reply;
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



    public ZMQ.Socket getRequestSocket() {
        return requestSocket;
    }

    public void setRequestSocket(ZMQ.Socket requestSocket) {
        this.requestSocket = requestSocket;
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
