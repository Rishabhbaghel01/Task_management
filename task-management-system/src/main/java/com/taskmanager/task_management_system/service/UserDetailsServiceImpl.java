package com.taskmanager.task_management_system.service;

import com.taskmanager.task_management_system.model.User;
import com.taskmanager.task_management_system.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	// Constructor to inject the UserRepository and PasswordEncoder beans
	public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	// Method to load user details by username
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Searching for the user in the database using the username
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

		// Building and returning a UserDetails object using the found user's data
		return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
				.password(user.getPassword()).roles("USER").build();
	}
}
