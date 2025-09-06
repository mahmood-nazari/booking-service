package com.example.booking.service.slot.impl;

import java.time.Instant;
import java.util.Optional;

import com.example.booking.api.dto.SlotDto;
import com.example.booking.api.dto.SlotsPageDto;
import com.example.booking.domain.availableslot.AvailableSlot;
import com.example.booking.domain.availableslot.repository.AvailableSlotRepository;
import com.example.booking.service.slot.AvailableSlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvailableSlotServiceImpl implements AvailableSlotService {
	private final AvailableSlotRepository slotRepo;

	@Cacheable(value = "freeSlots:v3",
			key = "T(java.util.Objects).hash(#from, #to, #pageable.pageNumber, #pageable.pageSize, #pageable.sort)",
			sync = true)
	public SlotsPageDto getFreeSlots(Instant from, Instant to, Pageable pageable) {
		Page<AvailableSlot> page = slotRepo.findFreeWithin(from, to, pageable);
		var content = page.map(s -> new SlotDto(s.getId(), s.getStartTime(), s.getEndTime()))
				.getContent();

		return new SlotsPageDto(
				content,
				page.getNumber(),
				page.getSize(),
				page.getTotalElements(),
				page.getTotalPages(),
				page.isLast()
		);
	}

	@Override
	public Optional<AvailableSlot> getById(Long id) {
		log.debug("going to get available slot by id: {}", id);
		return slotRepo.findById(id);
	}

	@Override
	public AvailableSlot saveAndFlush(AvailableSlot availableSlot) {
		log.info("saving available slot to DB with id: {}", availableSlot.getId());
		return slotRepo.saveAndFlush(availableSlot);
	}
}