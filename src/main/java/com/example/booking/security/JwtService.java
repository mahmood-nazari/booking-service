package com.example.booking.security;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class JwtService {
	private final Key key;

	private final long expirationMinutes;

	public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-minutes}") long exp) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes()); this.expirationMinutes = exp;
	}

	public String generate(Long userId, String username) {
		log.info("going to generate toke for username: {}", username);
		Instant now = Instant.now(); Instant exp = now.plusSeconds(expirationMinutes * 60);
		return Jwts.builder()
				.setSubject(username)
				.addClaims(Map.of("uid", userId))
				.setIssuedAt(Date.from(now))
				.setExpiration(Date.from(exp))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

	public Jws<Claims> parse(String token) {
		log.info("going to parse token");
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
	}
}