package com.videogames.controllers;

import org.springframework.security.authentication.AuthenticationManager;
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

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final CustomUserDetailService userDetailsService;
	private final AuthenticationManager authenticationManager;
	
	public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, CustomUserDetailService userDetailsService, AuthenticationManager authenticationManager) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userDetailsService = userDetailsService;
		this.authenticationManager = authenticationManager;
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
	public String register(@ModelAttribute User user, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if(userRepository.existsByUsername(user.getUsername())) {
			redirectAttributes.addFlashAttribute("error", "Username già esistente!");
			return "redirect:/register";
		}
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole("ROLE_USER");
		userRepository.save(user);
		
		UserDetails userDetails =
		        userDetailsService.loadUserByUsername(user.getUsername());

		Authentication authentication =
		        new UsernamePasswordAuthenticationToken(
		                userDetails,
		                null,
		                userDetails.getAuthorities()
		        );

		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		request.getSession(true)
        .setAttribute(
                "SPRING_SECURITY_CONTEXT",
                SecurityContextHolder.getContext()
        );
		
		redirectAttributes.addFlashAttribute("success", "Registrazione completata!");
		return "redirect:/home";
	}
	
	@GetMapping("/home")
	public String home(Authentication authentication) {
		
		System.out.println(authentication);
		return "home/home";
	}
}
