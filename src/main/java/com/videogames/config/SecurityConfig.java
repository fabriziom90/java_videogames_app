package com.videogames.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.videogames.services.CustomUserDetailService;

@Configuration
public class SecurityConfig {

	private final CustomUserDetailService customUserDetailsService;
	
	public SecurityConfig(CustomUserDetailService customUserDetailService) {
		this.customUserDetailsService = customUserDetailService;
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authManager(HttpSecurity http) throws Exception{
		AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		
		authBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
		
		return authBuilder.build();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/login", "/register", "/css/*").permitAll()
				.anyRequest().authenticated()
			)
			.formLogin(form -> form
				.loginPage("/login")
				.defaultSuccessUrl("/home", true)
				.permitAll()
			)
			.logout(logout -> logout
					.logoutSuccessUrl("/login?logout")
					.permitAll()
					
			);
		
		return http.build();
	}
}
