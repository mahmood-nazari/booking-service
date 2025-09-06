package com.example.booking.service;

import com.example.booking.api.dto.SlotDto;
import com.example.booking.api.dto.SlotsPageDto;
import com.example.booking.domain.availableslot.AvailableSlot;
import com.example.booking.domain.availableslot.repository.AvailableSlotRepository;
import com.example.booking.service.slot.impl.AvailableSlotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailableSlotServiceTest {

	@Mock
	AvailableSlotRepository slotRepo;

	@InjectMocks
	AvailableSlotServiceImpl service;

	private Instant from;
	private Instant to;
	private Pageable pageable;

	@BeforeEach
	void setup() {
		from = Instant.parse("2025-09-01T00:00:00Z");
		to   = Instant.parse("2025-09-30T23:59:59Z");
		pageable = PageRequest.of(0, 2, Sort.by("startTime").ascending());
	}

	@Test
	void getFreeSlots_mapsCorrectly() {
		var s1 = new AvailableSlot();
		s1.setId(10L);
		s1.setStartTime(Instant.parse("2025-09-10T10:00:00Z"));
		s1.setEndTime(Instant.parse("2025-09-10T10:30:00Z"));

		var s2 = new AvailableSlot();
		s2.setId(11L);
		s2.setStartTime(Instant.parse("2025-09-10T11:00:00Z"));
		s2.setEndTime(Instant.parse("2025-09-10T11:30:00Z"));

		Page<AvailableSlot> page = new PageImpl<>(List.of(s1, s2), pageable, 5L);
		when(slotRepo.findFreeWithin(from, to, pageable)).thenReturn(page);

		SlotsPageDto dto = service.getFreeSlots(from, to, pageable);

		assertThat(dto).isNotNull();
		assertThat(dto.content()).hasSize(2);
		assertThat(dto.content().get(0))
				.extracting(SlotDto::id, SlotDto::startTime, SlotDto::endTime)
				.containsExactly(10L, s1.getStartTime(), s1.getEndTime());
		assertThat(dto.content().get(1).id()).isEqualTo(11L);

		assertThat(dto.totalElements()).isEqualTo(5L);
		assertThat(dto.totalPages()).isEqualTo(3);
		assertThat(dto.last()).isFalse();

		verify(slotRepo).findFreeWithin(from, to, pageable);
		verifyNoMoreInteractions(slotRepo);
	}

	@Test
	void getById_found() {
		var s = new AvailableSlot();
		s.setId(99L);
		when(slotRepo.findById(99L)).thenReturn(Optional.of(s));

		var result = service.getById(99L);

		assertThat(result).isPresent();
		assertThat(result.get().getId()).isEqualTo(99L);
		verify(slotRepo).findById(99L);
		verifyNoMoreInteractions(slotRepo);
	}

	@Test
	void getById_notFound() {
		when(slotRepo.findById(404L)).thenReturn(Optional.empty());

		var result = service.getById(404L);

		assertThat(result).isEmpty();
		verify(slotRepo).findById(404L);
		verifyNoMoreInteractions(slotRepo);
	}

	@Test
	void saveAndFlush_delegates() {
		var s = new AvailableSlot();
		s.setId(7L);
		when(slotRepo.saveAndFlush(s)).thenAnswer(inv -> inv.getArgument(0));

		var saved = service.saveAndFlush(s);

		assertThat(saved).isSameAs(s);
		verify(slotRepo).saveAndFlush(s);
		verifyNoMoreInteractions(slotRepo);
	}
}
