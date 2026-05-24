package com.tmp.authservice.exception;

import com.tmp.authservice.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationErrors(
	        MethodArgumentNotValidException ex, HttpServletRequest request) {

	    String message = ex.getBindingResult().getFieldErrors()
	            .stream()
	            .map(FieldError::getDefaultMessage)
	            .collect(Collectors.joining(", "));

	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
	            new ErrorResponse(LocalDateTime.now(), 400, message, request.getRequestURI())
	    );
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(
	        IllegalArgumentException ex, HttpServletRequest request) {

	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
	            new ErrorResponse(LocalDateTime.now(), 400, ex.getMessage(), request.getRequestURI())
	    );
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(
	        AccessDeniedException ex, HttpServletRequest request) {

	    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
	            new ErrorResponse(LocalDateTime.now(), 403, "Access denied", request.getRequestURI())
	    );
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(
	        RuntimeException ex, HttpServletRequest request) {

	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
	            new ErrorResponse(LocalDateTime.now(), 404, ex.getMessage(), request.getRequestURI())
	    );
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(
	        Exception ex, HttpServletRequest request) {

	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	            new ErrorResponse(LocalDateTime.now(), 500, "An unexpected error occurred", request.getRequestURI())
	    );
	}
}