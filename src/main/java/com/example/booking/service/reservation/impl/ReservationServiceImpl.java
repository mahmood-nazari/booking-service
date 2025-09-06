package com.example.booking.service.reservation.impl;

import java.time.Instant;

import com.example.booking.domain.availableslot.AvailableSlot;
import com.example.booking.domain.reservation.Reservation;
import com.example.booking.domain.reservation.repository.ReservationRepository;
import com.example.booking.domain.user.User;
import com.example.booking.exception.AlreadyCanceledException;
import com.example.booking.exception.ReservationNotFoundException;
import com.example.booking.exception.ResultStatus;
import com.example.booking.exception.SlotAlreadyReservedException;
import com.example.booking.exception.SlotNotFoundException;
import com.example.booking.exception.UserNotFoundException;
import com.example.booking.service.reservation.ReservationService;
import com.example.booking.service.reservation.model.ReservationResponseModel;
import com.example.booking.service.slot.AvailableSlotService;
import com.example.booking.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.booking.exception.ResultStatus.RESERVATION_NOT_FOUND_EXCEPTION;
import static com.example.booking.exception.ResultStatus.SLOT_ALREADY_RESERVED_EXCEPTION;
import static com.example.booking.exception.ResultStatus.SLOT_NOT_FOUND_EXCEPTION;
import static com.example.booking.exception.ResultStatus.USER_NOT_FOUND_EXCEPTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

	private final AvailableSlotService availableSlotService;

	private final ReservationRepository reservationRepo;

	private final UserService userService;

	@Transactional
	@CacheEvict(value = "freeSlots:v3", allEntries = true)
	public ReservationResponseModel reserve(Long slotId, Long userId) {
		var user = getUser(userId);

		var slot = getSlot(slotId);

		validationForReservedSlot(slot);

		updateAvailableSlot(slot, true);

		var reservation = saveReservation(slot, user);

		return new ReservationResponseModel(reservation.getId(), slot.getId(), user.getId(),
				reservation.getStatus().name(), reservation.getCreatedAt(), reservation.getCancelledAt());
	}

	private Reservation saveReservation(AvailableSlot slot, User user) {
		var reservation = Reservation.builder()
				.slot(slot).user(user)
				.status(Reservation.Status.ACTIVE)
				.createdAt(Instant.now())
				.build();
		reservationRepo.save(reservation);
		return reservation;
	}

	private void updateAvailableSlot(AvailableSlot slot, Boolean reserve) {
		slot.setReserved(reserve);
		try {
			availableSlotService.saveAndFlush(slot);
		} catch (OptimisticLockingFailureException e) {
			throw new OptimisticLockingFailureException("slot already reserved (concurrent)");
		}
	}

	private static void validationForReservedSlot(AvailableSlot slot) {
		if (slot.isReserved()) {
			throw new SlotAlreadyReservedException(SLOT_ALREADY_RESERVED_EXCEPTION.getDescription(),
					SLOT_ALREADY_RESERVED_EXCEPTION);
		}
	}

	private AvailableSlot getSlot(Long slotId) {
		return availableSlotService.getById(slotId).orElseThrow(() ->
				new SlotNotFoundException(SLOT_NOT_FOUND_EXCEPTION.getDescription(), SLOT_NOT_FOUND_EXCEPTION));
	}

	private User getUser(Long userId) {
		return userService.getById(userId).orElseThrow(() ->
				new UserNotFoundException(USER_NOT_FOUND_EXCEPTION.getDescription(), USER_NOT_FOUND_EXCEPTION));
	}

	@Transactional
	@CacheEvict(value = "freeSlots:v3", allEntries = true)
	public Long cancel(Long reservationId) {
		var reservation = getReservation(reservationId);

		log.info("canceling reservation with model {}", reservation);
		validationForStatus(reservation);

		var slot = reservation.getSlot();

		updateAvailableSlot(slot, false);

		var savedReservation = getSavedReservation(reservation);
		log.info("reservation canceled with id {}", reservationId);
		return savedReservation.getId();
	}

	private Reservation getSavedReservation(Reservation reservation) {
		reservation.setStatus(Reservation.Status.CANCELLED);
		reservation.setCancelledAt(Instant.now());
		return reservationRepo.save(reservation);
	}

	private static void validationForStatus(Reservation reservation) {
		if (Reservation.Status.CANCELLED.equals(reservation.getStatus())) {
			throw new AlreadyCanceledException(ResultStatus.ALREADY_CANCELED_EXCEPTION.getDescription(),
					ResultStatus.ALREADY_CANCELED_EXCEPTION);
		}
	}

	private Reservation getReservation(Long reservationId) {
		return reservationRepo.findById(reservationId)
				.orElseThrow(() -> new ReservationNotFoundException(RESERVATION_NOT_FOUND_EXCEPTION.getDescription(),
						RESERVATION_NOT_FOUND_EXCEPTION));
	}
}