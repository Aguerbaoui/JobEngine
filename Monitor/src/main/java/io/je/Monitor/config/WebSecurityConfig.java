package io.je.Monitor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import utils.log.LoggerUtils;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /* extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .cors()
                .and()
                .headers()
                .frameOptions().disable()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/ws/**").permitAll()
                .anyRequest()
                .authenticated();
        // @formatter:on
    }
*/

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> {
                            try {
                                authz
                                        //.authorizeRequests()
                                        .antMatchers("/ws/**").permitAll()
                                        .anyRequest().authenticated()
                                        .and()
                                        .cors()
                                        .and()
                                        .headers()
                                        .frameOptions().disable()
                                        .and()
                                        .csrf().disable();

                            } catch (Exception e) {
                                LoggerUtils.logException(e);
                                throw new RuntimeException(e);
                            }
                        }
                )
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    HttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedDoubleSlash(true);
        return firewall;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
