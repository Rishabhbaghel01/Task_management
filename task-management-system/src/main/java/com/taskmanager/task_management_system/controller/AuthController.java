package com.taskmanager.task_management_system.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.task_management_system.config.JwtUtil;
import com.taskmanager.task_management_system.model.User;
import com.taskmanager.task_management_system.repository.UserRepository;

@RestController
@RequestMapping("/api/auth") // Endpoint for authentication
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	// Constructor for initializing required fields
	public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
			JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtUtil = jwtUtil;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/login") // Login endpoint
	public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
		String username = credentials.get("username");
		String password = credentials.get("password");

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
		}

		String token = jwtUtil.generateToken(username);
		return ResponseEntity.ok(Map.of("token", token)); // Return generated token
	}

	@PostMapping("/register") // Register endpoint
	public ResponseEntity<?> register(@RequestBody Map<String, String> userDetails) {
		String username = userDetails.get("username");
		String password = userDetails.get("password");
		String email = userDetails.get("email");

		if (username == null || password == null || email == null) {
			return ResponseEntity.badRequest().body(Map.of("error", "All fields are required"));
		}

		if (userRepository.findByUsername(username).isPresent()) {
			return ResponseEntity.badRequest().body(Map.of("error", "Username is already taken"));
		}

		User newUser = new User();
		newUser.setUsername(username);
		newUser.setPassword(passwordEncoder.encode(password));
		newUser.setEmail(email);
		userRepository.save(newUser);

		return ResponseEntity.ok(Map.of("message", "User registered successfully"));
	}
}
