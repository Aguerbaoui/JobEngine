package io.je;

import java.util.concurrent.Executor;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.logger.JELogger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class SpringAsyncConfig implements AsyncConfigurer {

	@Override
	public Executor getAsyncExecutor() {
		JELogger.trace(JEMessages.SETTING_ASYNC_EXECUTOR);
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(3);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("AsynchThread-");
		executor.initialize();
		return executor;
	}
}