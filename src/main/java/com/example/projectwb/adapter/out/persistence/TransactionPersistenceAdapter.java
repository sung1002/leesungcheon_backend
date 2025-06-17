package com.example.projectwb.adapter.out.persistence;

import com.example.projectwb.adapter.out.persistence.entity.TransactionJpaEntity;
import com.example.projectwb.adapter.out.persistence.mapper.TransactionMapper;
import com.example.projectwb.application.port.out.LoadTransactionPort;
import com.example.projectwb.application.port.out.RegisterTransactionPort;
import com.example.projectwb.domain.Transaction;
import com.example.projectwb.domain.Transaction.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TransactionPersistenceAdapter implements LoadTransactionPort, RegisterTransactionPort {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public List<Transaction> findTransactionsByAccountId(Long accountId) {
        List<TransactionJpaEntity> jpaEntities = transactionRepository
            .findBySourceAccountIdOrTargetAccountIdOrderByTransactionDateDesc(accountId, accountId);
        return jpaEntities.stream()
            .map(transactionMapper::mapToDomain)
            .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getDailyTransactionSum(Long accountId, TransactionType type,
        LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal sum = transactionRepository.calculateDailySum(accountId, type, startDate,
            endDate);
        return sum == null ? BigDecimal.ZERO : sum;
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        TransactionJpaEntity entity = transactionMapper.mapToEntity(transaction);
        transactionRepository.save(entity);
    }
}
