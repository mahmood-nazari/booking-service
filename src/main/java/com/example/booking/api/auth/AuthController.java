package com.example.booking.api.auth;

import com.example.booking.api.auth.mapper.AuthControllerMapper;
import com.example.booking.api.dto.LoginRequest;
import com.example.booking.api.dto.LoginResponse;
import com.example.booking.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final AuthService authService;

	private final AuthControllerMapper mapper;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		log.info("login request received with username: {}", request.username());
		var login = authService.login(request.username(), request.password());
		var response = mapper.toLoginResponse(login.token(), login.username());
		log.info("login successful with username: {}", request.username());
		return ResponseEntity.ok(response);
	}
}