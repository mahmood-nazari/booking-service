package com.example.booking.service;

import com.example.booking.domain.user.User;
import com.example.booking.domain.user.reposioty.UserRepository;
import com.example.booking.service.user.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	UserRepository userRepository;

	@InjectMocks
	UserServiceImpl userService;

	private User user;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(42L);
		user.setUsername("mahmood");
	}

	@Nested
	class GetByUsernameTests {

		@Test
		void getByUsername_found() {
			when(userRepository.findByUsername("mahmood")).thenReturn(Optional.of(user));

			Optional<User> result = userService.getByUsername("mahmood");

			assertThat(result).isPresent();
			assertThat(result.get().getId()).isEqualTo(42L);
			assertThat(result.get().getUsername()).isEqualTo("mahmood");

			verify(userRepository).findByUsername("mahmood");
			verifyNoMoreInteractions(userRepository);
		}

		@Test
		void getByUsername_notFound() {
			when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

			Optional<User> result = userService.getByUsername("ghost");

			assertThat(result).isEmpty();
			verify(userRepository).findByUsername("ghost");
			verifyNoMoreInteractions(userRepository);
		}
	}

	@Nested
	class GetByIdTests {

		@Test
		void getById_found() {
			when(userRepository.findById(42L)).thenReturn(Optional.of(user));

			Optional<User> result = userService.getById(42L);

			assertThat(result).isPresent();
			assertThat(result.get().getUsername()).isEqualTo("mahmood");

			verify(userRepository).findById(42L);
			verifyNoMoreInteractions(userRepository);
		}

		@Test
		void getById_notFound() {
			when(userRepository.findById(999L)).thenReturn(Optional.empty());

			Optional<User> result = userService.getById(999L);

			assertThat(result).isEmpty();
			verify(userRepository).findById(999L);
			verifyNoMoreInteractions(userRepository);
		}
	}
}
