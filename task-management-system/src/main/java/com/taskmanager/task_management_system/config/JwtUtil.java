package com.taskmanager.task_management_system.config;

import java.security.Key;
import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private static final String SECRET_KEY = "a-very-long-secret-key-with-at-least-32-characters";
	private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 Hour

	private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

	public String generateToken(String username) {
		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(key, SignatureAlgorithm.HS256).compact();
	}

	public String extractUsername(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}

	// Validating the token
	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	// Checking if the token has expired
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	// Extracting the expiration date from the token
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	// Creating a new token
	public String createToken(String username) {
		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Token expires after one hour
				.signWith(SignatureAlgorithm.HS256, key).compact();
	}

	// Interface for extracting data from the token
	@FunctionalInterface
	public interface ClaimsResolver<T> {
		T resolve(Claims claims);
	}

	// Extracting all data from the token
	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
	}

	// Extracting specific data from the token (such as subject and time)
	public <T> T extractClaim(String token, ClaimsResolver<T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.resolve(claims);
	}
}
