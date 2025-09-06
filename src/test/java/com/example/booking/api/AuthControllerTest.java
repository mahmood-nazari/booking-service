package com.example.booking.api;

import com.example.booking.api.auth.AuthController;
import com.example.booking.api.auth.mapper.AuthControllerMapper;
import com.example.booking.security.JwtAuthFilter;
import com.example.booking.service.auth.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthService authService;

	@MockBean
	private AuthControllerMapper mapper;

	@MockBean
	private JwtAuthFilter authFilter;


	@Test
	void login_invalidRequest_returns() throws Exception {
		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"username":"mahmood"}
								"""))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(authService, mapper);
	}

	@Test
	void login_badCredentials_returns() throws Exception {
		when(authService.login(anyString(), anyString()))
				.thenThrow(new BadCredentialsException("invalid credentials"));

		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"username":"mahmood","password":"bad"}
								"""))
				.andExpect(status().isUnauthorized());

		verify(authService).login("mahmood", "bad");
		verifyNoInteractions(mapper);
	}
}
