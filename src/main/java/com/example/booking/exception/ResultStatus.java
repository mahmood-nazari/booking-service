package com.example.booking.exception;

import java.io.IOException;
import java.util.Properties;

public enum ResultStatus {
	// TODO: make a file named 'message.properties' and map descriptions to proper Farsi message
	SUCCESS(0, "success"),
	UNKNOWN(1, "unknown.error"),
	BAD_CREDENTIAL_EXCEPTION(2, "bad.credential.exception"),
	ALREADY_CANCELED_EXCEPTION(3, "already.closed.exception"),
	SLOT_NOT_FOUND_EXCEPTION(4, "slot.not.found.exception"),
	USER_NOT_FOUND_EXCEPTION(5, "user.not.found.exception"),
	SLOT_ALREADY_RESERVED_EXCEPTION(6, "slot.already.reserved.exception"),
	RESERVATION_NOT_FOUND_EXCEPTION(7, "reservation.not.found.exception"),
	FAILURE(1000, "failure");

	private final String description;

	private final Integer status;

	private ResultStatus(int status, String description) {
		this.status = status;
		String errorMsg = ResultStatus.MessageHolder.ERROR_MESSAGE_PROPERTIES.getProperty(description);
		this.description = errorMsg != null ? errorMsg : description;
	}

	public String getDescription() {
		return this.description;
	}

	public Integer getStatus() {
		return this.status;
	}

	private static final class MessageHolder {
		private static final Properties ERROR_MESSAGE_PROPERTIES = new Properties();

		private MessageHolder() {
		}

		static {
			try {
				ERROR_MESSAGE_PROPERTIES.load(ResultStatus.class.getResourceAsStream("/error-messages.properties"));
			} catch (IOException var1) {
				throw new ExceptionInInitializerError(var1);
			}
		}
	}
}

