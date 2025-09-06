package com.example.booking.exception;

public class AlreadyCanceledException extends BusinessException {
	public AlreadyCanceledException(String errorMessage, ResultStatus resultStatus) {
		super(errorMessage, resultStatus);
	}

	@Override
	public ResultStatus getResultStatus() {
		return ResultStatus.ALREADY_CANCELED_EXCEPTION;
	}
}
