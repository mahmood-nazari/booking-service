package com.example.booking.api.response;


import java.util.Map;

public class ErrorBody extends ResponseService {
	private Map<String, Object> details;

	public ErrorBody() {
	}

	public ErrorBody(Map<String, Object> details) {
		this.details = details;
	}

	public Map<String, Object> getDetails() {
		return details;
	}

	public void setDetails(Map<String, Object> details) {
		this.details = details;
	}
}

