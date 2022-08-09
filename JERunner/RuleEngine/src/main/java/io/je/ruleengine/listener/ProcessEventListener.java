package io.je.ruleengine.listener;

import org.drools.core.event.DefaultProcessEventListener;
import org.kie.api.definition.process.Process;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessStartedEvent;

public class ProcessEventListener extends DefaultProcessEventListener {


    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        System.err.println("ProcessCompletedEvent getKieRuntime getKieBase getProcesses : ");

        for (Process process : event.getKieRuntime().getKieBase().getProcesses()) {
            System.err.println("process Id : " + process.getId());
            System.err.println("process Name : " + process.getName());
            System.err.println("process Package name: " + process.getPackageName());
            System.err.println("process type : " + process.getType());
            System.err.println("process version : " + process.getVersion());
        }
    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
        System.err.println("ProcessStartedEvent getKieRuntime getKieBase getProcesses : ");

        for (Process process : event.getKieRuntime().getKieBase().getProcesses()) {
            System.err.println("process Id : " + process.getId());
            System.err.println("process Name : " + process.getName());
            System.err.println("process Package name: " + process.getPackageName());
            System.err.println("process type : " + process.getType());
            System.err.println("process version : " + process.getVersion());
        }
    }

}
