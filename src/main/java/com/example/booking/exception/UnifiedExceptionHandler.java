package com.example.booking.exception;


import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.booking.api.response.ErrorBody;
import com.example.booking.api.response.ResultMakers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class UnifiedExceptionHandler {

	/* ---------------- Helpers ---------------- */

	private static Map<String, Object> meta(HttpServletRequest req) {
		Map<String, Object> m = new LinkedHashMap<>();
		m.put("path", req.getRequestURI());
		m.put("timestamp", Instant.now().toString());
		return m;
	}

	private static ResponseEntity<ErrorBody> build(HttpStatus status, ResultStatus rs, String msg, Map<String, ?> details) {
		ErrorBody body = new ErrorBody(details != null ? new LinkedHashMap<>(details) : null);
		if (msg != null && !msg.isBlank()) {
			ResultMakers.fill(body, rs, msg);
		} else {
			ResultMakers.fill(body, rs);
		}
		return ResponseEntity.status(status).body(body);
	}

	/* -------------- 400 — Validation/Bad Request -------------- */

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorBody> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest req) {
		Map<String, String> fields = ex.getBindingResult().getFieldErrors()
				.stream()
				.collect(Collectors.toMap(
						FieldError::getField,
						fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid",
						(a, b) -> a,
						LinkedHashMap::new
				));
		Map<String, Object> details = new LinkedHashMap<>(meta(req));
		details.put("fields", fields);
		log.warn("400 Validation failed at {} -> {}", req.getRequestURI(), fields);
		return build(HttpStatus.BAD_REQUEST, ResultStatus.FAILURE, "validation failed", details);
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ErrorBody> handleBind(BindException ex, HttpServletRequest req) {
		Map<String, String> fields = ex.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(
						FieldError::getField,
						fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid",
						(a, b) -> a,
						LinkedHashMap::new
				));
		Map<String, Object> details = new LinkedHashMap<>(meta(req));
		details.put("fields", fields);
		log.warn("400 Bind validation at {} -> {}", req.getRequestURI(), fields);
		return build(HttpStatus.BAD_REQUEST, ResultStatus.FAILURE, "validation failed", details);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorBody> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
		Map<String, String> fields = ex.getConstraintViolations().stream()
				.collect(Collectors.toMap(
						v -> v.getPropertyPath().toString(),
						ConstraintViolation::getMessage,
						(a, b) -> a,
						LinkedHashMap::new
				));
		Map<String, Object> details = new LinkedHashMap<>(meta(req));
		details.put("fields", fields);
		log.warn("400 Constraint violations at {} -> {}", req.getRequestURI(), fields);
		return build(HttpStatus.BAD_REQUEST, ResultStatus.FAILURE, "validation failed", details);
	}

	@ExceptionHandler({
			HttpMessageNotReadableException.class,
			MissingServletRequestParameterException.class,
			IllegalArgumentException.class,
			IllegalStateException.class
	})
	public ResponseEntity<ErrorBody> handleBadRequest(Exception ex, HttpServletRequest req) {
		log.warn("400 Bad request at {} -> {}", req.getRequestURI(), ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, ResultStatus.FAILURE, ex.getMessage(), meta(req));
	}

	@ExceptionHandler({ BadCredentialException.class, AuthenticationException.class })
	public ResponseEntity<ErrorBody> handleAuth(AuthenticationException ex, HttpServletRequest req) {
		log.warn("401 Unauthorized at {} -> {}", req.getRequestURI(), ex.getMessage());
		return build(HttpStatus.UNAUTHORIZED, ResultStatus.BAD_CREDENTIAL_EXCEPTION, null, meta(req));
	}

	@ExceptionHandler({ SlotAlreadyReservedException.class })
	public ResponseEntity<ErrorBody> handleSlotAlreadyReserved(SlotAlreadyReservedException ex, HttpServletRequest req) {
		log.warn("slot already reserved {} -> {}", req.getRequestURI(), ex.getMessage());
		return build(HttpStatus.NOT_FOUND, ResultStatus.SLOT_ALREADY_RESERVED_EXCEPTION, null, meta(req));
	}

	@ExceptionHandler({ SlotNotFoundException.class })
	public ResponseEntity<ErrorBody> handleSlotNotFound(SlotNotFoundException ex, HttpServletRequest req) {
		log.warn("slot not found {} -> {}", req.getRequestURI(), ex.getMessage());
		return build(HttpStatus.NOT_FOUND, ResultStatus.SLOT_NOT_FOUND_EXCEPTION, null, meta(req));
	}

	@ExceptionHandler({ AlreadyCanceledException.class })
	public ResponseEntity<ErrorBody> handleAlreadyCanceled(AlreadyCanceledException ex, HttpServletRequest req) {
		log.warn("reservation already canceled {} -> {}", req.getRequestURI(), ex.getMessage());
		return build(HttpStatus.NOT_FOUND, ResultStatus.ALREADY_CANCELED_EXCEPTION, null, meta(req));
	}

	@ExceptionHandler({ UserNotFoundException.class })
	public ResponseEntity<ErrorBody> handleUserNotFound(UserNotFoundException ex, HttpServletRequest req) {
		log.warn("user not found {} -> {}", req.getRequestURI(), ex.getMessage());
		return build(HttpStatus.NOT_FOUND, ResultStatus.USER_NOT_FOUND_EXCEPTION, null, meta(req));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorBody> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
		log.warn("403 Forbidden at {} -> {}", req.getRequestURI(), ex.getMessage());
		return build(HttpStatus.FORBIDDEN, ResultStatus.FAILURE, "forbidden", meta(req));
	}

	@ExceptionHandler({ ReservationNotFoundException.class })
	public ResponseEntity<ErrorBody> handleNotFound(ReservationNotFoundException ex, HttpServletRequest req) {
		log.warn("404 Not found at {} -> {}", req.getRequestURI(), ex.getMessage());
		return build(HttpStatus.NOT_FOUND, ResultStatus.RESERVATION_NOT_FOUND_EXCEPTION, ex.getMessage(), meta(req));
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorBody> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
		log.warn("405 Method not allowed at {} -> {}", req.getRequestURI(), ex.getMessage());
		return build(HttpStatus.METHOD_NOT_ALLOWED, ResultStatus.FAILURE, ex.getMessage(), meta(req));
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ErrorBody> handleUnsupportedMedia(HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
		log.warn("415 Unsupported media at {} -> {}", req.getRequestURI(), ex.getMessage());
		return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ResultStatus.FAILURE, ex.getMessage(), meta(req));
	}

	@ExceptionHandler({ OptimisticLockingFailureException.class })
	public ResponseEntity<ErrorBody> handleConflict(Exception ex, HttpServletRequest req) {
		log.warn("409 Conflict at {} -> {}", req.getRequestURI(), ex.getMessage());
		return build(HttpStatus.CONFLICT, ResultStatus.FAILURE, ex.getMessage(), meta(req));
	}

	@ExceptionHandler(TransactionSystemException.class)
	public ResponseEntity<ErrorBody> handleTx(TransactionSystemException ex, HttpServletRequest req) {
		log.warn("422 TX validation at {} -> {}", req.getRequestURI(), ex.getMostSpecificCause().getMessage());
		return build(HttpStatus.UNPROCESSABLE_ENTITY, ResultStatus.FAILURE, "validation failed during persistence", meta(req));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorBody> handleAll(Exception ex, HttpServletRequest req) {
		log.error("500 at {} -> {}", req.getRequestURI(), ex.toString(), ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, ResultStatus.UNKNOWN, null, meta(req));
	}
}

