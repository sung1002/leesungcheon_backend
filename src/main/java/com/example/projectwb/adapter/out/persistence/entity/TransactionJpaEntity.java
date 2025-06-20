package com.example.projectwb.adapter.out.persistence.entity;

import com.example.projectwb.domain.Transaction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long sourceAccountId;

    @Column
    private Long targetAccountId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column
    private BigDecimal fee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Transaction.TransactionType type;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    public TransactionJpaEntity(Long sourceAccountId, Long targetAccountId, BigDecimal amount,
        BigDecimal fee, Transaction.TransactionType type, LocalDateTime transactionDate) {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.fee = fee;
        this.type = type;
        this.transactionDate = transactionDate;
    }
}
