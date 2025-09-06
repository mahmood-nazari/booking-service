package com.example.booking.api.dto;

import java.io.Serializable;
import java.time.Instant;

public record SlotDto(Long id, Instant startTime, Instant endTime) implements Serializable {
	private static final long serialVersionUID = 1L;

}
