package io.je.runtime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import utils.log.LoggerUtils;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    HttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedDoubleSlash(true);
        return firewall;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // WARNING authentication / CORS disabled as RuntimeManager not routed in Ocelot
        // Should add authentication if RuntimeManager added to Gateway ...
        http
                .authorizeHttpRequests((authz) -> {
                            try {
                                authz
                                        .anyRequest()
                                        //.authenticated()
                                        .permitAll()
                                        .and()
                                        .csrf()
                                        .disable();
                            } catch (Exception e) {
                                LoggerUtils.logException(e);
                                throw new RuntimeException(e);
                            }
                        }
                )
                .httpBasic(withDefaults());
        return http.build();
    }

}
