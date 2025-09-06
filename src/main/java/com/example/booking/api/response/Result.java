package com.example.booking.api.response;

import java.io.Serializable;

import com.example.booking.exception.ResultStatus;

public class Result implements Serializable {
	private static final long serialVersionUID = 6091567334208093240L;

	private ResultStatus title;

	private int status;

	private String message;

	private ResultLevel level;

	public Result(ResultStatus title) {
		this.title = title;
		this.status = title.getStatus();
		this.message = title.getDescription();
		this.level = this.getResultLevel(title);
	}

	public Result(ResultStatus title, ResultLevel level) {
		this.title = title;
		this.status = title.getStatus();
		this.message = title.getDescription();
		this.level = level;
	}

	public ResultLevel getResultLevel(ResultStatus resultStatus) {
		if (resultStatus == ResultStatus.SUCCESS) {
			return ResultLevel.INFO;
		} else {
			return resultStatus == ResultStatus.FAILURE ? ResultLevel.BLOCKER : ResultLevel.WARN;
		}
	}

	public void setTitle(final ResultStatus title) {
		this.title = title;
	}

	public void setStatus(final int status) {
		this.status = status;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public void setLevel(final ResultLevel level) {
		this.level = level;
	}

	public ResultStatus getTitle() {
		return this.title;
	}

	public int getStatus() {
		return this.status;
	}

	public String getMessage() {
		return this.message;
	}

	public ResultLevel getLevel() {
		return this.level;
	}

	public String toString() {
		ResultStatus var10000 = this.getTitle();
		return "Result(title=" + var10000 + ", status=" + this.getStatus() + ", message=" + this.getMessage() + ", level=" + this.getLevel() + ")";
	}

	public Result() {
	}
}