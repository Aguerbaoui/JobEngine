package io.je.Monitor.zmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JEMonitorSubscriber {


    @Autowired
    Subscriber subscriber;

    public void initSubscriber() {
        //"tcp://"+ SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(), SIOTHConfigUtility.getSiothConfig().getDataModelPORTS().getDmService_PubAddress(), topic
        //Subscriber subscriber = new Subscriber(/*"tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode()*/"tcp://192.168.4.128", 12345, "JEMonitorTopic");
        Thread thread = new Thread(subscriber);
        subscriber.setListening(true);
        thread.start();
    }

}
