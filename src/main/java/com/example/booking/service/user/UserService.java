package com.example.booking.service.user;

import java.util.Optional;

import com.example.booking.domain.user.User;

public interface UserService {

	Optional<User> getByUsername(String username);

	Optional<User> getById(Long id);
}
