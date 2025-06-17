package com.example.projectwb.adapter.out.persistence;

import static com.example.projectwb.adapter.out.persistence.entity.QTransactionJpaEntity.transactionJpaEntity;

import com.example.projectwb.domain.Transaction;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomTransactionRepositoryImpl implements CustomTransactionRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public BigDecimal calculateDailySum(Long accountId, Transaction.TransactionType type,
        LocalDateTime startOfDay, LocalDateTime endOfDay) {

        BigDecimal sum = queryFactory
            .select(transactionJpaEntity.amount.sum())
            .from(transactionJpaEntity)
            .where(
                transactionJpaEntity.sourceAccountId.eq(accountId),
                transactionJpaEntity.type.eq(type),
                transactionJpaEntity.transactionDate.between(startOfDay, endOfDay)
            )
            .fetchOne();

        return sum == null ? BigDecimal.ZERO : sum;
    }
}
