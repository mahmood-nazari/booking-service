package com.example.booking.service.reservation.model;

import java.time.Instant;

public record ReservationResponseModel(Long id, Long slotId, Long userId, String status, Instant createdAt,
									   Instant cancelledAt) {
}