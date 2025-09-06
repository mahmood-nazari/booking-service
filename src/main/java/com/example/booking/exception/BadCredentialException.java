package com.example.booking.exception;

import static com.example.booking.exception.ResultStatus.BAD_CREDENTIAL_EXCEPTION;

public class BadCredentialException extends BusinessException {


	public BadCredentialException(String errorMessage, ResultStatus resultStatus) {
		super(errorMessage, resultStatus);
	}

	@Override
	public ResultStatus getResultStatus() {
		return BAD_CREDENTIAL_EXCEPTION;
	}
}
