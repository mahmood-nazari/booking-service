package com.example.booking.api.reservation;

import com.example.booking.api.dto.ReservationRequest;
import com.example.booking.api.dto.ReservationResponse;
import com.example.booking.api.reservation.mapper.ReservationControllerMapper;
import com.example.booking.security.AuthUserDetails;
import com.example.booking.service.reservation.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
	private final ReservationService reservationService;

	private final ReservationControllerMapper mapper;


	@PostMapping
	public ResponseEntity<ReservationResponse> reserve(@AuthenticationPrincipal AuthUserDetails userDetail, @Valid @RequestBody ReservationRequest request) {
		log.info("Reservation request received with slotId: {} and username: {}", request.slotId(), userDetail.getUsername());
		var result = reservationService.reserve(request.slotId(), userDetail.getId());
		log.info("reservation successfully done with slotId: {} and username: {}", request.slotId(), userDetail.getUsername());
		return ResponseEntity.ok(mapper.toReservationResponse(result));
	}


	@DeleteMapping("/{id}")
	public ResponseEntity<Long> cancel(@PathVariable Long id) {
		log.info("canceling reservation with id {}", id);
		return ResponseEntity.ok(reservationService.cancel(id));
	}
}