package com.example.booking.api.dto;

import java.io.Serializable;
import java.util.List;

public record SlotsPageDto(
		List<SlotDto> content,
		int page,
		int size,
		long totalElements,
		int totalPages,
		boolean last
) implements Serializable {
	private static final long serialVersionUID = 1L;
}