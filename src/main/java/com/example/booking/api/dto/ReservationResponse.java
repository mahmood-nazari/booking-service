package com.example.booking.api.dto;

import java.time.Instant;

import com.example.booking.api.response.ResponseService;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReservationResponse extends ResponseService {

	private Long id;

	private Long slotId;

	private Long userId;

	private String status;

	private Instant createdAt;

	private Instant cancelledAt;

}
