package com.example.booking.service;

import java.time.Instant;
import java.util.Optional;

import com.example.booking.domain.availableslot.AvailableSlot;
import com.example.booking.domain.reservation.Reservation;
import com.example.booking.domain.reservation.repository.ReservationRepository;
import com.example.booking.domain.user.User;
import com.example.booking.exception.AlreadyCanceledException;
import com.example.booking.exception.ReservationNotFoundException;
import com.example.booking.exception.SlotAlreadyReservedException;
import com.example.booking.exception.SlotNotFoundException;
import com.example.booking.exception.UserNotFoundException;
import com.example.booking.service.reservation.ReservationService;
import com.example.booking.service.reservation.impl.ReservationServiceImpl;
import com.example.booking.service.slot.AvailableSlotService;
import com.example.booking.service.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@Import({ ReservationServiceImpl.class })
class ReservationServiceTest {

	@MockBean
	AvailableSlotService availableSlotService;

	@MockBean
	ReservationRepository reservationRepo;

	@MockBean
	UserService userService;

	@Autowired
	ReservationService service;

	User user;

	AvailableSlot slot;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(1L);

		slot = new AvailableSlot();
		slot.setId(10L);
		slot.setReserved(false);
	}

	@Nested
	@DisplayName("reserve(slotId, userId)")
	class ReserveTests {

		@Test
		void reserve_success() {
			when(userService.getById(1L)).thenReturn(Optional.of(user));
			when(availableSlotService.getById(10L)).thenReturn(Optional.of(slot));
			when(availableSlotService.saveAndFlush(any(AvailableSlot.class))).thenAnswer(inv -> inv.getArgument(0));
			when(reservationRepo.save(any(Reservation.class))).thenAnswer(inv -> {
				Reservation r = inv.getArgument(0);
				r.setId(100L);
				r.setCreatedAt(Instant.now());
				return r;
			});

			var resp = service.reserve(10L, 1L);

			assertThat(resp).isNotNull();
			assertThat(resp.id()).isEqualTo(100L);
			assertThat(resp.slotId()).isEqualTo(10L);
			assertThat(resp.userId()).isEqualTo(1L);
			assertThat(resp.status()).isEqualTo(Reservation.Status.ACTIVE.name());
			verify(availableSlotService).saveAndFlush(argThat(s -> s.isReserved() && s.getId().equals(10L)));
			verify(reservationRepo).save(any(Reservation.class));
		}

		@Test
		void reserve_userNotFound() {
			when(userService.getById(1L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> service.reserve(10L, 1L))
					.isInstanceOf(UserNotFoundException.class)
					.hasMessageContaining("user.not.found.exception");

			verifyNoInteractions(availableSlotService, reservationRepo);
		}

		@Test
		void reserve_slotNotFound() {
			when(userService.getById(1L)).thenReturn(Optional.of(user));
			when(availableSlotService.getById(10L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> service.reserve(10L, 1L))
					.isInstanceOf(SlotNotFoundException.class)
					.hasMessageContaining("slot.not.found.exception");

			verify(reservationRepo, never()).save(any());
		}

		@Test
		void reserve_alreadyReserved() {
			when(userService.getById(1L)).thenReturn(Optional.of(user));
			AvailableSlot reservedSlot = new AvailableSlot();
			reservedSlot.setId(10L);
			reservedSlot.setReserved(true);
			when(availableSlotService.getById(10L)).thenReturn(Optional.of(reservedSlot));

			assertThatThrownBy(() -> service.reserve(10L, 1L))
					.isInstanceOf(SlotAlreadyReservedException.class)
					.hasMessageContaining("slot.already.reserved.exception");

			verify(availableSlotService, never()).saveAndFlush(any());
			verify(reservationRepo, never()).save(any());
		}

		@Test
		void reserve_optimisticLock_conflict() {
			when(userService.getById(1L)).thenReturn(Optional.of(user));
			when(availableSlotService.getById(10L)).thenReturn(Optional.of(slot));
			when(availableSlotService.saveAndFlush(any(AvailableSlot.class)))
					.thenThrow(new OptimisticLockingFailureException("version conflict"));

			assertThatThrownBy(() -> service.reserve(10L, 1L))
					.isInstanceOf(OptimisticLockingFailureException.class)
					.hasMessageContaining("concurrent");

			verify(reservationRepo, never()).save(any());
		}
	}

	@Nested
	@DisplayName("cancel(reservationId)")
	class CancelTests {

		@Test
		void cancel_success() {
			var res = new Reservation();
			res.setId(200L);
			res.setUser(user);
			res.setSlot(slot);
			res.setStatus(Reservation.Status.ACTIVE);

			when(reservationRepo.findById(200L)).thenReturn(Optional.of(res));
			when(availableSlotService.saveAndFlush(any(AvailableSlot.class))).thenAnswer(inv -> inv.getArgument(0));
			when(reservationRepo.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

			service.cancel(200L);

			assertThat(res.getStatus()).isEqualTo(Reservation.Status.CANCELLED);
			assertThat(res.getCancelledAt()).isNotNull();
			assertThat(slot.isReserved()).isFalse();
			verify(availableSlotService).saveAndFlush(argThat(s -> !s.isReserved()));
			verify(reservationRepo).save(argThat(r -> r.getStatus() == Reservation.Status.CANCELLED));
		}

		@Test
		void cancel_notFound() {
			when(reservationRepo.findById(999L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> service.cancel(999L))
					.isInstanceOf(ReservationNotFoundException.class)
					.hasMessageContaining("reservation.not.found.exception");

			verifyNoInteractions(availableSlotService);
		}

		@Test
		void cancel_alreadyCancelled_isIdempotent() {
			Reservation res = new Reservation();
			res.setId(201L);
			res.setUser(user);
			res.setSlot(slot);
			res.setStatus(Reservation.Status.CANCELLED);

			when(reservationRepo.findById(201L)).thenReturn(Optional.of(res));

			Assertions.assertThrows(AlreadyCanceledException.class, () -> service.cancel(201L));

			verify(availableSlotService, never()).saveAndFlush(any());
			verify(reservationRepo, never()).save(any());
		}

		@Test
		void cancel_slotStateChanged_conflict() {
			var res = new Reservation();
			res.setId(202L);
			res.setUser(user);
			res.setSlot(slot);
			res.setStatus(Reservation.Status.ACTIVE);

			when(reservationRepo.findById(202L)).thenReturn(Optional.of(res));
			when(availableSlotService.saveAndFlush(any(AvailableSlot.class)))
					.thenThrow(new OptimisticLockingFailureException("slot already reserved (concurrent)"));

			assertThatThrownBy(() -> service.cancel(202L))
					.isInstanceOf(OptimisticLockingFailureException.class)
					.hasMessageContaining("slot already reserved (concurrent)");

			verify(reservationRepo, never()).save(any());
		}
	}
}
