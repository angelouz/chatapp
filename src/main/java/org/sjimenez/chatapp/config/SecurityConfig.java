package org.sjimenez.chatapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Profile(value = { "development", "production" })
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomAuthenticationHandler customAuthenticationHandler;

	@Autowired
	private CustomAuthenticationProvider customAuthenticationProvider;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(customAuthenticationProvider);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/static/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin().loginPage("/").failureHandler(customAuthenticationHandler);
		http.csrf().disable();
		http.authorizeRequests().antMatchers("/", "/login", "/user/resources/insert").permitAll().anyRequest()
				.authenticated();
		http.logout().logoutSuccessUrl("/");
	}

}
