package utils.zmq;


import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public abstract class ZMQSubscriber implements Runnable {

    protected ZContext context = null;

    protected ZMQ.Socket subSocket = null;

    protected String url;

    protected int subPort;

    protected String topic;

    //protected int subscribers = 0;

    protected boolean listening = false;

    public ZMQSubscriber(String url, int subPort, String topic) {
        this.url = url;
        this.subPort = subPort;
        this.topic = topic;
        this.context = new ZContext();
       // subscribers += 1;
    }

    
    public ZMQSubscriber() {
		super();
		this.context = new ZContext();
	}


	public void closeSocket()
    {
    	if(this.subSocket!=null)
    	{
    		this.subSocket.close();
    		this.context.destroySocket(subSocket);
    		this.subSocket=null;
    	}
    }
    
    
    
    public ZMQ.Socket getSubSocket() {
        if(subSocket == null) {
            try {
                this.subSocket = this.context.createSocket(SocketType.SUB);
                this.subSocket.setReceiveTimeOut(1000);
                this.subSocket.connect(url+":"+subPort);
                this.subSocket.subscribe(topic.getBytes());
               if(ZMQSecurity.isSecure())
               {
            	   subSocket.setCurveServerKey(ZMQSecurity.getServerPair().publicKey.getBytes());
                   subSocket.setCurveSecretKey(ZMQSecurity.getServerPair().secretKey.getBytes());
           		   subSocket.setCurvePublicKey(ZMQSecurity.getServerPair().publicKey.getBytes());
               }

            } catch (Exception e) {
            	System.out.println( e.getMessage());
                this.subSocket = null;
            }
        }
        return subSocket;
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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
    
}
