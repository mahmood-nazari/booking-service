package com.example.booking.service;


import java.util.Optional;

import com.example.booking.domain.user.User;
import com.example.booking.exception.BadCredentialException;
import com.example.booking.security.JwtService;
import com.example.booking.service.auth.impl.AuthServiceImpl;
import com.example.booking.service.auth.model.LoginResponseModel;
import com.example.booking.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	UserService userService;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	JwtService jwtService;

	@InjectMocks
	AuthServiceImpl authService;

	private User user;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(42L);
		user.setUsername("mahmood");
		user.setPasswordHash("$2a$10$hash");
	}

	@Test
	void login_success() {
		when(userService.getByUsername("mahmood")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("secret", user.getPasswordHash())).thenReturn(true);
		when(jwtService.generate(42L, "mahmood")).thenReturn("jwt-token");

		LoginResponseModel resp = authService.login("mahmood", "secret");

		assertThat(resp).isNotNull();
		assertThat(resp.token()).isEqualTo("jwt-token");
		assertThat(resp.username()).isEqualTo("mahmood");
		verify(userService).getByUsername("mahmood");
		verify(passwordEncoder).matches("secret", user.getPasswordHash());
		verify(jwtService).generate(42L, "mahmood");
	}

	@Test
	void login_userNotFound() {
		when(userService.getByUsername("ghost")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> authService.login("ghost", "x"))
				.isInstanceOf(BadCredentialException.class)
				.hasMessageContaining("bad.credential.exception");

		verify(userService).getByUsername("ghost");
		verifyNoInteractions(passwordEncoder, jwtService);
	}

	@Test
	void login_wrongPassword() {
		when(userService.getByUsername("mahmood")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("bad", user.getPasswordHash())).thenReturn(false);

		assertThatThrownBy(() -> authService.login("mahmood", "bad"))
				.isInstanceOf(BadCredentialException.class)
				.hasMessageContaining("bad.credential.exception");

		verify(userService).getByUsername("mahmood");
		verify(passwordEncoder).matches("bad", user.getPasswordHash());
		verifyNoInteractions(jwtService);
	}
}
