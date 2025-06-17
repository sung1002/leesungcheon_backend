package com.example.projectwb.application.port.out;

import com.example.projectwb.domain.Transaction;
import com.example.projectwb.domain.Transaction.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface LoadTransactionPort {

    List<Transaction> findTransactionsByAccountId(Long accountId);

    BigDecimal getDailyTransactionSum(Long accountId, TransactionType transactionType,
        LocalDateTime startDate, LocalDateTime endDate);
}
