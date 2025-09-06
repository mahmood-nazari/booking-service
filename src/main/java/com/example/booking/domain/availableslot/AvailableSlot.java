package com.example.booking.domain.availableslot;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "available_slots",
		indexes = {
				@Index(name = "idx_slots_reserved_start", columnList = "is_reserved,start_time"),
				@Index(name = "idx_slots_reserved_end", columnList = "is_reserved,end_time")
		})
public class AvailableSlot {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	@Column(name = "start_time", nullable = false)
	private Instant startTime;


	@Column(name = "end_time", nullable = false)
	private Instant endTime;


	@Column(name = "is_reserved", nullable = false)
	private boolean reserved;


	@Version
	private Long version;
}
