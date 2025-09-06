package com.example.booking.service.slot;

import java.time.Instant;
import java.util.Optional;

import com.example.booking.api.dto.SlotsPageDto;
import com.example.booking.domain.availableslot.AvailableSlot;

import org.springframework.data.domain.Pageable;

public interface AvailableSlotService {

	SlotsPageDto getFreeSlots(Instant from, Instant to, Pageable pageable);

	Optional<AvailableSlot> getById(Long id);

	AvailableSlot saveAndFlush(AvailableSlot availableSlot);
}
