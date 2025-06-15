/*
 * Copyright (c) 2022-2022 the original author or authors.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.example.psoft_22_23_project.exceptions;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.psoft_22_23_project.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Check https://www.baeldung.com/exception-handling-for-rest-with-spring
 * <p>
 * Based on https://github.com/Yoh0xFF/java-spring-security-example
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private final Logger logger = LogManager.getLogger();

	@ExceptionHandler(value = { org.hibernate.StaleObjectStateException.class, ConflictException.class })
	@ResponseStatus(HttpStatus.CONFLICT)
	protected ResponseEntity<Object> handleConflict(final HttpServletRequest request, final Exception ex) {
		logger.error("ConflictException {}\n", Utils.sanitize(request.getRequestURI()), ex);

		final Map<String, String> details = new HashMap<>();
		details.put("message", "Object was updated by another user");
		details.put("error", ex.getMessage());

		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiCallError<>("Conflict", details.entrySet()));
	}

	@ExceptionHandler({ ConstraintViolationException.class })
	@ResponseStatus(HttpStatus.CONFLICT)
	protected ResponseEntity<Object> handleConstraintViolation(final HttpServletRequest request,
			final ConstraintViolationException ex) {
		logger.error("ConstraintViolationException {}\n", Utils.sanitize(request.getRequestURI()), ex);

		final Map<String, String> details = new HashMap<>();
		details.put("message", "The identity of the object you tried to create is already in use");
		details.put("error", ex.getMessage());
		details.put("constraint", ex.getConstraintName());
		details.put("state", ex.getSQLState());

		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiCallError<>("Conflict", details.entrySet()));
	}

	@ExceptionHandler({ DataIntegrityViolationException.class })
	@ResponseStatus(HttpStatus.CONFLICT)
	protected ResponseEntity<Object> handleDataIntegrityViolation(final HttpServletRequest request,
			final DataIntegrityViolationException ex) {
		logger.error("DataIntegrityViolationException {}\n", Utils.sanitize(request.getRequestURI()), ex);

		final Map<String, String> details = new HashMap<>();
		details.put("message", "The identity of the object you tried to create is already in use");
		details.put("error", ex.getMessage());

		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiCallError<>("Conflict", details.entrySet()));
	}

	@ExceptionHandler({ IllegalArgumentException.class, NumberFormatException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<Object> handleIllegalArgument(final HttpServletRequest request,
			final IllegalArgumentException ex) {
		logger.error("BadRequestException {}\n", Utils.sanitize(request.getRequestURI()), ex);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ApiCallError<>("Bad Request", List.of(ex.getMessage())));
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ApiCallError<String>> handleNotFoundException(final HttpServletRequest request,
			final NotFoundException ex) {
		logger.error("NotFoundException {}\n", Utils.sanitize(request.getRequestURI()), ex);

		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiCallError<>("Not found", List.of(ex.getMessage())));
	}

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ApiCallError<String>> handleValidationException(final HttpServletRequest request,
			final ValidationException ex) {
		logger.error("ValidationException {}\n", Utils.sanitize(request.getRequestURI()), ex);

		return ResponseEntity.badRequest()
				.body(new ApiCallError<>("Bad Request: Validation Failed", List.of(ex.getMessage())));
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseEntity<ApiCallError<String>> handleAccessDeniedException(final HttpServletRequest request,
			final AccessDeniedException ex) {
		logger.error("handleAccessDeniedException {}\n", Utils.sanitize(request.getRequestURI()), ex);

		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(new ApiCallError<>("Access denied!", List.of(ex.getMessage())));
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ApiCallError<String>> handleInternalServerError(final HttpServletRequest request,
			final Exception ex) {
		String errorId = UUID.randomUUID().toString();
		logger.error("Erro interno [ID: {}] na URI {}: {}", errorId, Utils.sanitize(request.getRequestURI()), Utils.sanitize(ex.getMessage()), ex);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ApiCallError<>("Internal server error", List.of(ex.getMessage())));
	}

	/**
	 * Override Spring's default handling of HTTP 415 Unsupported Media Type errors
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
			org.springframework.web.HttpMediaTypeNotSupportedException ex,
			org.springframework.http.HttpHeaders headers,
			org.springframework.http.HttpStatusCode status,
			org.springframework.web.context.request.WebRequest request) {
		
		logger.warn("UnsupportedMediaType for URI {} - Content-Type: {}, Supported types: {}", 
				request.getDescription(false), 
				ex.getContentType(),
				ex.getSupportedMediaTypes());

		final Map<String, Object> details = new HashMap<>();
		details.put("message", "Content-Type is not supported for this endpoint");
		details.put("providedContentType", ex.getContentType() != null ? ex.getContentType().toString() : "null");
		details.put("supportedMediaTypes", ex.getSupportedMediaTypes());
		details.put("timestamp", System.currentTimeMillis());

		return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
				.body(new ApiCallError<>("Unsupported Media Type", details.entrySet()));
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ApiCallError<T> {

		private String message;
		private Collection<T> details;
	}
}
