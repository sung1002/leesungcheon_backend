package com.example.projectwb.adapter.out.persistence;

import com.example.projectwb.domain.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CustomTransactionRepository {

    BigDecimal calculateDailySum(
        Long accountId,
        Transaction.TransactionType type,
        LocalDateTime startDate,
        LocalDateTime endDate
    );
}
