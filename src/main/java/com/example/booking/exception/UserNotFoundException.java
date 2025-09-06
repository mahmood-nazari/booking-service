package com.example.booking.exception;

import static com.example.booking.exception.ResultStatus.USER_NOT_FOUND_EXCEPTION;

public class UserNotFoundException extends BusinessException {


	public UserNotFoundException(String errorMessage, ResultStatus resultStatus) {
		super(errorMessage, resultStatus);
	}

	@Override
	public ResultStatus getResultStatus() {
		return USER_NOT_FOUND_EXCEPTION;
	}
}
