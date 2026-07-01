package com.taskmanager.task_management_system.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.taskmanager.task_management_system.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtTokenUtil;

	@Autowired
	public JwtAuthenticationFilter(JwtUtil jwtTokenUtil) {
		this.jwtTokenUtil = jwtTokenUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Extracting the token from the Authorization header
		String authorizationHeader = request.getHeader("Authorization");

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7); // Removing "Bearer " to get the token itself

			try {
				// Validating the token using JwtUtil
				String username = jwtTokenUtil.extractUsername(jwtToken);

				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					// If the token is valid, you can set the security context directly
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							username, null, null); // No need for UserDetails
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			} catch (Exception ex) {
				// Handling errors (such as invalid token)
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("Unauthorized: Invalid or expired token");
				return;
			}
		}

		// Passing the request to the next filters
		filterChain.doFilter(request, response);
	}
}
