package com.example.projectwb.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Transaction {

    private Long id;
    private String sourceAccountNumber;
    private String targetAccountNumber;
    private BigDecimal amount;
    private BigDecimal fee;
    private TransactionType type;
    private LocalDateTime transactionDate;

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER
    }
}
