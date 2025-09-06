package com.example.booking.api.slot;

import java.time.Instant;

import com.example.booking.api.dto.SlotsPageDto;
import com.example.booking.service.slot.AvailableSlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
@Slf4j
public class SlotController {
	private final AvailableSlotService availableSlotService;


	@GetMapping
	public SlotsPageDto list(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "startTime") String sortBy,
			@RequestParam(defaultValue = "ASC") Sort.Direction direction,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
		log.info("going to get slots");
		Sort sort = Sort.by(direction, "endTime".equals(sortBy) ? "endTime" : "startTime");
		Pageable pageable = PageRequest.of(page, size, sort);
		return availableSlotService.getFreeSlots(from, to, pageable);
	}
}
