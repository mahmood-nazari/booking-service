package com.example.booking.exception;

import static com.example.booking.exception.ResultStatus.RESERVATION_NOT_FOUND_EXCEPTION;

public class ReservationNotFoundException extends BusinessException {


	public ReservationNotFoundException(String errorMessage, ResultStatus resultStatus) {
		super(errorMessage, resultStatus);
	}

	@Override
	public ResultStatus getResultStatus() {
		return RESERVATION_NOT_FOUND_EXCEPTION;
	}
}
