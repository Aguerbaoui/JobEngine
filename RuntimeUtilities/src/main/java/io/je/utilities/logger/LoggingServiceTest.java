package io.je.utilities.logger;

import java.time.LocalDateTime;
import java.util.Date;

import io.je.utilities.zmq.ZMQSubscriber;


//TODO: to be deleted, just used for testing
public class LoggingServiceTest {

	public static void publish(String projectId, LogLevel logLevel, String logDate, LogCategory category,
			LogSubModules subModule, Object message) {
			LogMessageFormat msg = new LogMessageFormat(logLevel, message, logDate, category, projectId,subModule);
			ZMQLogPublisher.publish(msg);
		
	}

	public static void main(String[] args) {

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				while (true) {
		   			System.out.println("***");

					LoggingServiceTest.publish("123", LogLevel.Inform, LocalDateTime.now().toString(), LogCategory.Runtime, LogSubModules.Rule,
							"Rule was added");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
		};

		Thread t = new Thread(runnable);
		t.start();
		
	/*	ZMQSubscriber sub = new ZMQSubscriber("tcp://127.0.0.1", 15001, "SIOTH##LogTopic") {
			
			@Override
			public void run() {
				while (true)
				{
		   			System.out.println("---");

					 String data = null;
			   		 try {
			   			data = this.getSubSocket().recvStr();
			   			System.out.println("data:"+data);
			   		 }catch (Exception e) {
						e.printStackTrace();
						continue;
					}
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		};
		
		new Thread(sub).start();
*/
	}

}
