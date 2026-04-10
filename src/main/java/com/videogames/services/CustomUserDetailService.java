package com.videogames.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.videogames.interfaces.UserRepository;
import com.videogames.models.User;

@Service
public class CustomUserDetailService implements UserDetailsService {
	
	private final UserRepository userRepository;
	
	public CustomUserDetailService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));
		
		return org.springframework.security.core.userdetails.User
				.withUsername(user.getUsername())
				.password(user.getPassword())
				.roles(user.getRole().replace("ROLE_", "")) //spring aggiunge da solo la dicitura ROLE_. Se non lo rimovessi diventerbbe in sessione ROLE_ROLE_RUOLO
				.build();
	}

}
