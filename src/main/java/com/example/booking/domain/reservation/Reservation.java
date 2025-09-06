package com.example.booking.domain.reservation;

import java.time.Instant;

import com.example.booking.domain.availableslot.AvailableSlot;
import com.example.booking.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "reservations", indexes = {
		@Index(name = "idx_res_user", columnList = "user_id"),
		@Index(name = "idx_res_slot", columnList = "slot_id")
})
public class Reservation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id")
	private User user;


	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "slot_id")
	private AvailableSlot slot;


	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;


	@Column(name = "created_at", nullable = false)
	private Instant createdAt;


	@Column(name = "cancelled_at")
	private Instant cancelledAt;


	public enum Status {ACTIVE, CANCELLED}
}