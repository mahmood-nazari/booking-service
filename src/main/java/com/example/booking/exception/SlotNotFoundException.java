package com.example.booking.exception;

import static com.example.booking.exception.ResultStatus.SLOT_NOT_FOUND_EXCEPTION;

public class SlotNotFoundException extends BusinessException {


	public SlotNotFoundException(String errorMessage, ResultStatus resultStatus) {
		super(errorMessage, resultStatus);
	}

	@Override
	public ResultStatus getResultStatus() {
		return SLOT_NOT_FOUND_EXCEPTION;
	}
}
