package com.example.projectwb.core;

import com.example.projectwb.adapter.in.web.dto.ErrorResponse;
import com.example.projectwb.adapter.out.persistence.entity.exception.AccountEntityNotFoundException;
import com.example.projectwb.domain.exception.AccountNotFoundException;
import com.example.projectwb.domain.exception.DailyLimitException;
import com.example.projectwb.domain.exception.InsufficientFundsException;
import com.example.projectwb.domain.exception.InvalidTransactionRequestException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex,
        HttpServletRequest request) {
        log.warn("AccountNotFoundException: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found",
            ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccountEntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountEntityNotFoundException(AccountNotFoundException ex,
        HttpServletRequest request) {
        log.warn("AccountEntityNotFoundException: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found",
            ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({InsufficientFundsException.class, DailyLimitException.class, InvalidTransactionRequestException.class})
    public ResponseEntity<ErrorResponse> handleBusinessRuleException(RuntimeException ex,
        HttpServletRequest request) {
        log.warn("BusinessRuleException: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
            "Bad Request", ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
        MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        log.warn("MethodArgumentNotValidException: {}", errorMessage);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
            "Bad Request", errorMessage, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 500 - 처리되지 않은 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex,
        HttpServletRequest request) {
        log.error("Unhandled Exception caught: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error", "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.", request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
