package com.example.booking.domain.availableslot.repository;

import java.time.Instant;

import com.example.booking.domain.availableslot.AvailableSlot;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvailableSlotRepository extends JpaRepository<AvailableSlot, Long> {

	@Query("select s from AvailableSlot s where s.reserved = false and (:from is null or s.startTime >= :from) and (:to is null or s.endTime <= :to)")
	Page<AvailableSlot> findFreeWithin(@Param("from") Instant from,
			@Param("to") Instant to,
			Pageable pageable);
}
