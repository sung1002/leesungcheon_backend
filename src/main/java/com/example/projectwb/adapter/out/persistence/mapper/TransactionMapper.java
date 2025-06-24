package com.example.projectwb.adapter.out.persistence.mapper;

import com.example.projectwb.adapter.out.persistence.entity.TransactionJpaEntity;
import com.example.projectwb.domain.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public Transaction mapToDomain(TransactionJpaEntity entity) {
        return Transaction.builder()
            .id(entity.getId())
            .sourceAccountNumber(entity.getSourceAccountNumber())
            .targetAccountNumber(entity.getTargetAccountNumber())
            .amount(entity.getAmount())
            .fee(entity.getFee())
            .type(entity.getType())
            .transactionDate(entity.getTransactionDate())
            .build();
    }

    public TransactionJpaEntity mapToEntity(Transaction transaction) {
        return new TransactionJpaEntity(
            transaction.getSourceAccountNumber(),
            transaction.getTargetAccountNumber(),
            transaction.getAmount(),
            transaction.getFee(),
            transaction.getType(),
            transaction.getTransactionDate()
        );
    }
}
