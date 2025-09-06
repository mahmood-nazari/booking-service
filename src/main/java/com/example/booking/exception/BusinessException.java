package com.example.booking.exception;


import lombok.Getter;

public abstract class BusinessException extends RuntimeException {
	private static final long serialVersionUID = -3749766539158141005L;

	protected String errorCode;

	protected String errorMessage;

	@Getter
	protected ResultStatus resultStatus;

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(Throwable cause) {
		super(cause);
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessException(String errorMessage, ResultStatus resultStatus) {

		super(errorMessage);
		this.errorMessage = errorMessage;
		this.resultStatus = resultStatus;
	}

	public BusinessException(String errorCode, String errorMessage) {

		super(errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public BusinessException(String errorCode, String errorMessage, ResultStatus resultStatus) {

		super(errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.resultStatus = resultStatus;
	}

	public BusinessException(String errorCode, String errorMessage, ResultStatus resultStatus, String message,
			Throwable cause) {

		super(message, cause);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.resultStatus = resultStatus;
	}

	public BusinessException(ResultStatus resultStatus, String message) {
		super(message);
		this.resultStatus = resultStatus;
	}

	public BusinessException(ResultStatus resultStatus, String message, Throwable cause) {
		super(message, cause);
		this.resultStatus = resultStatus;
	}

}