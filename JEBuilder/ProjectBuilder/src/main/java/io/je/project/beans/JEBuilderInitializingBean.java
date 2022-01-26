package io.je.project.beans;

//import io.je.project.config.AuthenticationInterceptor;
import io.je.project.config.AuthenticationInterceptor;
import io.je.project.config.BuilderProperties;
import io.je.project.config.LicenseProperties;
import io.je.project.services.ConfigurationService;
import io.je.project.services.ProjectService;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import io.je.utilities.monitoring.JEMonitor;

import io.siothconfig.SIOTHConfigUtility;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.zmq.ZMQSecurity;

@Component
public class JEBuilderInitializingBean implements InitializingBean {


	@Autowired
    @Lazy
    ProjectService projectService;
	
	@Autowired
    @Lazy
	ConfigurationService configService;
	
	@Autowired
	BuilderProperties builderProperties;

    @Override
    public void afterPropertiesSet() {
        try {
            ConfigurationConstants.initConstants(builderProperties.getSiothId(), builderProperties.isDev());
            SIOTHConfigUtility.setSiothId(builderProperties.getSiothId());
            JELogger.initLogger("JEBuilder", builderProperties.getJeBuilderLogPath(),builderProperties.getJeBuilderLogLevel());
            ConfigurationConstants.setJavaGenerationPath(SIOTHConfigUtility.getSiothConfig().getJobEngine().getGeneratedClassesPath());
            AuthenticationInterceptor.init(builderProperties.getIssuer());
            LicenseProperties.init();
           // JEMonitor.setPort(builderProperties.getMonitoringPort());
        	/*while(!LicenseProperties.licenseIsActive())
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
        	}*/
            JEMonitor.setPort(builderProperties.getMonitoringPort());
            ZMQSecurity.setSecure(builderProperties.getUseZmqSecurity());
			configService.init();
            JELogger.control(JEMessages.LOGGER_INITIALIZED,
                    LogCategory.DESIGN_MODE, null,
                    LogSubModule.JEBUILDER, null);
            JELogger.control(JEMessages.BUILDER_STARTED,  LogCategory.DESIGN_MODE,
                    null, LogSubModule.JEBUILDER, null);

        } catch (  Exception   e) {
            JELogger.error(JEMessages.UNEXPECTED_ERROR , LogCategory.DESIGN_MODE, null,
                    LogSubModule.JEBUILDER, null);
        }

    }
}