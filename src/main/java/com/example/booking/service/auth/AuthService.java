package com.example.booking.service.auth;

import com.example.booking.service.auth.model.LoginResponseModel;

public interface AuthService {

	LoginResponseModel login(String username, String password);

}
