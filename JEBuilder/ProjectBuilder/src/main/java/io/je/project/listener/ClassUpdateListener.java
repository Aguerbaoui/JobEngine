package io.je.project.listener;

import java.util.Arrays;
import java.util.List;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.classbuilder.models.DataModelAction;
import io.je.classbuilder.models.ModelUpdate;
import io.je.project.services.ClassService;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.InstanceCreationFailed;
import io.je.utilities.logger.JELogger;
import io.je.utilities.zmq.ZMQSubscriber;

public class ClassUpdateListener extends ZMQSubscriber  {

	ClassService classService = new ClassService();
	
	
	static ObjectMapper objectMapper = new ObjectMapper();
	
	public ClassUpdateListener(String url, int subPort, String topic) {
		super(url, subPort, topic);
	}

	@Override
	public void run() {
		while(listening)
    	{
			
          //  JELogger.info(ClassUpdateListener.class, "--------------------------------------");

   		 String data = null;
   		 try {
   			data = this.getSubSocket().recvStr();
   		 }catch (Exception e) {
			e.printStackTrace();
			continue;
		}

             try {
            	 if( data !=null && !data.equals(topic))
				{
                     JELogger.info(ClassUpdateListener.class, JEMessages.DATA_RECEIVED + data);
                     List<ModelUpdate> updates = null;
                     try {
                    	 updates = Arrays.asList(objectMapper.readValue(data, ModelUpdate[].class));
             		} catch (JsonProcessingException e) {
             			
             			e.printStackTrace();
             			throw new InstanceCreationFailed("Failed to parse model update : " + e.getMessage());

             		}
                     
                    for(ModelUpdate update : updates )
                    {
                    	 if(update.getAction()==DataModelAction.UPDATE)
                         {
                        	 classService.addClass(update.getModel(), true);
                         }
                    }
                     
                     
				}
			} catch (Exception e) {
				e.getStackTrace();
				JELogger.error(ClassUpdateListener.class, Arrays.toString(e.getStackTrace()));
			}
             
             try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
                 JELogger.error(ClassUpdateListener.class,JEMessages.THREAD_INTERRUPTED );
			}
    	}
		
	}

}
