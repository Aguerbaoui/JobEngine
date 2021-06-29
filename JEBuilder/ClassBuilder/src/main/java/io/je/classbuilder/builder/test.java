package io.je.classbuilder.builder;

import io.je.classbuilder.models.GetModelObject;
import io.je.utilities.config.JEConfiguration;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.logger.JELogger;
import io.je.utilities.zmq.ZMQRequester;

public class test {

	public static void main(String[] args) throws InterruptedException {
		

	while (true)
		{
			String response=null;
			try {
				ZMQRequester requester = new ZMQRequester("tcp://192.168.7.202", 6638);
				String jsonMsg = "{\"type\":\"GETMODELBYID\",\"modelId\":\"b0f1fb73-2f7f-15e8-e233-7272e3107b85\",\"workspaceId\":\"74dc232f-4ef2-a17f-ea1f-25a3452bc801\"}";
	               response = requester.sendRequest(jsonMsg) ;
	              if (response == null) {
	            	  System.out.println( JEMessages.CLASS_NOT_FOUND);
	                  throw new ClassNotFoundException(JEMessages.CLASS_NOT_FOUND );
	              } else {
	                  System.out.println("Data Model defintion Returned : " + response);
	              }

			} catch (Exception e) {
				// TODO : replace with custom exception
				e.printStackTrace();
				System.out.println("Failed to send log message to the logging system : " + e.getMessage());
			}
			
			Thread.sleep(500);

		}
		

	/*	while(true)
		{
			new Thread(new Runnable(){      
				   @Override
				   public void run(){
						while (true)
						{
							String response=null;
							try {
								ZMQRequester requester = new ZMQRequester("tcp://192.168.7.202", 6638);

								String jsonMsg = "{\"type\":\"GETMODELBYID\",\"modelId\":\"b0f1fb73-2f7f-15e8-e233-7272e3107b85\",\"workspaceId\":\"74dc232f-4ef2-a17f-ea1f-25a3452bc801\"}";
					               response = requester.sendRequest(jsonMsg) ;
					              if (response == null) {
					            	  System.out.println( JEMessages.CLASS_NOT_FOUND);
					                  throw new ClassNotFoundException(JEMessages.CLASS_NOT_FOUND );
					              } else {
					                  System.out.println("Data Model defintion Returned : " + response);
					              }

							} catch (Exception e) {
								// TODO : replace with custom exception
								e.printStackTrace();
								System.out.println("Failed to send log message to the logging system : " + e.getMessage());
							}
							
							//Thread.sleep(500);

						}
				   }
				 }).start();
			
			Thread.sleep(2000);

		}
		
	*/
		}
	
}
