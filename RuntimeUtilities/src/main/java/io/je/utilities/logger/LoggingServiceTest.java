package io.je.utilities.logger;

import java.time.LocalDateTime;



//TODO: to be deleted, just used for testing
public class LoggingServiceTest {

	public static void publish(String projectId, LogLevel logLevel, String logDate, LogCategory category,
			LogSubModule subModule, Object message) {
			//JEConfiguration.setLoggingSystemURL("tcp://localhost");
			//JEConfiguration.setLoggingSystemZmqPublishPort(15001);
			LogMessage msg = new LogMessage(logLevel, message, logDate, category, projectId,subModule,"ruleId123");
			ZMQLogPublisher.publish(msg);
		
	}

	public static void main(String[] args) {

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				Integer i=0;
				while (true) {
					
		   			System.out.println("***");

					LoggingServiceTest.publish((i++).toString(), LogLevel.INFORM, LocalDateTime.now().toString(), LogCategory.RUNTIME, LogSubModule.RULE,
							"Rule was added");
					System.out.println("Sent message");
					try {
						Thread.sleep(5000);
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