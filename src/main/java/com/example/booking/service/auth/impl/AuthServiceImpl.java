package com.example.booking.service.auth.impl;

import com.example.booking.domain.user.User;
import com.example.booking.exception.BadCredentialException;
import com.example.booking.exception.ResultStatus;
import com.example.booking.security.JwtService;
import com.example.booking.service.auth.AuthService;
import com.example.booking.service.auth.model.LoginResponseModel;
import com.example.booking.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserService userService;

	private final PasswordEncoder passwordEncoder;

	private final JwtService jwtService;

	@Override
	public LoginResponseModel login(final String username, final String password) {
		final User user = userService.getByUsername(username)
				.orElseThrow(() -> new BadCredentialException(ResultStatus.BAD_CREDENTIAL_EXCEPTION.getDescription(),
						ResultStatus.BAD_CREDENTIAL_EXCEPTION));

		credentialValidation(password, user);

		final String token = jwtService.generate(user.getId(), user.getUsername());
		log.info("token generated successfully");
		return new LoginResponseModel(token, user.getUsername());
	}


	private void credentialValidation(String password, User user) {
		if (!passwordEncoder.matches(password, user.getPasswordHash())) {
			throw new BadCredentialException(ResultStatus.BAD_CREDENTIAL_EXCEPTION.getDescription(),
					ResultStatus.BAD_CREDENTIAL_EXCEPTION);
		}

	}
}
