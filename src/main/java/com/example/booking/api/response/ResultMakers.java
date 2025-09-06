package com.example.booking.api.response;

import com.example.booking.exception.ResultStatus;

public final class ResultMakers {
	private ResultMakers(){}

	public static void fill(ResponseService body, ResultStatus status) {
		body.setResult(status);
	}

	public static void fill(ResponseService body, ResultStatus status, String message) {
		body.setResult(status, message);
	}
}