package io.je.project.services;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class ProjectServicesTestConfiguration {
    @Bean
    @Primary
    public ProjectService productService() {
        return Mockito.mock(ProjectService.class);
    }
}
