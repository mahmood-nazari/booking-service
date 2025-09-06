package com.example.booking.domain.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "users")
public class User {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	@Column(nullable = false, unique = true)
	private String username;


	@Column(nullable = false, unique = true)
	private String email;


	@Column(name = "password", nullable = false)
	private String passwordHash;


	@Column(name = "created_at")
	private Instant createdAt;
}
