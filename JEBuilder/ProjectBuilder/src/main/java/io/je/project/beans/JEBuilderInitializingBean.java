package io.je.project.beans;

//import io.je.project.config.AuthenticationInterceptor;
import io.je.project.config.BuilderProperties;
import io.je.project.config.LicenseProperties;
import io.je.project.services.ConfigurationService;
import io.je.project.services.ProjectService;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.LicenseNotActiveException;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.date.DateUtils;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.zmq.ZMQSecurity;

@Component
public class JEBuilderInitializingBean implements InitializingBean {


	@Autowired
    ProjectService projectService;
	
	@Autowired 
	ConfigurationService configService;
	
	@Autowired
	BuilderProperties builderProperties;
    @Override
    public void afterPropertiesSet() {
        try {
            JELogger.initLogger("JEBuilder", builderProperties.getJeBuilderLogPath(),builderProperties.getJeBuilderLogLevel());
            JELogger.control(JEMessages.LOGGER_INITIALIZED,
                    LogCategory.DESIGN_MODE, null,
                    LogSubModule.JEBUILDER, null);
           // AuthenticationInterceptor.init(builderProperties.getIssuer());
            LicenseProperties.init();
        	while(!LicenseProperties.licenseIsActive())
        	{
        		try {
        			Thread.sleep(5000);
    				LicenseProperties.checkLicenseIsActive();    				
    			} catch (LicenseNotActiveException e) {
    				JELogger.error(e.getMessage(), LogCategory.SIOTH_APPLICATION, "",
    						LogSubModule.JEBUILDER, "");
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
        	}
        	
            
            
            ZMQSecurity.setSecure(builderProperties.getUseZmqSecurity());
			configService.init();
            JELogger.control(JEMessages.BUILDER_STARTED,  LogCategory.DESIGN_MODE,
                    null, LogSubModule.JEBUILDER, null);

        } catch (  Exception   e) {
            JELogger.error(JEMessages.UNEXPECTED_ERROR , LogCategory.DESIGN_MODE, null,
                    LogSubModule.JEBUILDER, null);
        }

    }
}