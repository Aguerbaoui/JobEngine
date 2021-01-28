package io.je.project.services;

import org.springframework.stereotype.Service;

import io.je.project.models.JEBuilderConfigModel;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.JEGlobalconfig;

@Service
public class ConfigurationService {

	public void updateConfig(JEBuilderConfigModel configModel) {
		if(configModel.getDataDefinitionIpAddress() != null && configModel.getDataDefinitionPort() !=null)
		{
			JEGlobalconfig.CLASS_DEFINITION_API = configModel.getDataDefinitionIpAddress() + ":" +configModel.getDataDefinitionPort();
		}
		if(configModel.getJeRunnerIpAddress()!=null && configModel.getJeRunnerPort()!=null)
		{
			JEGlobalconfig.RUNTIME_MANAGER_BASE_API = configModel.getJeRunnerIpAddress()+":"+configModel.getJeRunnerPort();
		}
		
		
	}

}
