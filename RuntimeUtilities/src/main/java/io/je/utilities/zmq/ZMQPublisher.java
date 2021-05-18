package io.je.utilities.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

public class ZMQPublisher {

	protected  ZContext context;
	protected Socket socket;
	protected String url;
	protected int publishPort;
	private String connectionUrl;

	public ZMQPublisher(String url,int publishPort) {
		
		this.url = url;
		this.publishPort=publishPort;
		this.connectionUrl = url + ":" + publishPort;
		context = new ZContext();
		socket = context.createSocket(SocketType.PUB);
		socket.connect(connectionUrl);
	}

	public void publish(String msgToBePublished,String topic) {
		
		//TODO sockets aren't thread safe, only the context is
		synchronized(socket)
		{
			socket.sendMore(topic);
			socket.send( msgToBePublished,0);
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
