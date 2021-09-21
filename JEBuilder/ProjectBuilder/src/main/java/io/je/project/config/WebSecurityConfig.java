/*package io.je.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;



@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private String authServer="http://192.168.0.169:5000";
	
	
	@Override
	 public void configure(final HttpSecurity http) throws Exception { 
		 http
		 	.csrf().disable()
		 	.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
		 	.authorizeRequests()
		 	//.antMatchers(HttpMethod.GET, "/").hasAuthority("SCOPE_eng:api")

		 	
		 	.and()
		 	.oauth2ResourceServer()
		 	.jwt();
	 }
	
	@Bean
	 JwtDecoder jwtDecoder() {
		NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder)
	    		 JwtDecoders.fromOidcIssuerLocation(authServer);
	     
	     OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator("JobEngineAPI");
	     OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(authServer);
	     OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

	     jwtDecoder.setJwtValidator(withAudience);

	     return jwtDecoder;
	 }
	
}
*/

