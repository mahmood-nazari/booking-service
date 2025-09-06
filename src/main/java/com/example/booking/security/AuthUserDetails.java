package com.example.booking.security;

import java.util.Collection;
import java.util.List;

import com.example.booking.domain.user.User;
import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Getter
public class AuthUserDetails implements UserDetails {
	private final Long id;

	private final String username;

	private final String password;

	private final List<GrantedAuthority> authorities;

	public AuthUserDetails(User u) {
		this.id = u.getId(); this.username = u.getUsername(); this.password = u.getPasswordHash();
		this.authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
