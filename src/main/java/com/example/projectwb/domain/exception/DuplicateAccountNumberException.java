package com.example.projectwb.domain.exception;

public class DuplicateAccountNumberException extends RuntimeException {

    public DuplicateAccountNumberException(String message) {
        super(message);
    }
}
