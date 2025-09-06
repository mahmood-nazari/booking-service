package com.example.booking.service.user.impl;

import java.util.Optional;

import com.example.booking.domain.user.User;
import com.example.booking.domain.user.reposioty.UserRepository;
import com.example.booking.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public Optional<User> getByUsername(String username) {
		log.info("going to get user by username {}", username);
		return userRepository.findByUsername(username);
	}

	@Override
	public Optional<User> getById(Long id) {
		log.info("going to get user by id {}", id);
		return userRepository.findById(id);
	}
}
