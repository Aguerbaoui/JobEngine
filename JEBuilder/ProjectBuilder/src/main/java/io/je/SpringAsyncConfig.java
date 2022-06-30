package io.je;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import java.util.concurrent.Executor;

/*
* Spring async configurations
* */
@Configuration
@EnableAsync
public class SpringAsyncConfig implements AsyncConfigurer {

	@Override
	public Executor getAsyncExecutor() {
		JELogger.debug(JEMessages.SETTING_ASYNC_EXECUTOR,
				LogCategory.RUNTIME, null,
				LogSubModule.JERUNNER, null);
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(3);
		executor.setMaxPoolSize(20);
		executor.setQueueCapacity(1000);
		executor.setThreadNamePrefix("AsynchThread-");
		executor.initialize();
		return executor;
	}
}