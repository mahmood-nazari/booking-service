package com.example.booking.service.reservation;

import com.example.booking.service.reservation.model.ReservationResponseModel;

public interface ReservationService {

	ReservationResponseModel reserve(Long slotId, Long userId);

	Long cancel(Long reservationId);
}
