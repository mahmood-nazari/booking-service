package com.example.booking.api;

import java.time.Instant;
import java.util.List;

import com.example.booking.api.dto.ReservationResponse;
import com.example.booking.api.reservation.ReservationController;
import com.example.booking.api.reservation.mapper.ReservationControllerMapper;
import com.example.booking.exception.AlreadyCanceledException;
import com.example.booking.exception.ReservationNotFoundException;
import com.example.booking.exception.ResultStatus;
import com.example.booking.exception.SlotAlreadyReservedException;
import com.example.booking.exception.SlotNotFoundException;
import com.example.booking.exception.UnifiedExceptionHandler;
import com.example.booking.security.AuthUserDetails;
import com.example.booking.security.JwtAuthFilter;
import com.example.booking.service.reservation.ReservationService;
import com.example.booking.service.reservation.model.ReservationResponseModel;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(UnifiedExceptionHandler.class)
class ReservationControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	ReservationService reservationService;

	@MockBean
	ReservationControllerMapper mapper;

	@MockBean
	JwtAuthFilter jwtAuthFilter;

	private static RequestPostProcessor withAuthPrincipal(AuthUserDetails principal) {
		return request -> {
			Authentication auth = new UsernamePasswordAuthenticationToken(principal, "N/A", List.of());
			request.setUserPrincipal(auth);
			SecurityContext context = SecurityContextHolder.createEmptyContext();
			context.setAuthentication(auth);
			SecurityContextHolder.setContext(context);
			request.getSession(true)
					.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
			return request;
		};
	}

	private AuthUserDetails principal(long id, String username) {
		AuthUserDetails p = mock(AuthUserDetails.class);
		when(p.getId()).thenReturn(id);
		when(p.getUsername()).thenReturn(username);
		return p;
	}

	@Test
	void reserve_success_200() throws Exception {
		var principal = principal(1L, "mahmood");

		var model = new ReservationResponseModel(
				100L, 10L, 1L, "ACTIVE",
				Instant.parse("2025-09-04T10:00:00Z"),
				null
		);

		var dto = new ReservationResponse();

		when(reservationService.reserve(10L, 1L)).thenReturn(model);
		when(mapper.toReservationResponse(model)).thenReturn(dto);

		mockMvc.perform(post("/api/reservations")
						.with(withAuthPrincipal(principal))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"slotId\":10}"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

		verify(reservationService).reserve(10L, 1L);
		verify(mapper).toReservationResponse(model);
		verifyNoMoreInteractions(reservationService, mapper);
	}

	@Test
	void reserve_invalid_400() throws Exception {
		var principal = principal(1L, "mahmood");

		mockMvc.perform(post("/api/reservations")
						.with(withAuthPrincipal(principal))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(reservationService, mapper);
	}

	@Test
	void reserve_slotNotFound_404() throws Exception {
		var principal = principal(2L, "user2");

		when(reservationService.reserve(10L, 2L))
				.thenThrow(new SlotNotFoundException(
						ResultStatus.SLOT_NOT_FOUND_EXCEPTION.getDescription(),
						ResultStatus.SLOT_NOT_FOUND_EXCEPTION));

		mockMvc.perform(post("/api/reservations")
				.with(withAuthPrincipal(principal))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"slotId\":10}"));

		verify(reservationService).reserve(10L, 2L);
		verifyNoInteractions(mapper);
	}

	@Test
	void reserve_conflict_409() throws Exception {
		var principal = principal(3L, "user3");

		when(reservationService.reserve(10L, 3L))
				.thenThrow(new SlotAlreadyReservedException(
						ResultStatus.SLOT_ALREADY_RESERVED_EXCEPTION.getDescription(),
						ResultStatus.SLOT_ALREADY_RESERVED_EXCEPTION));

		mockMvc.perform(post("/api/reservations")
				.with(withAuthPrincipal(principal))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"slotId\":10}"));

		verify(reservationService).reserve(10L, 3L);
		verifyNoInteractions(mapper);
	}

	@Test
	void cancel_success_200() throws Exception {
		when(reservationService.cancel(100L)).thenReturn(100L);

		mockMvc.perform(delete("/api/reservations/{id}", 100L))
				.andExpect(status().isOk())
				.andExpect(content().string("100"));

		verify(reservationService).cancel(100L);
		verifyNoMoreInteractions(reservationService);
	}

	@Test
	void cancel_alreadyCanceled_409() throws Exception {
		doThrow(new AlreadyCanceledException(
				ResultStatus.ALREADY_CANCELED_EXCEPTION.getDescription(),
				ResultStatus.ALREADY_CANCELED_EXCEPTION))
				.when(reservationService).cancel(200L);

		mockMvc.perform(delete("/api/reservations/{id}", 200L));

		verify(reservationService).cancel(200L);
	}

	@Test
	void cancel_conflict_409() throws Exception {
		doThrow(new OptimisticLockingFailureException("slot state changed"))
				.when(reservationService).cancel(300L);

		mockMvc.perform(delete("/api/reservations/{id}", 300L))
				.andExpect(status().isConflict());

		verify(reservationService).cancel(300L);
	}

	@Test
	void cancel_notFound_404() throws Exception {
		doThrow(new ReservationNotFoundException(ResultStatus.RESERVATION_NOT_FOUND_EXCEPTION.getDescription(),
				ResultStatus.RESERVATION_NOT_FOUND_EXCEPTION))
				.when(reservationService).cancel(404L);

		mockMvc.perform(delete("/api/reservations/{id}", 404L))
				.andExpect(status().isNotFound());

		verify(reservationService).cancel(404L);
	}

}
