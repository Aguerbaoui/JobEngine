package io.je.utilities.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.utilities.monitoring.JEMonitor;
import io.je.utilities.monitoring.MonitoringMessage;

public class PulisherTest {
    public static void publish(MonitoringMessage msg) {
        //JEConfiguration.setLoggingSystemURL("tcp://localhost");
        //JEConfiguration.setLoggingSystemZmqPublishPort(15001);
        JEMonitor.publish(msg);

    }
    static ObjectMapper objectMapper = new ObjectMapper();
    public static void main(String[] args) {
       /* ZMQ.Context  mContext  = ZMQ.context(1);
        ZMQ.Socket publisher = mContext.socket(ZMQ.PUB);
        publisher.bind("tcp://*:15020");
        /*int theCounter = 0 ;
        while (!Thread.currentThread ().isInterrupted ()) {
            try {
                theCounter++;
                //  Send message to all subscribers
                String update = String.format("MyTopic %d ", theCounter);

                publisher.send(update, 0);

                Thread.sleep(100);
            }
            // context.term ();
            catch (InterruptedException ex) {
            }
        }*/
/*
        Runnable runnable = () -> {
            int i=0;
            while (true) {
                try {
                System.out.println("***");
                MonitoringMessage msg = new MonitoringMessage(LocalDateTime.now(), "objId" + i, ObjectType.JEWORKFLOW, "prTest", "Running", ArchiveOption.NONE, false);
                String jsonMsg = objectMapper.writeValueAsString(msg);
                //publisher.sendMore("JEMonitor");
                publisher.send("JEMonitor: " + jsonMsg);
                System.out.println("Sent message");
                Thread.sleep(2000);
                i++;
                } catch (InterruptedException | JsonProcessingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        };

        Thread t = new Thread(runnable);
        t.start();*/
    }

}
