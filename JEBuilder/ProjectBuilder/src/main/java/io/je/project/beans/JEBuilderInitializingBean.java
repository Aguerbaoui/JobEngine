package io.je.project.beans;

import io.je.project.config.AuthenticationInterceptor;
import io.je.project.config.BuilderProperties;
import io.je.project.config.LicenseProperties;
import io.je.project.services.ConfigurationService;
import io.je.project.services.ProjectService;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.LicenseNotActiveException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;
import io.je.utilities.zmq.ZMQSecurity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            JELogger.debug(JEMessages.LOGGER_INITIALIZED,
                    LogCategory.DESIGN_MODE, null,
                    LogSubModule.JEBUILDER, null);
            AuthenticationInterceptor.init(builderProperties.getJwksUrl(), builderProperties.getIssuer());
            LicenseProperties.init();
        	while(!LicenseProperties.licenseIsActive())
        	{
        		try {
        			Thread.sleep(20000);
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
            
            JELogger.debug(JEMessages.BUILDER_STARTED,  LogCategory.DESIGN_MODE,
                    null, LogSubModule.JEBUILDER, null);
            configService.init();
        } catch (  Exception   e) {
            JELogger.error(JEMessages.UNEXPECTED_ERROR , LogCategory.DESIGN_MODE, null,
                    LogSubModule.JEBUILDER, null);
        }

    }
}