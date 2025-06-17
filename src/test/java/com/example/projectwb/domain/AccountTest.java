package com.example.projectwb.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AccountTest {

    @Test
    @DisplayName("계좌에 성공적으로 입금한다.")
    void deposit_success() {
        // given
        Account account = new Account(1L, "1234-5678-9101", new BigDecimal("10000"), "이성천");
        BigDecimal amount = new BigDecimal("5000");

        // when
        account.deposit(amount);

        // then
        assertThat(account.getBalance()).isEqualTo(new BigDecimal("15000"));
    }

    @Test
    @DisplayName("계좌에서 성공적으로 출금한다.")
    void withdraw_success() {
        // given
        Account account = new Account(1L, "1234-5678-9101", new BigDecimal("10000"), "이성천");
        BigDecimal amount = new BigDecimal("3000");

        // when
        boolean result = account.withdraw(amount);

        // then
        assertThat(result).isTrue();
        assertThat(account.getBalance()).isEqualTo(new BigDecimal("7000"));
    }

    @Test
    @DisplayName("잔액보다 큰 금액을 출금하면 실패한다.")
    void withdraw_fail_insufficient_funds() {
        // given
        Account account = new Account(1L, "1234-5678-9101", new BigDecimal("10000"), "홍길동");
        BigDecimal amount = new BigDecimal("12000");

        // when
        boolean result = account.withdraw(amount);

        // then
        assertThat(result).isFalse();
        assertThat(account.getBalance()).isEqualTo(new BigDecimal("10000")); // 잔액은 그대로여야 함
    }
}