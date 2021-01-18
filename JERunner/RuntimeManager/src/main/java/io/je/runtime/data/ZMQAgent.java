package io.je.runtime.data;

import io.je.ruleengine.impl.RuleEngine;
import io.je.runtime.ruleenginehandler.RuleEngineHandler;
import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.beans.JEData;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.exceptions.InstanceCreationFailed;
import io.je.utilities.logger.JELogger;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ZMQAgent implements Runnable {

    private ZContext context = null;

    private ZMQ.Socket subSocket = null;

    private ZMQ.Socket requestSocket = null;

    private String url;

    private int subPort;

    private int requestPort;

    private String topic;

    private boolean listening = true;

    public ZMQAgent(String url,int subPort, int requestPort, String topic) {
        this.url = url;
        this.subPort = subPort;
        this.topic = topic;
        this.requestPort = requestPort;
        this.context = new ZContext();
    }

    public ZMQ.Socket getSubSocket() {
        if(subSocket == null) {
            try {
                this.subSocket = this.context.createSocket(SocketType.SUB);
                this.subSocket.setReceiveTimeOut(APIConstants.SUBSCRIBER_TIMEOUT);
                this.subSocket.connect(url+":"+subPort);
                this.subSocket.subscribe(topic.getBytes());

            } catch (Exception e) {
                JELogger.error(ZMQAgent.class, e.getMessage());
                this.subSocket = null;
            }
        }
        return subSocket;
    }

    /*public static void main(String[] args) {
        ZMQAgent agent = new ZMQAgent("tcp://192.168.0.128", 5554, 6638, "");
        while(true) {
            String log = agent.getSubSocket().recvStr();
            JELogger.info(ZMQAgent.class, log);
        }

    }*/
    public String sendRequest(String request) {
        String reply = "";
        try {
            requestSocket = context.createSocket(SocketType.REQ);
            requestSocket.setReceiveTimeOut(APIConstants.REQUESTER_TIMEOUT);
            requestSocket.connect(url+":"+requestPort);
            requestSocket.send(request, 0);
            reply = requestSocket.recvStr(0);
            context.close();
            requestSocket.close();
            requestSocket = null;
            return reply;
        } catch (Exception e) {
            JELogger.error(ZMQAgent.class, e.getMessage());
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

    public void setSubSocket(ZMQ.Socket subSocket) {
        this.subSocket = subSocket;
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

    public int getSubPort() {
        return subPort;
    }

    public void setSubPort(int subPort) {
        this.subPort = subPort;
    }

    public int getRequestPort() {
        return requestPort;
    }

    public void setRequestPort(int requestPort) {
        this.requestPort = requestPort;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
    

	@Override
	public void run() {
		while(listening)
    	{
    		 String data = this.getSubSocket().recvStr();
             JELogger.info(ZMQAgent.class, "read data " + data);
             try {
				RuntimeDispatcher.injectData(new JEData(this.topic, data));
			} catch (Exception e) {
				//e.printStackTrace();
			}
    	}
		
	}

  /*  public void prepareListener() {
        while(true) {
            if(isListening()) {
                String data = this.getSubSocket().recvStr();
                JELogger.info(ZMQAgent.class, data);
                RuntimeDispatcher.injectData(new JEData(this.topic, data));

                //DISPATCHER calls to rule engine handler / workflow handler
            }
        }
    } */
}
