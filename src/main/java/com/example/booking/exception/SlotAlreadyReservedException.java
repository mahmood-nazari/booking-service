package com.example.booking.exception;

import static com.example.booking.exception.ResultStatus.SLOT_ALREADY_RESERVED_EXCEPTION;

public class SlotAlreadyReservedException extends BusinessException {


	public SlotAlreadyReservedException(String errorMessage, ResultStatus resultStatus) {
		super(errorMessage, resultStatus);
	}

	@Override
	public ResultStatus getResultStatus() {
		return SLOT_ALREADY_RESERVED_EXCEPTION;
	}
}
