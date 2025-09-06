package com.example.booking.domain.user.reposioty;

import java.util.Optional;

import com.example.booking.domain.user.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
}
