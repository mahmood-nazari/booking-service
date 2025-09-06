package com.example.booking.api.dto;

import com.example.booking.api.response.ResponseService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse extends ResponseService {

	private String token;

	private String username;
}