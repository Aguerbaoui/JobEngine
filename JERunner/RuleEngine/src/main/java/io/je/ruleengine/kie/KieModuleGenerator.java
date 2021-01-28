package io.je.ruleengine.kie;

import org.kie.api.KieServices;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;

public class KieModuleGenerator {
    KieServices ks = KieServices.Factory.get();

    public void generateKieModule(EqualityBehaviorOption equalityBehaviorOption, EventProcessingOption eventProcessingOption, KieSessionType kieSessionType, ClockTypeOption clockTypeOption) {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("kie-base").setDefault(true)
                .setEqualsBehavior(equalityBehaviorOption)
                .setEventProcessingMode(eventProcessingOption);

        kieBaseModel1.newKieSessionModel("kie-session").setDefault(true)

                .setType(kieSessionType).setClockType(clockTypeOption);
        //kfs.writeKModuleXML(kproj.toXML());
    }

}
