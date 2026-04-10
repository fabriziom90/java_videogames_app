package com.videogames.controllers;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.videogames.interfaces.UserRepository;
import com.videogames.models.User;
import com.videogames.services.CustomUserDetailService;

@Controller
public class AuthController {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final CustomUserDetailService userDetailsService;
	
	public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, CustomUserDetailService userDetailsService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userDetailsService = userDetailsService;
	}
	
	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}
	
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("user", new User());
		return "auth/register";
	}
	
	@PostMapping("/register")
	public String register(@ModelAttribute User user, Model model, RedirectAttributes redirectAttributes) {
		if(userRepository.existsByUsername(user.getUsername())) {
			redirectAttributes.addFlashAttribute("error", "Username già esistente!");
			return "redirect:/register";
		}
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole("ROLE_USER");
		userRepository.save(user);
		
		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		redirectAttributes.addFlashAttribute("success", "Registrazione completata!");
		return "redirect:/home";
	}
	
	@GetMapping("/home")
	public String home() {
		return "home/home";
	}
}
