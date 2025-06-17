package com.example.projectwb.adapter.out.persistence.entity.exception;

public class AccountEntityNotFoundException extends RuntimeException {

    public AccountEntityNotFoundException() {
        super("Account entity가 존재하지 않습니다.");
    }
}
