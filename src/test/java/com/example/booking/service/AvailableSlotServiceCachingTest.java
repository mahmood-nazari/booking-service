package com.example.booking.service;

import java.time.Instant;
import java.util.List;

import com.example.booking.domain.availableslot.AvailableSlot;
import com.example.booking.domain.availableslot.repository.AvailableSlotRepository;
import com.example.booking.service.slot.AvailableSlotService;
import com.example.booking.service.slot.impl.AvailableSlotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {
		AvailableSlotServiceImpl.class,
		AvailableSlotServiceCachingTest.TestCachingConfig.class,
		CacheAutoConfiguration.class
})
@ActiveProfiles("test")
class AvailableSlotServiceCachingTest {

	@Configuration
	@EnableCaching
	static class TestCachingConfig {
		@Bean
		CacheManager cacheManager() {
			return new ConcurrentMapCacheManager("freeSlots:v3");
		}
	}

	@Autowired
	AvailableSlotService service;

	@Autowired
	CacheManager cacheManager;

	@MockBean
	AvailableSlotRepository slotRepo;

	@BeforeEach
	void clearCacheAndResetMocks() {
		Cache cache = cacheManager.getCache("freeSlots:v3");
		if (cache != null) {
			cache.clear();
		}
		Mockito.reset(slotRepo);
	}

	@Test
	void cached_calls_sameParams_callRepoOnce() {
		Instant from = Instant.parse("2025-09-01T00:00:00Z");
		Instant to = Instant.parse("2025-09-30T23:59:59Z");
		Pageable p0 = PageRequest.of(0, 10, Sort.by("startTime").ascending());

		var s = new AvailableSlot();
		s.setId(1L);
		s.setStartTime(Instant.parse("2025-09-10T10:00:00Z"));
		s.setEndTime(Instant.parse("2025-09-10T10:30:00Z"));

		when(slotRepo.findFreeWithin(from, to, p0))
				.thenReturn(new PageImpl<>(List.of(s), p0, 1));

		var first = service.getFreeSlots(from, to, p0);
		var second = service.getFreeSlots(from, to, p0);

		verify(slotRepo, times(1)).findFreeWithin(from, to, p0);
		assertThat(second.content()).hasSize(1);
		assertThat(first).usingRecursiveComparison().isEqualTo(second);
	}

	@Test
	void change_pageable_changesCacheKey() {
		Instant from = Instant.parse("2025-09-01T00:00:00Z");
		Instant to = Instant.parse("2025-09-30T23:59:59Z");
		Pageable p0 = PageRequest.of(0, 10, Sort.by("startTime").ascending());
		Pageable p1 = PageRequest.of(1, 10, Sort.by("startTime").ascending());

		when(slotRepo.findFreeWithin(eq(from), eq(to), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(new AvailableSlot()), p0, 1));

		service.getFreeSlots(from, to, p0);
		service.getFreeSlots(from, to, p1);

		verify(slotRepo, times(1)).findFreeWithin(from, to, p0);
		verify(slotRepo, times(1)).findFreeWithin(from, to, p1);
		verifyNoMoreInteractions(slotRepo);
	}

}
