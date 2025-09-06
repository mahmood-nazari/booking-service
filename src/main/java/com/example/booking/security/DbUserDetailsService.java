package com.example.booking.security;

import com.example.booking.domain.user.reposioty.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {
	private final UserRepository userRepo;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepo.findByUsername(username)
				.map(AuthUserDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("user not found"));
	}
}
