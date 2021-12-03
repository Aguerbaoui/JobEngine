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
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.zmq.ZMQSubscriber;


public class ClassUpdateListener extends ZMQSubscriber {

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
					JELogger.debug(JEMessages.DATA_RECEIVED + data,  LogCategory.RUNTIME,
							null, LogSubModule.CLASS, null);
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
                        	 classService.addClass(update.getModel(), true,true);
                         }
						if(update.getAction()==DataModelAction.DELETE)
						{
							classService.removeClass(update.getModel().getName());
						}
                    }
                     
                     
				}
			} catch (Exception e) {
				 JELogger.error(JEMessages.ERROR_GETTING_CLASS_UPDATES, LogCategory.DESIGN_MODE, null,
						 LogSubModule.CLASS, null);
			}
             
             try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				 JELogger.error(JEMessages.THREAD_INTERRUPTED, LogCategory.DESIGN_MODE, null,
						 LogSubModule.CLASS, null);
			}
    	}
		
	}

}
