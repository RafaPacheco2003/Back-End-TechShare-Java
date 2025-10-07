package com.techmate.techmate.exception;

import com.techmate.techmate.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.NOT_FOUND.value(), request.getRequestURI(), null,
                Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.toList());
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST.value(), request.getRequestURI(), "VALIDATION_ERROR", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.toList());
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST.value(), request.getRequestURI(), "VALIDATION_ERROR", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientStock(InsufficientStockException ex, HttpServletRequest request) {
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.CONFLICT.value(), request.getRequestURI(), "INSUFFICIENT_STOCK",
                Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        String code = ex.getCode();
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.UNPROCESSABLE_ENTITY.value(), request.getRequestURI(), code,
                Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.FORBIDDEN.value(), request.getRequestURI(), "ACCESS_DENIED",
                Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.UNAUTHORIZED.value(), request.getRequestURI(), "UNAUTHENTICATED",
                Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST.value(), request.getRequestURI(), "MALFORMED_JSON",
                Collections.singletonList("JSON inválido o cuerpo no parseable"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), ex);
        ApiErrorResponse body = ApiErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI(),
                "INTERNAL_ERROR",
                Collections.singletonList("Ha ocurrido un error interno")
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
